package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.coocent.visualizerlib.R;


/**

 */
public class PinkCircleRenderer extends Renderer {
    private final String TAG = "PinkCircleRenderer";
    private Paint mPaint;
    private int mDivisions = 8;//频谱数量
    // float modulation = 0;
    float mAmplitude = 0.83f;//调节跳动幅度
    float mCircleSize = 0.7f;//控制圈的大小
    float maxValue = 0;

    private int radius = -1;

    public PinkCircleRenderer(Context context, int color) {
        mCircleSize = context.getResources().getDimensionPixelSize(R.dimen.pink_circle_size);
        if (color == -1) {
            color = Color.argb(205, 255, 192, 203);
        }
        int paintSize = context.getResources().getDimensionPixelSize(R.dimen.pink_size);
        mPaint = new Paint();
        mPaint.setStrokeWidth(paintSize);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(color);
        // mPaint.setColor(Color.argb(205, 255, 192, 203));
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
//        double angle = 0;
//        for (int i = 0; i < 120; i++, angle += 3) {
//            int x = (int) Math.ceil(i * 8.5);
//            int t = ((byte) (-Math.abs(data[x]) + 128)) * (rect.height() / 4) / 128 + 5;
//            //  int t = (Math.abs(data[x])-100) + mDefaultHeight;
//
//            mPoints[i * 4] = (float) (rect.width() / 2
//                    + radius
//                    * Math.cos(Math.toRadians(angle)));
//            mPoints[i * 4 + 1] = (float) (rect.height() / 2
//                    + radius
//                    * Math.sin(Math.toRadians(angle)));
//            mPoints[i * 4 + 2] = (float) (rect.width() / 2
//                    + (radius + t)
//                    * Math.cos(Math.toRadians(angle)));
//            mPoints[i * 4 + 3] = (float) (rect.height() / 2
//                    + (radius + t)
//                    * Math.sin(Math.toRadians(angle)));
//
//        }
//        canvas.drawLines(mPoints, mPaint);
    }

    /**
     * //mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
     * 需要设置为128个点
     *
     * @param canvas - Canvas to draw on
     * @param data   - Data to audioRender
     * @param rect   - Rect to audioRender into
     */
    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
        //LogUtils.e(TAG, "nsc =" + data.length);
        int cx = rect.width() / 2;
        int cy = rect.height() / 2;

        for (int i = 0; i < data.length / mDivisions; i++) {
            // Calculate dbValue
            byte rfk = data[mDivisions * i];
            byte ifk = data[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            float dbValue = (float) (75 * Math.log10(magnitude)); //* (float) Math.log10(magnitude);//这里会导致正负无穷的数出现
            if (dbValue > 220) {//根据数据随意给的最高限制
                dbValue = 220;
            } else if (dbValue < 0) {
                dbValue = 0;
            }
            float value = 20 + dbValue;//20是给定的最小值
            float[] cartPoint = {
                    (float) (i * mDivisions) / (float) (data.length - 1),
                    cy - value / 100
            };
            float[] polarPoint = toPolar(cartPoint, rect, cx, cy);
            mFFTPoints[i * 4] = polarPoint[0];
            mFFTPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {(float) (i * mDivisions) / (float) (data.length - 1),
                    cy + value};

            float[] polarPoint2 = toPolar(cartPoint2, rect, cx, cy);
            mFFTPoints[i * 4 + 2] = polarPoint2[0];
            mFFTPoints[i * 4 + 3] = polarPoint2[1];
        }
        canvas.drawLines(mFFTPoints, mPaint);
    }

    private float[] toPolar(float[] cartesian, Rect rect, int cx, int cy) {
        if (cartesian[1] < 0) {//cartesian[1]可能是正无穷或者负无穷的数
            cartesian[1] = maxValue;
        } else {
            maxValue = cartesian[1];
        }
        // float cX = rect.width() / 2;
        // float cY = rect.height() / 2;
        float angle = (float) ((cartesian[0]) * 2 * Math.PI);
        float radius = (float) ((cx * (1 - mAmplitude) + mAmplitude * cartesian[1])/2 + mCircleSize);
        float[] out = {
                (float) (cx + radius * Math.sin(angle)),
                (float) (cy + radius * Math.cos(angle))
        };
        return out;
    }
}

