package com.mylaputa.beleco;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class LiveWallpaperRenderer implements GLSurfaceView.Renderer {
    private final static int TRANSITION_STEP = 4;
    private final static float MAX_BIAS_RANGE = 0.01f;
    private final static String TAG = "LiveWallpaperRenderer";
    private final Handler mHandler = new Handler();
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    /**
     * Square instance
     */
    private Wallpaper wallpaper;
    private float offsetStepX = 0f;
    private float offsetX = 0.5f;// , offsetY = 0.5f;
    private float scrollOffsetX = 0.5f;// , offsetY = 0.5f;
    private float tinyOffsetX, tinyOffsetY;
    private float currentOffsetX, currentOffsetY;
    private float goalOffsetX, goalOffsetY;
    // private int count;
    private boolean isGoalChanged;
    // private final Context mContext;
    private Callbacks mCallbacks;
    // private float aspectRatio;
    private float screenAspectRatio;
    private int screenH;
    private float wallpaperAspectRatio;
    private float biasRange;
    private float scrollRange;
    private boolean scrollMode = true;
    private int delay = 3;
    private final Runnable transition = new Runnable() {
        @Override
        public void run() {
            transitionCal();
        }
    };
    // private final float angleRangeS = 45;
    // private final float angleRangeL = 135;
    // private String wallpaperPath;
    private boolean needsRefreshWallpaper;
    private int isDefaultWallpaper;
    private float preA;
    private float preB;

    // private int width, height;
    // int i = 0;

    public LiveWallpaperRenderer(Callbacks callbacks) {

        // mContext = context;
        mCallbacks = callbacks;
    }

    public void release() {
        // TODO stuff to release
        // stopTransition();
        if (wallpaper != null)
            wallpaper.destroy();
        mHandler.removeCallbacksAndMessages(null);
        mCallbacks = null;
    }

    /**
     * The Surface is created/init()
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_BLEND);
        // GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
        // GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,
                GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        Wallpaper.initGl();
    }

    public void stopTransition() {
        mHandler.removeCallbacks(transition);
    }

    /**
     * Here we do our drawing
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // if (i == 100) {
        // Log.i("renderer", "onDrawFrame");
        // i = 0;
        // } else {
        // i++;
        // }
        // Log.d("renderer", "onDrawFrame");
        if (needsRefreshWallpaper) {
            loadTexture();
            needsRefreshWallpaper = false;
            // preCalculate();
        }
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, preA * (-2.0f * scrollOffsetX + 1f)
                        + currentOffsetX, currentOffsetY, preB, preA
                        * (-2.0f * scrollOffsetX + 1f) + currentOffsetX,
                currentOffsetY, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        wallpaper.draw(mMVPMatrix);

    }

    private final void preCalculate() {
        if (offsetStepX > 0) {
            if (wallpaperAspectRatio > (1 + 1 / (3 * offsetStepX))
                    * screenAspectRatio) {
                // Log.d(TAG, "11");
                scrollRange = 1 + 1 / (3 * offsetStepX);
            } else if (wallpaperAspectRatio >= screenAspectRatio) {
                // Log.d(TAG, "12");
                scrollRange = wallpaperAspectRatio / screenAspectRatio;
            } else {
                // Log.d(TAG, "13");
                scrollRange = 1;
            }
        } else {
            scrollRange = 1;
        }
        // ------------------------------------------------------
        preA = screenAspectRatio * (scrollRange - 1);
        // preB = -1f;
        if (screenAspectRatio < 1)
            preB = -1.0f + biasRange / screenAspectRatio;
        else
            preB = -1.0f + biasRange * screenAspectRatio;
    }

    /**
     * If the surface changes, reset the view
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) { // Prevent A Divide By Zero By
            height = 1; // Making Height Equal One
        }
        // Log.d(TAG, "onSurfaceChanged, width = " + width + ", height = "
        // + height);

        screenAspectRatio = (float) width / (float) height;
        screenH = height;
        needsRefreshWallpaper = true;
        // preCalculate();
        // aspectRatio = mCallbacks.getWallpaperDesiredAspectRatio();
        // Log.d("renderer", "aspectRatio = " + aspectRatio);

        GLES20.glViewport(0, 0, width, height);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -0.1f * screenAspectRatio,
                0.1f * screenAspectRatio, -0.1f, 0.1f, 0.1f, 2);

    }

    public final void setOffset(float offsetX, float offsetY) {
        if (scrollMode) {
            this.offsetX = offsetX;
            scrollOffsetX = offsetX;
            mCallbacks.requestRender();
        } else {
            this.offsetX = offsetX;
        }
    }

    public final void setOffsetStep(float offsetStepX, float offsetStepY) {

        // if (offsetStepX < 0.0000001f) {
        // this.offsetStepX = 0.0000001f;
        // } else {
        // this.offsetStepX = offsetStepX;
        //
        // }
        //
        // Log.d(TAG, "offsetStepX = " + offsetStepX);
        if (this.offsetStepX != offsetStepX) {

            this.offsetStepX = offsetStepX;
            preCalculate();
            // needsRefreshWallpaper = true;
        }
    }

    public final void setOffsetMode(boolean scrollMode) {
        this.scrollMode = scrollMode;
        if (scrollMode)
            scrollOffsetX = offsetX;
        else
            scrollOffsetX = 0.5f;
        mCallbacks.requestRender();
    }

    public final void setOrientationAngle(float roll, float pitch) {
        // Log.d("tinyOffset", tinyOffsetX + ", " + tinyOffsetY);
        goalOffsetX = (float) (biasRange * Math.sin(Math.toRadians(roll)));
        goalOffsetY = (float) (biasRange * Math.sin(Math.toRadians(pitch)));
        // Log.i("onSensorChanged", roll + ", " + goalOffsetX + "; " + pitch
        // + ", " + goalOffsetY);
        // goalOffsetX = biasRange * angle2Length(roll);
        // goalOffsetY = -biasRange * angle2Length(pitch);
        isGoalChanged = true;
        // count = 0;
        stopTransition();
        mHandler.post(transition);
    }

    public final void setIsDefaultWallpaper(int isDefault) {
        // Log.d("tinyOffset", tinyOffsetX + ", " + tinyOffsetY);
        this.isDefaultWallpaper = isDefault;
        needsRefreshWallpaper = true;
        // mCallbacks.requestRender();
    }

    public final void setBiasRange(int multiples) {
        // Log.d("tinyOffset", tinyOffsetX + ", " + tinyOffsetY);
        biasRange = multiples * MAX_BIAS_RANGE + 0.03f;
        // mCallbacks.requestRender();
        preCalculate();
    }

    public final void setDelay(int delay) {
        this.delay = delay;
    }

    private final void transitionCal() {
        // Log.d("transitionCal", "transition");
        stopTransition();
        if (Math.abs(currentOffsetX - goalOffsetX) < .0000001
                && Math.abs(currentOffsetY - goalOffsetY) < .0000001)
            return;
        if (isGoalChanged) {
            tinyOffsetX = (goalOffsetX - currentOffsetX)
                    / (delay * TRANSITION_STEP);
            tinyOffsetY = (goalOffsetY - currentOffsetY)
                    / (delay * TRANSITION_STEP);
            isGoalChanged = false;
        }
        currentOffsetX += tinyOffsetX;
        currentOffsetY += tinyOffsetY;
        mCallbacks.requestRender();

        mHandler.postDelayed(transition, LiveWallpaperService.SENSOR_DELAY_US
                / (TRANSITION_STEP * 1000));

    }

    private final void loadTexture() {
        // Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (isDefaultWallpaper == 2) {
                is = mCallbacks.openAssets();
            } else {
                is = mCallbacks.openCustom();
            }
            if (wallpaper != null)
                wallpaper.destroy();
            wallpaper = new Wallpaper(cropBitmap(is));
            preCalculate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        System.gc();
        Log.d(TAG, "loadTexture");
    }

    private final Bitmap cropBitmap(InputStream is) {
        // BitmapFactory.Options opt = new BitmapFactory.Options();
        // opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // opt.inPurgeable = true;
        // opt.inInputShareable = true;
        Bitmap src = BitmapFactory.decodeStream(is);
        final float width = src.getWidth();
        final float height = src.getHeight();
        wallpaperAspectRatio = width / height;
        // if (offsetStepX > 0) {
        // if (ratio > (1 + 1 / (3 * offsetStepX)) * aspectRatio) {
        // // Log.d(TAG, "11");
        // scrollRange = 1 + 1 / (3 * offsetStepX);
        // Bitmap result = Bitmap.createBitmap(src, (int) (width - height
        // * scrollRange * aspectRatio) / 2, 0, (int) (height
        // * scrollRange * aspectRatio), (int) height);
        // src.recycle();
        // // src = null;
        // return result;
        // } else if (ratio >= aspectRatio) {
        // // Log.d(TAG, "12");
        // scrollRange = ratio / aspectRatio;
        // return src;
        // } else {
        // // Log.d(TAG, "13");
        // scrollRange = 1;
        // Bitmap result = Bitmap.createBitmap(src, 0,
        // (int) (height - width / aspectRatio) / 2, (int) width,
        // (int) (width / aspectRatio));
        // src.recycle();
        // // src = null;
        // return result;
        // }
        // } else {
        // scrollRange = 1;
        // if (ratio > aspectRatio) {
        // // Log.d(TAG, "21");
        // Bitmap result = Bitmap.createBitmap(src, (int) (width - height
        // * aspectRatio) / 2, 0, (int) (height * aspectRatio),
        // (int) height);
        // src.recycle();
        // // src = null;
        // return result;
        // } else if (ratio < aspectRatio) {
        // // Log.d(TAG, "22");
        // Bitmap result = Bitmap.createBitmap(src, 0,
        // (int) (height - width / aspectRatio) / 2, (int) width,
        // (int) (width / aspectRatio));
        // src.recycle();
        // // src = null;
        // return result;
        // } else {
        // // Log.d(TAG, "23");
        // return src;
        // }
        // }
        if (wallpaperAspectRatio < screenAspectRatio) {
            scrollRange = 1;
            Bitmap tmp = Bitmap.createBitmap(src, 0, (int) (height - width
                            / screenAspectRatio) / 2, (int) width,
                    (int) (width / screenAspectRatio));
            src.recycle();
            // src = null;
            // return result;
            if (tmp.getHeight() > 1.1 * screenH) {
                Bitmap result = Bitmap.createScaledBitmap(tmp,
                        (int) (1.1 * screenH * screenAspectRatio),
                        (int) (1.1 * screenH), true);
                tmp.recycle();
                return result;
            } else
                return tmp;
        } else {
            if (src.getHeight() > 1.1 * screenH) {
                Bitmap result = Bitmap.createScaledBitmap(src,
                        (int) (1.1 * screenH * wallpaperAspectRatio),
                        (int) (1.1 * screenH), true);
                src.recycle();
                return result;
            } else
                return src;
        }

    }

    interface Callbacks {
        // float getWallpaperDesiredAspectRatio();
        InputStream openAssets() throws IOException;

        InputStream openCustom() throws IOException;

        void requestRender();
    }

}
