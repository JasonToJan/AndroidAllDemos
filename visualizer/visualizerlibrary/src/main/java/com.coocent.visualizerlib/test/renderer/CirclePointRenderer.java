package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.coocent.visualizerlib.R;

/**

 */
public class CirclePointRenderer extends Renderer {
    private Paint mPaint;
    private Paint mPoint;
    private Paint mEdgePath;
    private int mDivisions = 1;
    private float mCircleSize = 1.9f;

    public CirclePointRenderer(Context context,int color) {
        super();
        mCircleSize = 15;
        if (color==-1){
            color =  Color.argb(255, 255, 102, 204);
        }
        mPaint = new Paint();
        mPaint.setStrokeWidth(1f);
        //mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);

        mEdgePath = new Paint();
        mEdgePath.setStrokeWidth(3f);
        //  mEdgePath.setAntiAlias(true);
        mEdgePath.setStyle(Paint.Style.STROKE);
        // mEdgePath.setColor(Color.argb(255, 255, 102, 204));
        mEdgePath.setColor(color);
        mEdgePath.setAlpha(50);

        mPoint = new Paint();
        mPoint.setStrokeWidth(5f);
        mPoint.setStrokeCap(Paint.Cap.ROUND);
        //  mPoint.setAntiAlias(true);
        mPoint.setColor(color);
        // mPoint.setColor(Color.argb(205, 255, 192, 203));
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < data.length - 1; i++) {
            int data1 = rect.height() / 2 + ((byte) (data[i] + 128)) * (rect.height() / 2) / 128;
            float[] cartPoint = {(float) i / (data.length - 1), data1};

            //LogUtils.e("nsc", " onAudioRender=" + data1);
            float[] polarPoint = audioPolar(cartPoint, rect, mCircleSize);
            mPoints[i * 4] = polarPoint[0];
            mPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {
                    (float) (i + 1) / (data.length - 1),
                    rect.height() / 2 + ((byte) (data[i + 1] + 128)) * (rect.height() / 2) / 128
            };

            float[] polarPoint2 = audioPolar(cartPoint2, rect, mCircleSize);
            mPoints[i * 4 + 2] = polarPoint2[0];
            mPoints[i * 4 + 3] = polarPoint2[1];

        }
        canvas.drawPoints(mPoints, mPoint);
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, (float) (mAudioRadius - 20), mPaint);
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
//        for (int i = 0; i < data.length - 1 / mDivisions; i++) {
//            byte rfk = data[mDivisions * i];
//            byte ifk = data[mDivisions * i + 1];
//            float magnitude = (rfk * rfk + ifk * ifk);
//           // double a = Math.log10(magnitude);
//            float dbValue = (float)(75* Math.log10(magnitude)); //* (float) Math.log10(magnitude);//这里会导致正负无穷的数出现
//            if (dbValue > 220) {//根据数据随意给的最高限制
//                dbValue = 220;
//            } else if (dbValue < 0) {
//                dbValue = 0;
//            }
//            float value = 20 + dbValue;
//
//            float data1 = (float) (i * mDivisions) / (float) (data.length - 1);
//            float data2 = rect.height() / 2 - value / 100;
//            float[] cartPoint = {
//                    data1,
//                    data2
//            };
//
//            float[] polarPoint = toPolar(cartPoint, rect, 1.1f);
//            mFFTPoints[i * 4] = polarPoint[0];
//            mFFTPoints[i * 4 + 1] = polarPoint[1];
//
//            float[] cartPoint2 = {
//                    (float) (i * mDivisions) / (float) (data.length - 1),
//                    rect.height() / 2 + value
//            };
//
//            float[] polarPoint2 = toPolar(cartPoint2, rect, 1.1f);
//            mFFTPoints[i * 4 + 2] = polarPoint2[0];
//            mFFTPoints[i * 4 + 3] = polarPoint2[1];
//
//        }
//        canvas.drawLines(mFFTPoints, mEdgePath);

    }

    private double mAudioRadius = 0;

    private float[] audioPolar(float[] cartesian, Rect rect, float mCircleSize) {
        double cX = rect.width() / 2;
        double cY = rect.height() / 2;
        double angle = (cartesian[0]) * 2 * Math.PI;
        mAudioRadius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1]) * (1.2 + Math.sin(modulation)) / mCircleSize;
        float[] out = {
                (float) (cX + mAudioRadius * Math.sin(angle)),
                (float) (cY + mAudioRadius * Math.cos(angle))
        };
        return out;
    }


    private double mRadius;
    float modulation = 0;
    float modulationStrength = 0.4f; // 0-1
    float angleModulation = 0;
    float aggresive = 0.4f;
    float maxValue = 0;

    private float[] toPolar(float[] cartesian, Rect rect, float size) {
        if (cartesian[1] < 0) {//cartesian[1]可能是正无穷或者负无穷的数
            cartesian[1] = maxValue;
        } else {
            maxValue = cartesian[1];
        }
        //LogUtils.e("nsc", " cartesian[1]=" + cartesian[1]);
        double cX = rect.width() / 2;
        double cY = rect.height() / 2;
        if (cartesian[0] > 1000 || cartesian[0] < -1000 || cartesian[1] > 1000 || cartesian[1] < -1000) {
            cartesian[0] = maxValue;
        } else {
            maxValue = cartesian[0];
        }
        double angle = (cartesian[0]) * 2 * Math.PI;
        mRadius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2) / size;
        float point1 = (float) (cX + mRadius * Math.sin(angle + angleModulation));
        float point2 = (float) (cY + mRadius * Math.cos(angle + angleModulation));

        float[] out = {point1, point2};
        return out;
    }
}
