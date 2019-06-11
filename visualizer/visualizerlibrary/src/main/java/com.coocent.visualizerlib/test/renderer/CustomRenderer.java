package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import com.coocent.visualizerlib.utils.LogUtils;

import java.util.Random;

/**

 */
public class CustomRenderer extends Renderer {
    private Paint mPaint;
    private Paint mPoint;
    private Paint mEdgePath;
    private Paint mPaint2;
    private Random mRandom;
    private Interpolator mInterpolator = new CycleInterpolator(5);
    private int mDivisions = 1;

    private int radius = -1;
    protected int color = Color.BLUE;
    private float density = 50;
    private int gap;
    private float py = 0;
    private float[] mPoint2;
    private float[] mPoint3;
    private float[] mPoint4;

    public CustomRenderer() {
        super();
        mPaint = new Paint();
        mPaint.setStrokeWidth(10f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 255, 102, 204));
        //  paint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        // mPaint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.OUTER));
        //mPaint.setAlpha(255);
        // mPaint.setShadowLayer(20,5,5,Color.argb(255, 255, 102, 102));

        mEdgePath = new Paint();
        mEdgePath.setStrokeWidth(2f);
        mEdgePath.setAntiAlias(true);
        mEdgePath.setStyle(Paint.Style.STROKE);
        mEdgePath.setColor(Color.argb(205, 255, 192, 203));
        //mEdgePath.setMaskFilter(new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER));
        mEdgePath.setAlpha(255);

        mPoint = new Paint();
        mPoint.setStrokeWidth(5f);
        mPoint.setStrokeCap(Paint.Cap.ROUND);
        mPoint.setAntiAlias(true);
        mPoint.setColor(Color.argb(205, 255, 102, 204));
        mRandom = new Random();

        this.density = 50;
        this.gap = 4;

        mPaint2 = new Paint();
        mPaint2.setStrokeWidth(3f);
        mPaint2.setStrokeCap(Paint.Cap.ROUND);
        mPaint2.setAntiAlias(true);
        mPaint2.setColor(Color.argb(205, 255, 102, 203));
    }

    public void setDensity(float density) {
        if (this.density > 180) {
            this.gap = 1;
        } else {
            this.gap = 4;
        }
        this.density = density;
        if (density > 256) {
            this.density = 250;
            this.gap = 0;
        } else if (density <= 10) {
            this.density = 10;
        }
    }

    private int mDegree = 30;
    private int mAddDegree = 0;

    @Override
    public void onAudioRender(Canvas canvas, byte[] bytes, Rect rect) {
//        if (mPoint2 == null || mPoints.length < bytes.length * 4) {
//            mPoint2 = new float[bytes.length * 4];
//            mPoint3 = new float[bytes.length * 4];
//            mPoint4 = new float[bytes.length * 4];
//        }
//
//        if (radius == -1) {
//            radius = rect.height() < rect.width() ? rect.height() : rect.width();
//            radius = (int) (radius * 0.65 / 2);
//            double circumference = 2 * Math.PI * radius;
//            mPaint.setStrokeWidth((float) (circumference / 240));
//            //mPaint.setStyle(Paint.Style.STROKE);
//            // mPaint.setStrokeWidth(4);
//        }
//
//        double angle = 0;
//        mDegree += 2;
//        mAddDegree += 1;
//        py += 0.005;
//        float cx = rect.width() / 2 + (float) (mRadius * Math.cos(360 - 3.14 * Math.PI * py));
//        float cy = rect.height() / 2 + (float) (mRadius * Math.sin(360 - 3.14 * Math.PI * py));
//
//        for (int i = 0; i < 120; i++, angle += 3) {
//            int x = (int) Math.ceil(i * 8.5);
//            int t = ((byte) (Math.abs(bytes[x]) + 128)) * (rect.height() / 4) / 128;
//            //  int t = (Math.abs(data[x])-100) + mDefaultHeight;
//
//            mPoints[i * 4] = (float) (rect.width() / 2 + radius
//                    * Math.cos(Math.toRadians(angle + mAddDegree)));
//            mPoints[i * 4 + 1] = (float) (rect.height() / 2 + radius
//                    * Math.sin(Math.toRadians(angle + mAddDegree)));
//            mPoints[i * 4 + 2] = (float) (rect.width() / 2 + (radius + 5)
//                    * Math.cos(Math.toRadians(angle + mAddDegree)));
//            mPoints[i * 4 + 3] = (float) (rect.height() / 2 + (radius + 5)
//                    * Math.sin(Math.toRadians(angle + mAddDegree)));
//
//
//            mPoint2[i * 4] = (float) (rect.width() / 2
//                    + radius
//                    * Math.cos(Math.toRadians(angle + mDegree)));
//            mPoint2[i * 4 + 1] = (float) (rect.height() / 2
//                    + radius
//                    * Math.sin(Math.toRadians(angle + mDegree)));
//            mPoint2[i * 4 + 2] = (float) (rect.width() / 2
//                    + (radius + 30)
//                    * Math.cos(Math.toRadians(angle + mDegree)));
//            mPoint2[i * 4 + 3] = (float) (rect.height() / 2
//                    + (radius + 30)
//                    * Math.sin(Math.toRadians(angle + mDegree)));
//
//            mPoint3[i * 4] = (float) (rect.width() / 2 + radius*3/4 * Math.cos(Math.toRadians(angle)));
//            mPoint3[i * 4 + 1] = (float) (rect.height() / 2 + radius*3/4 * Math.sin(Math.toRadians(angle)));
//            mPoint3[i * 4 + 2] = (float) (rect.width() / 2 + (radius*3/4 + t) * Math.cos(Math.toRadians(angle)));
//            mPoint3[i * 4 + 3] = (float) (rect.height() / 2 + (radius*3/4 + t) * Math.sin(Math.toRadians(angle)));
//
//            mPoint4[i * 4] = (float) (rect.width() / 2 + radius*3/6 * Math.cos(Math.toRadians(angle+mDegree)));
//            mPoint4[i * 4 + 1] = (float) (rect.height() / 2 + radius*3/6 * Math.sin(Math.toRadians(angle+mDegree)));
//            mPoint4[i * 4 + 2] = (float) (rect.width() / 2 + (radius*3/6 + 10) * Math.cos(Math.toRadians(angle+mDegree)));
//            mPoint4[i * 4 + 3] = (float) (rect.height() / 2 + (radius*3/6 + 10) * Math.sin(Math.toRadians(angle+mDegree)));
//        }
//        canvas.drawLines(mPoint3,mEdgePath);
//        canvas.drawLines(mPoints, mPaint);
//        canvas.drawLines(mPoint2, mEdgePath);
//        canvas.drawLines(mPoint4,mPaint2);
    }


    private double mRadius;
    private float colorCounter = 0;
    float modulation = 0;
    float modulationStrength = 0.4f; // 0-1
    float angleModulation = 0;
    float aggresive = 0.4f;

    private float[] toPolar(float[] cartesian, Rect rect, float size) {
        double cX = rect.width() / 2;
        double cY = rect.height() / 2;
        double angle = (cartesian[0]) * 2 * Math.PI;
        mRadius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2) / size;
        float[] out = {
                (float) (cX + mRadius * Math.sin(angle + angleModulation)),
                (float) (cY + mRadius * Math.cos(angle + angleModulation))
        };
        return out;
    }


    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < data.length - 1 / mDivisions; i++) {
            byte rfk = data[mDivisions * i];
            byte ifk = data[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            float dbValue = 75 * (float) Math.log10(magnitude);
            if (dbValue > 220) {
                dbValue = 220;
            } else if (dbValue < 0) {
                dbValue = 0;
            }
            float value = 20 + dbValue;
            float[] cartPoint = {
                    (float) (i * mDivisions) / (data.length - 1),
                    rect.height() / 2 - value / 100
            };

            float[] polarPoint = toPolar(cartPoint, rect, 5f);
            mFFTPoints[i * 4] = polarPoint[0];
            mFFTPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {
                    (float) (i * mDivisions) / (data.length - 1),
                    rect.height() / 2 + value
            };

            float[] polarPoint2 = toPolar(cartPoint2, rect, 5f);
            mFFTPoints[i * 4 + 2] = polarPoint2[0];
            mFFTPoints[i * 4 + 3] = polarPoint2[1];
        }
        canvas.drawLines(mFFTPoints, mEdgePath);
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, 100, mEdgePath);


    }

    public int getAlpha() {
        return (int) ((1.0f - mInterpolator.getInterpolation((float) mRadius)) * 255);
    }

    private float getSize() {
        BounceInterpolator interpolator = new BounceInterpolator();
        float size = interpolator.getInterpolation((float) mRadius / 50);
        LogUtils.e("nsc", " getSize=" + size);
        return size;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }
}
