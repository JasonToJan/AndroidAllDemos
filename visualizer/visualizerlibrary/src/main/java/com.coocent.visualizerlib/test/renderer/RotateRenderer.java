package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;


import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**

 */
public class RotateRenderer extends Renderer {
    private final String TAG = "EnergyBlockRenderer";
    private Paint mPaint;
    private Paint mPoint;

    private float mInitialRadius = 165; // 初始波纹半径
    private float mMaxRadiusRate = 0.99f; // 如果没有设置mMaxRadius，可mMaxRadius = 最小长度 * mMaxRadiusRate;
    private float mMaxRadius; // 最大波纹半径
    private long mDuration = 3000; // 一个波纹从创建到消失的持续时间
    private int mSpeed = 1000; // 波纹的创建速度，每500ms创建一个
    private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
    private List<Circle> mCircleList = new ArrayList<>();
    private long mLastCreateTime;
    private float px = 0;//控制小圆点出现位置
    private float py = 0;//控制小圆点移动
    public static Random mRandom;
    private Path mPath;

    public void layout(int w, int h) {
        mMaxRadius = Math.min(w, h) * mMaxRadiusRate / 2.0f;
    }

    public RotateRenderer(Context context, float initialRadius,int color) {
        if (color ==-1){
            color = Color.argb(255, 220, 220, 220);
        }
        this.mInitialRadius = initialRadius;
        int paintSize = context.getResources().getDimensionPixelSize(R.dimen.rotate_size);
        mPaint = new Paint();
        mPaint.setStrokeWidth(paintSize);
        mPaint.setAntiAlias(true);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
      //  mPaint.setColor(Color.argb(255, 220, 220, 220));
        mPaint.setColor(color);

        mPoint = new Paint();
        mPoint.setStrokeWidth(13f);
       // mPoint.setAntiAlias(true);
        mPoint.setStrokeCap(Paint.Cap.ROUND);
        //mPoint.setColor(Color.argb(255, 220, 220, 220));
        mPoint.setColor(color);

        mRandom = new Random();
        // setInterpolator(new AccelerateInterpolator(1.2f));
        setInterpolator(new LinearInterpolator());

        mPath = new Path();
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        LogUtils.d("执行了onAudioRender 1");
        newCircle();
        Iterator<Circle> iterator = mCircleList.iterator();
        while (iterator.hasNext()) {
            Circle circle = iterator.next();
            if (System.currentTimeMillis() - circle.mCreateTime < mDuration) {
                mPaint.setAlpha(circle.getAlpha() * 3 / 5);
                mPoint.setAlpha(circle.getAlpha() / 3);
                canvas.drawCircle(rect.width() / 2, rect.height() / 2, circle.getCurrentRadius(), mPaint);

                px += ((double) mRandom.nextInt(5) / 1000 + 0.001);
                py += 0.0001;

                if (py > 0.1) {
                    px = 0;
                    py = 0;
                }
                canvas.drawCircle(
                        rect.width() / 2 + (float) (circle.getRadius() * Math.cos(360 - 3.14 * Math.PI * circle.getPointPosition() * py)),
                        rect.height() / 2 + (float) (circle.getRadius() * Math.sin(360 - 3.14 * Math.PI * circle.getPointPosition() * py)),
                        circle.getPointSize(), mPoint);

                LogUtils.d("执行了onAudioRender2");

            } else {
                iterator.remove();
            }
        }
        //  mPaint.setXfermode(null);
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < (data.length - 1) / 64; i++) {
            byte rfk = data[i];
            byte ifk = data[i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            float dbValue = 75 * (float) Math.log10(magnitude);
            mCircleRadius = dbValue;
        }
    }

    private float mCircleRadius;


    private void newCircle() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastCreateTime < mSpeed) {
            return;
        }
        Circle circle = new Circle();
        circle.setPointSize(getPointSize(5, 20));
        circle.setAmplitude(mCircleRadius / 10);
        circle.setPointPosition((float) (px + mRandom.nextInt(30)));
        mCircleList.add(circle);
        mLastCreateTime = currentTime;
    }


    private class Circle {
        private long mCreateTime;
        private int mPointSize;
        private float x;
        private float y;
        private float mRadius;
        private double mPointPosition;//控制点的出现位置
        private float mAmplitude = 1;

        public Circle() {
            this.mCreateTime = System.currentTimeMillis();
        }

        public double getPointPosition() {
            return mPointPosition;
        }

        public void setPointPosition(double mPointPosition) {
            this.mPointPosition = mPointPosition;
        }

        public int getAlpha() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            return (int) ((1.0f - mInterpolator.getInterpolation(percent)) * 255);
        }

        public float getCurrentRadius() {
            float percent = (System.currentTimeMillis() - mCreateTime) * 1.0f / mDuration;
            // mRadius = mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius);
            mRadius = mInitialRadius + mInterpolator.getInterpolation(percent) * (mMaxRadius - mInitialRadius) + getAmplitude();
           // LogUtils.e(TAG, "nsc  =" + getAmplitude() + "mRadius=" + mRadius);
            return mRadius;
        }

        public float getRadius() {
            return mRadius;
        }

        public void setRadius(float mRadius) {
            this.mRadius = mRadius;
        }

        public int getPointSize() {
            return mPointSize;
        }

        public void setPointSize(int mPointSize) {
            this.mPointSize = mPointSize;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public float getAmplitude() {
            return mAmplitude;
        }

        public void setAmplitude(float mAmplitude) {
            this.mAmplitude = mAmplitude;
        }
    }


    public int getPointSize(int startNum, int endNum) {
        if (endNum > startNum) {
            return mRandom.nextInt(endNum - startNum) + startNum;
        }
        return 0;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }
    }


}
