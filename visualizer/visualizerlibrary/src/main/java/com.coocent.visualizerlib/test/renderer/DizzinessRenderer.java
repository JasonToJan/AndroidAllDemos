/**
 * Copyright 2011, Felix Palmer
 * <p>
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.coocent.visualizerlib.test.renderer;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

public class DizzinessRenderer extends Renderer {
    private int mDivisions;
    private Paint mPaint;

    float modulation = 0;
    float modulationStrength = 0.4f; // 0-1
    float angleModulation = 0;
    float aggresive = 0.4f;

    private Paint mEdgePath;
    private RadialGradient mRadialGradient;
    private float[] stops = new float[]{0f, 0.2f, 0.5f, 1f};
    /**
     * Renders the audio data onto a pulsing circle
     *
     * @param paint      - Paint to draw lines with
     * @param divisions  - must be a power of 2. Controls how many lines to draw
     */
    public DizzinessRenderer(Paint paint, int divisions ) {
        super();
        mPaint = paint;
        mDivisions = divisions;

        mEdgePath = new Paint();
        mEdgePath.setStrokeWidth(5);
        mEdgePath.setAntiAlias(true);
        mEdgePath.setColor(Color.WHITE);
    }


    private int getColor(){
        int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 1));
        int g = (int) Math.floor(128 * (Math.sin(colorCounter + 2) + 1));
        int b = (int) Math.floor(128 * (Math.sin(colorCounter + 4) + 1));
        colorCounter += 0.05;
        return Color.argb(128, r, g, b);
    }

    private int getColor1(){
        int r = (int) Math.floor(128 * (Math.sin(modulation) + 1));
        int g = (int) Math.floor(128 * (Math.sin(modulation + 2) + 1));
        int b = (int) Math.floor(128 * (Math.sin(modulation + 4) + 1));
        return Color.argb(128, r, g, b);
    }

    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
       // canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
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

        int[] colors = new int[]{Color.WHITE, getColor1(), Color.BLUE, getColor()};
        mRadialGradient = new RadialGradient(rect.width() / 2, rect.height() / 2, (float) mRadius, colors, stops, Shader.TileMode.REPEAT);
        mPaint.setShader(mRadialGradient);
        mPaint.setMaskFilter(new BlurMaskFilter((float) mRadius/10, BlurMaskFilter.Blur.SOLID));
        canvas.drawCircle(rect.width() / 2, rect.height() / 2, (float) mRadius, mPaint);
        modulation += 0.04;
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {


    }

    private double mRadius;

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
}
