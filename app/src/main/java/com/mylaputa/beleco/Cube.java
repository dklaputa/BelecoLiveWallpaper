package com.mylaputa.beleco;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by dklap on 3/14/2017.
 */

public class Cube extends View {
    private final Paint mPaint = new Paint();
    private final Paint mPaintHide = new Paint();
    private float angleRange;
    private float xrot;
    private float yrot;
    private Point[] points;
    private Line[] lines;

    public Cube(Context context) {
        this(context, null, 0);
    }

    public Cube(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Cube(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Create a Paint to draw the lines for our cube
        mPaint.setColor(0xffffffff);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintHide.setColor(0x65ffffff);
        mPaintHide.setAntiAlias(true);
        mPaintHide.setStrokeWidth(2);
        mPaintHide.setStrokeCap(Paint.Cap.ROUND);
        mPaintHide.setStyle(Paint.Style.STROKE);
        angleRange = (float) Math.PI / 6;
        points = new Point[]{new Point(-1, -1, 1), new Point(-1, 1, 1), new Point(1, 1, 1),
                new Point(1, -1, 1), new Point(-1, -1, -1), new Point(-1, 1, -1), new Point(1, 1,
                -1), new Point(1, -1, -1)};
        lines = new Line[]{new Line(points[0], points[1]), new Line(points[1], points[2]), new
                Line(points[2], points[3]), new Line(points[3], points[0]), new Line(points[0],
                points[4]), new Line(points[1], points[5]), new Line(points[2], points[6]), new
                Line(points[3], points[7]), new Line(points[4], points[5]), new Line(points[5],
                points[6]), new Line(points[6], points[7]), new Line(points[7], points[4])};
    }

    public void setRotation(@FloatRange(from = 0.0, to = 1.0) float x, float y) {
        this.xrot = angleRange * x;
        this.yrot = angleRange * y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        int size = getMaxSize();
        for (Point p : points)
            p.projectPoint(size);
        findHidePoint();
        for (Line l : lines)
            l.draw(canvas);
        canvas.restore();
    }

    private int getMaxSize() {
        return Math.min(getWidth(), getHeight());
    }

    private void findHidePoint() {
        for (int i = 0; i < 4; i++) {
            Point tmp = points[i];
            if (tmp.getNewX() >= lines[8].getPosX(tmp.getNewY()) && tmp.getNewX() <= lines[10]
                    .getPosX(tmp.getNewY()) && tmp.getNewY() >= lines[11].getPosY(tmp.getNewX())
                    && tmp.getNewY() <= lines[9].getPosY(tmp.getNewX()))
                tmp.setHind(true);
            else tmp.setHind(false);
        }
    }

    private class Point {
        private float x, y, z;
        private float newX, newY, newZ;
        private boolean hind;

        Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        void projectPoint(int cof) {
            newY = (float) (Math.sin(xrot) * z + Math.cos(xrot) * y);
            newZ = (float) (Math.cos(xrot) * z - Math.sin(xrot) * y);
            // rotation around Y-axis
            newX = (float) (Math.sin(yrot) * newZ + Math.cos(yrot) * x);
            newZ = (float) (Math.cos(yrot) * newZ - Math.sin(yrot) * x);
            // 3D-to-2D projection
            newX = 3f * cof * newX / (10 + newZ);
            newY = 3f * cof * newY / (10 + newZ);
        }

        float getNewX() {
            return newX;
        }

        float getNewY() {
            return newY;
        }

        boolean isHind() {
            return hind;
        }

        void setHind(boolean hind) {
            this.hind = hind;
        }
    }

    private class Line {
        private Point start, end;

        Line(Point start, Point end) {
            this.start = start;
            this.end = end;
        }

        float getPosX(float posY) {
            return (start.getNewX() * posY - end.getNewX() * posY + start.getNewY() * end.getNewX
                    () - start.getNewX() * end.getNewY()) / (start.getNewY() - end.getNewY());
        }

        float getPosY(float posX) {
            return (start.getNewY() * posX - end.getNewY() * posX + start.getNewX() * end.getNewY
                    () - start.getNewY() * end.getNewX()) / (start.getNewX() - end.getNewX());
        }

        void draw(Canvas canvas) {
            if (start.isHind() || end.isHind())
                canvas.drawLine(start.getNewX(), start.getNewY(), end.getNewX(), end.getNewY(),
                        mPaintHide);
            else
                canvas.drawLine(start.getNewX(), start.getNewY(), end.getNewX(), end.getNewY(),
                        mPaint);
        }
    }
}