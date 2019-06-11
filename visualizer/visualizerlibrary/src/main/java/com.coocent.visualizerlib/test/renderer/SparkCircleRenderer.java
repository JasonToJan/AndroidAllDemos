package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.test.renderer.particle.CustomSpark;

import java.util.Random;

/**

 */
public class SparkCircleRenderer extends Renderer {
    private Paint mPaint;
    private Paint mEdgePath;
    private Random mRandom;
    private Interpolator mInterpolator = new LinearInterpolator();
    private CustomSpark sparkManager;
    int[][] sparks = new int[100][10];
    private int mDivisions = 4;
    private int radius = -1;
    private int mDefaultHeight = 5;
    private float[] mPointsEdge;
    private int mRadiusEdge = 0;
    private Paint mPaint1;


    float modulation = 0;
    float mAmplitude = 0.83f;//调节跳动幅度
    float mCircleSize = 2.5f;//控制圈的大小
    float maxValue = 0;

    private int mColor;

    public SparkCircleRenderer(Context context,int color) {
        super();
        this.mColor = color;
      //  mCircleSize= context.getResources().getDimensionPixelSize(R.dimen.spark_circle_size)/10f;

        radius = 12;
        mRadiusEdge = radius + 20;
        mPaint = new Paint();
        mPaint.setStrokeWidth(5f);
        mPaint.setAntiAlias(true);
      //  mPaint.setColor(Color.argb(255, 255, 102, 204));

        if (color==-1){
            mPaint.setColor( Color.argb(255, 255, 102, 204));
        }else {
            mPaint.setColor(color);
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.OUTER));
        //mPaint.setAlpha(255);
        // mPaint.setShadowLayer(20,5,5,Color.argb(255, 255, 102, 102));

        mEdgePath = new Paint();
        mEdgePath.setStrokeWidth(2f);
        mEdgePath.setAntiAlias(true);
        mEdgePath.setAlpha(100);
        mEdgePath.setStrokeCap(Paint.Cap.ROUND);
        if (color==-1){
            mEdgePath.setColor(Color.argb(205, 255, 192, 203));
        }else {
            mEdgePath.setColor(color);
        }
       //

        mPaint1 = new Paint();
        mPaint1.setStrokeWidth(5f);
        mPaint1.setAntiAlias(true);
        // mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setStrokeCap(Paint.Cap.ROUND);

        if (color==-1){
            mPaint1.setColor(Color.argb(255, 255, 102, 204));
        }else {
            mPaint1.setColor(color);
        }
        mRandom = new Random();
        sparkManager = new CustomSpark();
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        double angle = 0;
        if (mPointsEdge == null || mPointsEdge.length < data.length * 4) {
            mPointsEdge = new float[data.length * 4];
        }

        for (int i = 0; i < 120; i++, angle += 3) {
            int x = (int) Math.ceil(i * 8.5);
            int t = ((byte) (-Math.abs(data[x]) + 128)) * (rect.height() / 4) / 128 + mDefaultHeight;
            //  int t = (Math.abs(data[x])-100) + mDefaultHeight;
           // LogUtils.e("nsc"," t=="+t);
            t = t/2;
            mPoints[i * 4] = (float) (rect.width() / 2 + radius * Math.cos(Math.toRadians(angle)));
            mPoints[i * 4 + 1] = (float) (rect.height() / 2 + radius * Math.sin(Math.toRadians(angle)));
            mPoints[i * 4 + 2] = (float) (rect.width() / 2 + (radius + t) * Math.cos(Math.toRadians(angle)));
            mPoints[i * 4 + 3] = (float) (rect.height() / 2 + (radius + t) * Math.sin(Math.toRadians(angle)));

            mPointsEdge[i * 4] = (float) (rect.width() / 2 + (radius) * Math.cos(Math.toRadians(angle)));
            mPointsEdge[i * 4 + 1] = (float) (rect.height() / 2 + (radius) * Math.sin(Math.toRadians(angle)));
            mPointsEdge[i * 4 + 2] = (float) (rect.width() / 2 + (radius + t + 20) * Math.cos(Math.toRadians(angle)));
            mPointsEdge[i * 4 + 3] = (float) (rect.height() / 2 + (radius + t + 20) * Math.sin(Math.toRadians(angle)));
        }

        canvas.drawLines(mPoints, mPaint);
        canvas.drawLines(mPoints, mPaint1);
        for (int[] n : sparks) {
            sparkManager.drawSpark(canvas, rect.width() / 2, rect.height() / 2, n, rect, 3f, mRandom.nextInt(8),mColor);
        }
        canvas.drawLines(mPointsEdge, mEdgePath);

    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
//        int cx = rect.width() / 2;
//        int cy = rect.height() / 2;
//
//        if (mPointsEdge == null || mPointsEdge.length < data.length * 4) {
//            mPointsEdge = new float[data.length * 4];
//        }
//
//        for (int i = 0; i < data.length  / mDivisions; i++) {
//            // Calculate dbValue
//            byte rfk = data[mDivisions * i];
//            byte ifk = data[mDivisions * i + 1];
//            float magnitude = (rfk * rfk + ifk * ifk);
//            float dbValue = (float) (75 * Math.log10(magnitude)); //* (float) Math.log10(magnitude);//这里会导致正负无穷的数出现
//            if (dbValue > 220) {//根据数据随意给的最高限制
//                dbValue = 220;
//            } else if (dbValue < 0) {
//                dbValue = 0;
//            }
//            float value = 20 + dbValue;//20是给定的最小值
//            float[] cartPoint = {
//                    (float) (i * mDivisions) / (float) (data.length - 1),
//                    cy - value / 100
//            };
//
//            float[] cartPoint2 = {(float) (i * mDivisions) / (float) (data.length - 1),
//                    cy + value};
//
//            float[] polarPoint = toPolar(cartPoint, rect, cx, cy);
//            float[] polarPoint2 = toPolar(cartPoint2, rect, cx, cy);
//
//            mFFTPoints[i * 4] = polarPoint[0];
//            mFFTPoints[i * 4 + 1] = polarPoint[1];
//            mFFTPoints[i * 4 + 2] = polarPoint2[0];
//            mFFTPoints[i * 4 + 3] = polarPoint2[1];
//
//            mPointsEdge[i * 4] = polarPoint[0];
//            mPointsEdge[i * 4 + 1] = polarPoint[1];
//            mPointsEdge[i * 4 + 2] = polarPoint2[0];
//            mPointsEdge[i * 4 + 3] = polarPoint2[1];
//        }
//        canvas.drawLines(mFFTPoints, mPaint);
//        canvas.drawLines(mFFTPoints, mPaint1);
//        canvas.drawLines(mPointsEdge, mEdgePath);
    }


//    private float[] toPolar(float[] cartesian, Rect rect, int cx, int cy) {
//        if (cartesian[1] < 0) {//cartesian[1]可能是正无穷或者负无穷的数
//            cartesian[1] = maxValue;
//        } else {
//            maxValue = cartesian[1];
//        }
//        // float cX = rect.width() / 2;
//        // float cY = rect.height() / 2;
//        float angle = (float) ((cartesian[0]) * 2 * Math.PI);
//        float radius = (float) ((cx * (1 - mAmplitude) + mAmplitude * cartesian[1]) * 1.2 / mCircleSize);
//        float[] out = {
//                (float) (cx + radius * Math.sin(angle)),
//                (float) (cy + radius * Math.cos(angle))
//        };
//        return out;
//    }


    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }
}
