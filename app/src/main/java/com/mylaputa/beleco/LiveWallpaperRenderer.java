package com.mylaputa.beleco;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;

import com.mylaputa.beleco.utils.Constant;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class LiveWallpaperRenderer implements GLSurfaceView.Renderer {
    private final static float MAX_BIAS_RANGE = 0.01f;
    private final static String TAG = "LiveWallpaperRenderer";
    private final Handler mHandler = new Handler();

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final Context mContext;

    private Wallpaper wallpaper;
    private float scrollStep = 0f;
    private float scrollOffsetXDup = 0.5f;// , offsetY = 0.5f;
    private float scrollOffsetX = 0.5f;// , offsetY = 0.5f;
    private float currentOffsetX, currentOffsetY;
    private float orientationOffsetX, orientationOffsetY;
    private int refreshRate = 60;
    private boolean noScroll = true;

    private float transitionStep = refreshRate / LiveWallpaperService.SENSOR_RATE;
    private Callbacks mCallbacks;

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
            long beginTimeMillis, timeTakenMillis, timeLeftMillis;
            beginTimeMillis = System.currentTimeMillis();
            transitionCal();
            timeTakenMillis = System.currentTimeMillis() - beginTimeMillis;
            timeLeftMillis = (1000L / refreshRate) - timeTakenMillis;

            // set some kind of minimum to prevent spinning
            if (timeLeftMillis < 5) {
                timeLeftMillis = 5; // Set a minimum
            }
            mHandler.postDelayed(this, timeLeftMillis);
        }
    };

    private int isDefaultWallpaper;
    private float preA;
    private float preB;

    LiveWallpaperRenderer(Context context, Callbacks callbacks) {

        mContext = context;
        mCallbacks = callbacks;
    }

    void release() {
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
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA,
                GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        Wallpaper.initGl();
    }

    void startTransition() {
        stopTransition();
        mHandler.post(transition);
    }

    void stopTransition() {
        mHandler.removeCallbacks(transition);
    }

    /**
     * Here we do our drawing
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        float x = preA * (-2 * scrollOffsetX + 1) + currentOffsetX;
        float y = currentOffsetY;
        Matrix.setLookAtM(mViewMatrix, 0, x, y, preB, x, y, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw square
        wallpaper.draw(mMVPMatrix);

    }

    private void preCalculate() {
        if (scrollStep > 0) {
            if (wallpaperAspectRatio > (1 + 1 / (3 * scrollStep))
                    * screenAspectRatio) {
                // Log.d(TAG, "11");
                scrollRange = 1 + 1 / (3 * scrollStep);
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

        screenAspectRatio = (float) width / (float) height;
        screenH = height;
        loadTexture();

        GLES20.glViewport(0, 0, width, height);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -0.1f * screenAspectRatio,
                0.1f * screenAspectRatio, -0.1f, 0.1f, 0.1f, 2);

    }

    void setOffset(float offsetX, float offsetY) {
        if (scrollMode) {
            scrollOffsetXDup = offsetX;
            scrollOffsetX = offsetX;
            noScroll = false;
        } else {
            this.scrollOffsetXDup = offsetX;
        }
    }

    void setOffsetStep(float offsetStepX, float offsetStepY) {
        if (scrollStep != offsetStepX) {
            scrollStep = offsetStepX;
            preCalculate();
        }
    }

    void setOffsetMode(boolean scrollMode) {
        this.scrollMode = scrollMode;
        if (scrollMode)
            scrollOffsetX = scrollOffsetXDup;
        else
            scrollOffsetX = 0.5f;
        mCallbacks.requestRender();
    }

    void setOrientationAngle(float roll, float pitch) {
        orientationOffsetX = (float) (biasRange * Math.sin(Math.toRadians(roll)));
        orientationOffsetY = (float) (biasRange * Math.sin(Math.toRadians(pitch)));
    }

    void setIsDefaultWallpaper(int isDefault) {
        isDefaultWallpaper = isDefault;
        loadTexture();
    }

    void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
        transitionStep = refreshRate / LiveWallpaperService.SENSOR_RATE;
    }

    void setBiasRange(int multiples) {
        // Log.d("tinyOffset", tinyOffsetX + ", " + tinyOffsetY);
        biasRange = multiples * MAX_BIAS_RANGE + 0.03f;
        // mCallbacks.requestRender();
        preCalculate();
    }

    void setDelay(int delay) {
        this.delay = delay;
    }

    private void transitionCal() {
        // Log.d("transitionCal", "transition");
//        stopTransition();
        if (Math.abs(currentOffsetX - orientationOffsetX) > .001
                || Math.abs(currentOffsetY - orientationOffsetY) > .001) {
//        Log.i(TAG,Math.abs(currentOffsetX - goalOffsetX)+" "+Math.abs(currentOffsetY - goalOffsetY));
            float tinyOffsetX = (orientationOffsetX - currentOffsetX)
                    / (delay * transitionStep);
            float tinyOffsetY = (orientationOffsetY - currentOffsetY)
                    / (delay * transitionStep);
            currentOffsetX += tinyOffsetX;
            currentOffsetY += tinyOffsetY;
            mCallbacks.requestRender();
            noScroll = true;
        } else if (!noScroll) {
            mCallbacks.requestRender();
            noScroll = true;
        }
    }

    private void loadTexture() {
        // Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (isDefaultWallpaper == 2) {
                is = mContext.getAssets().open(Constant.DEFAULT);
            } else {
                is = mContext.openFileInput(Constant.CACHE);
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

    private Bitmap cropBitmap(InputStream is) {
        Bitmap src = BitmapFactory.decodeStream(is);
        final float width = src.getWidth();
        final float height = src.getHeight();
        wallpaperAspectRatio = width / height;
        if (wallpaperAspectRatio < screenAspectRatio) {
            scrollRange = 1;
            Bitmap tmp = Bitmap.createBitmap(src, 0, (int) (height - width
                            / screenAspectRatio) / 2, (int) width,
                    (int) (width / screenAspectRatio));
            src.recycle();
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
        void requestRender();
    }

}
