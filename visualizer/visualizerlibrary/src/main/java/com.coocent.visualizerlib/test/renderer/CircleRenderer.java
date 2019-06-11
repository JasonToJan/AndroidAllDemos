package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.coocent.visualizerlib.R;


/**

 */
public class CircleRenderer extends Renderer {
    private final String TAG = "EnergyBlockRenderer";
    private Paint mPaint;
    private Paint mPoint;
    private int mDivisions = 16;

    float modulation = 0;
    float mAmplitude = 0.83f;//幅度
    float mRadius = 0;

    public CircleRenderer(Context context) {
        int paintSize = context.getResources().getDimensionPixelSize(R.dimen.pink_size);
        mPaint = new Paint();
        mPaint.setStrokeWidth(3f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 222, 92, 143));

        mPoint = new Paint();
        mPoint.setStrokeWidth(paintSize);
        mPoint.setAntiAlias(true);
        mPoint.setStrokeCap(Paint.Cap.ROUND);
        mPoint.setColor(Color.argb(255, 255, 192, 203));

    }

    private float[] toPolar(float[] cartesian, Rect rect, float radiusValue) {
        float cX = rect.width() / 2;
        float cY = rect.height() / 2;
        float angle = (float) ((cartesian[0]) * 2 * Math.PI);
        float radius = mRadius = (float) (((rect.width() / 2) * (1 - mAmplitude) + mAmplitude * cartesian[1] / 2) * (1.2 + Math.sin(modulation)) / radiusValue);
        float[] out = {
                (float) (cX + radius * Math.sin(angle)),
                (float) (cY + radius * Math.cos(angle))
        };
        return out;
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < data.length - 1 / mDivisions; i++) {
            float[] cartPoint = {(float) (i * mDivisions) / (data.length - 1),
                    rect.height() / 2 + ((byte) (data[i] + 128)) * (rect.height() / 2) / 128
            };

            float[] polarPoint = toPolar(cartPoint, rect, 2.2f);
            mPoints[i * 4] = polarPoint[0];
            mPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {
                    (float) ((i + 1) * mDivisions) / (data.length - 1),
                    rect.height() / 2 + ((byte) (data[i] + 128)) * (rect.height() / 2) / 128
            };

            float[] polarPoint2 = toPolar(cartPoint2, rect, 2.2f);
            mPoints[i * 4 + 2] = polarPoint2[0];
            mPoints[i * 4 + 3] = polarPoint2[1];
        }

        //先清空画布
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawLines(mPoints, mPaint);
        //canvas.drawPoints(mPoints, mPoint);

        //mPaint.setStyle(Paint.Style.STROKE);//设置空心
        //canvas.drawCircle(rect.width() / 2, rect.height() / 2, mRadius - 30, mPaint);
        //  mPaint.setXfermode(null);
        // LogUtils.e(TAG, "nsc AudioData =" + mPoints.length);

        modulation += 0.04;

    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < data.length / mDivisions; i++) {
            // Calculate dbValue
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

            float[] polarPoint = toPolar(cartPoint, rect, 1.8f);
            mFFTPoints[i * 4] = polarPoint[0];
            mFFTPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {
                    (float) (i * mDivisions) / (data.length - 1),
                    rect.height() / 2 + value
            };

            float[] polarPoint2 = toPolar(cartPoint2, rect, 1.8f);
            mFFTPoints[i * 4 + 2] = polarPoint2[0];
            mFFTPoints[i * 4 + 3] = polarPoint2[1];
        }
        //  LogUtils.e(TAG, "nsc FFTData=" + mFFTPoints.length);
        //canvas.drawPoints(mFFTPoints, mPoint);
        canvas.drawLines(mFFTPoints, mPoint);
    }
}
