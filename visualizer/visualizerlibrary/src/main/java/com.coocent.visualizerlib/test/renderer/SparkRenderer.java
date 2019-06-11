package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.coocent.visualizerlib.test.renderer.particle.SparkManager;


/**

 */
public class SparkRenderer extends Renderer {
    private Paint mPaint;
    private SparkManager sparkManager;
    int[][] sparks = new int[100][10];
    private float px = 0;//用于让爆炸烟花旋转起来，值的累加大小控制旋转的速度
    private double mSpeed = 0.005;
    private float mRotateRadius = 1.2f;

    public SparkRenderer(double speed,float radius) {
        super();
        this.mSpeed = speed;
        this.mRotateRadius = radius;

        mPaint = new Paint();
        mPaint.setStrokeWidth(3f);
       // mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.argb(205, 255, 192, 203));
        mPaint.setAlpha(150);
        sparkManager = new SparkManager();
    }

    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < data.length - 1; i++) {
            float[] cartPoint = {(float) i / (data.length - 1),
                    rect.height() / 2 + ((byte) (data[i] + 128)) * (rect.height() / 2) / 128
            };

            float[] polarPoint = toPolar(cartPoint, rect);
            mPoints[i * 4] = polarPoint[0];
            mPoints[i * 4 + 1] = polarPoint[1];

            float[] cartPoint2 = {
                    (float) (i + 1) / (data.length - 1),
                    rect.height() / 2 + ((byte) (data[i + 1] + 128)) * (rect.height() / 2) / 128
            };

            float[] polarPoint2 = toPolar(cartPoint2, rect);
            mPoints[i * 4 + 2] = polarPoint2[0];
            mPoints[i * 4 + 3] = polarPoint2[1];
        }
        px += mSpeed;
        if (px > 10) {
            px = 0;
        }
        // float x = rect.width() / 2 + (float) (mRadius * Math.cos(360 - 3.14 * Math.PI * px));
        // float y = rect.height() / 2 + (float) (mRadius * Math.sin(360 - 3.14 * Math.PI * px));
        float x = rect.width() / 2 + (float) (mRadius * Math.cos(3.14 * Math.PI * px));
        float y = rect.height() / 2 + (float) (mRadius * Math.sin(3.14 * Math.PI * px));
        for (int[] n : sparks) {
            sparkManager.drawSpark(canvas, (int) x, (int) y, n, rect, (float) mRadius / 100);
        }
        // canvas.drawCircle(rect.width() / 2, rect.height() / 2, (float) mRadius, mPaint);
    }


    private double mRadius;
    float modulation = 0;
    float modulationStrength = 0.4f; // 0-1
    float angleModulation = 0;
    float aggresive = 0.4f;

    private float[] toPolar(float[] cartesian, Rect rect) {
        double cX = rect.width() / 2;
        double cY = rect.height() / 2;
        double angle = (cartesian[0]) * 2 * Math.PI;
        mRadius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2)/mRotateRadius;
        float[] out = {
                (float) (cX + mRadius * Math.sin(angle + angleModulation)),
                (float) (cY + mRadius * Math.cos(angle + angleModulation))
        };
        return out;
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
    }
}
