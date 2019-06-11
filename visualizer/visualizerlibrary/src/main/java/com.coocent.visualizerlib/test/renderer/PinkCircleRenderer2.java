package com.coocent.visualizerlib.test.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.coocent.visualizerlib.utils.LogUtils;


/**

 */
public class PinkCircleRenderer2 extends Renderer {
    private Paint mPaint;
    private int radius = -1;
    private float mStrokeWidth = 0.005f;
    private int mDefaultHeight = 5;

    public PinkCircleRenderer2(Context context, float circleSize) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(5f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 255, 102, 204));
        // mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth > 10) {
            this.mStrokeWidth = 10 * 0.005f;
        } else if (strokeWidth < 1) {
            this.mStrokeWidth = 0.005f;
        }
        this.mStrokeWidth = strokeWidth * 0.005f;
    }

    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        if (radius == -1) {
            radius = rect.height() < rect.width() ? rect.height() : rect.width();
            radius = (int) (radius * 0.65 / 2);
            double circumference = 2 * Math.PI * radius;
            mPaint.setStrokeWidth((float) (circumference / 240));
            //mPaint.setStyle(Paint.Style.STROKE);
            // mPaint.setStrokeWidth(4);
        }

        double angle = 0;
        int t1 = 0;

        for (int i = 0; i < 120; i++, angle += 3) {
            int x = (int) Math.ceil(i * 8.5);
            int t = ((byte) (-Math.abs(data[x]) + 128)) * (rect.height() / 4) / 128 + mDefaultHeight;
          //  int t = (Math.abs(data[x])-100) + mDefaultHeight;

            mPoints[i * 4] = (float) (rect.width() / 2
                    + radius
                    * Math.cos(Math.toRadians(angle)));

            mPoints[i * 4 + 1] = (float) (rect.height() / 2
                    + radius
                    * Math.sin(Math.toRadians(angle)));

            mPoints[i * 4 + 2] = (float) (rect.width() / 2
                    + (radius  + t)
                    * Math.cos(Math.toRadians(angle)));

            mPoints[i * 4 + 3] = (float) (rect.height() / 2
                    + (radius +  t)
                    * Math.sin(Math.toRadians(angle)));
            LogUtils.e("nsc", " t=" + t);
            t1 = t;
        }

        canvas.drawLines(mPoints, mPaint);
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

    }
}
