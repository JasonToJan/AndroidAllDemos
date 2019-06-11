package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.coocent.visualizerlib.test.renderer.particle.RandomGenerator;
import com.coocent.visualizerlib.test.renderer.particle.SnowFlake;


/**

 */
public class SnowFlakeRenderer extends Renderer {
    private Paint mPaint;

    private static final int NUM_SNOWFLAKES = 100; // 雪花数量
    private SnowFlake[] mSnowFlakes; // 雪花
    private RandomGenerator mRandom;

    public SnowFlakeRenderer() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(1f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mRandom = new RandomGenerator();
    }

    public void layout(int w, int h) {
        mSnowFlakes = new SnowFlake[NUM_SNOWFLAKES];
        for (int i = 0; i < NUM_SNOWFLAKES; ++i) {
            mSnowFlakes[i] = SnowFlake.create(w, h, mPaint);
        }
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

       // LogUtils.e("nsc", " mRadius=" + mRadius + " mPoints=" + mPoints[0]);
        for (SnowFlake s : mSnowFlakes) {
            s.draw(canvas, Color.WHITE, mRandom.getRandom(5, 10 + (float) mRadius / 100));
        }
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

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
        mRadius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2);
        float[] out = {
                (float) (cX + mRadius * Math.sin(angle + angleModulation)),
                (float) (cY + mRadius * Math.cos(angle + angleModulation))
        };
        return out;
    }

    private float colorCounter = 0;

    private int getColor() {
        int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 1));
        int g = (int) Math.floor(128 * (Math.sin(colorCounter + 2) + 1));
        int b = (int) Math.floor(128 * (Math.sin(colorCounter + 4) + 1));
        colorCounter += 0.1;
        return Color.argb(128, r, g, b);
    }
}
