package com.mylaputa.beleco;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by dklap on 3/14/2017.
 */

public class Cube extends View {
    private final Paint mPaint = new Paint();
    private float xrot;
    private float yrot;

    public Cube(Context context) {
        this(context, null, 0);
    }

    public Cube(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cube(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Create a Paint to draw the lines for our cube
        final Paint paint = mPaint;
        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void setRotationAngle(float xrot, float yrot) {
        this.xrot = xrot;
        this.yrot = yrot;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw something
        drawCube(canvas);
        // Reschedule the next redraw
    }

    /*
     * Draw a wireframe cube by drawing 12 3 dimensional lines between
     * adjacent corners of the cube
     */
    void drawCube(Canvas c) {
        c.save();
        c.translate(getWidth() / 2, getHeight() / 2);
        int size = getMaxSize();
        drawLine(c, -1, -1, -1, 1, -1, -1, size);
        drawLine(c, 1, -1, -1, 1, 1, -1, size);
        drawLine(c, 1, 1, -1, -1, 1, -1, size);
        drawLine(c, -1, 1, -1, -1, -1, -1, size);
        drawLine(c, -1, -1, 1, 1, -1, 1, size);
        drawLine(c, 1, -1, 1, 1, 1, 1, size);
        drawLine(c, 1, 1, 1, -1, 1, 1, size);
        drawLine(c, -1, 1, 1, -1, -1, 1, size);
        drawLine(c, -1, -1, 1, -1, -1, -1, size);
        drawLine(c, 1, -1, 1, 1, -1, -1, size);
        drawLine(c, 1, 1, 1, 1, 1, -1, size);
        drawLine(c, -1, 1, 1, -1, 1, -1, size);
        c.restore();
    }

    private int getMaxSize() {
        return Math.min(getWidth(), getHeight());
    }

    /*
     * Draw a 3 dimensional line on to the screen
     */
    void drawLine(Canvas c, int x1, int y1, int z1, int x2, int y2, int z2, int cof) {
        // 3D transformations
        // rotation around X-axis
        float newy1 = (float) (Math.sin(xrot) * z1 + Math.cos(xrot) * y1);
        float newy2 = (float) (Math.sin(xrot) * z2 + Math.cos(xrot) * y2);
        float newz1 = (float) (Math.cos(xrot) * z1 - Math.sin(xrot) * y1);
        float newz2 = (float) (Math.cos(xrot) * z2 - Math.sin(xrot) * y2);
        // rotation around Y-axis
        float newx1 = (float) (Math.sin(yrot) * newz1 + Math.cos(yrot) * x1);
        float newx2 = (float) (Math.sin(yrot) * newz2 + Math.cos(yrot) * x2);
        newz1 = (float) (Math.cos(yrot) * newz1 - Math.sin(yrot) * x1);
        newz2 = (float) (Math.cos(yrot) * newz2 - Math.sin(yrot) * x2);
        // 3D-to-2D projection
        float startX = 3f * cof * newx1 / (10 + newz1);//range 3~4
        float startY = 3f * cof * newy1 / (10 + newz1);
        float stopX = 3f * cof * newx2 / (10 + newz2);
        float stopY = 3f * cof * newy2 / (10 + newz2);
        c.drawLine(startX, startY, stopX, stopY, mPaint);
    }
}