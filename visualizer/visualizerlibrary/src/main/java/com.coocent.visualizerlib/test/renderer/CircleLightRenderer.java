package com.coocent.visualizerlib.test.renderer;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;

/**

 */
public class CircleLightRenderer extends Renderer {
    private Paint mPaint;
    private Paint mEdgePath;
    private RadialGradient mRadialGradient;
    private float[] stops = new float[]{0f, 0.2f, 0.5f, 1f};
    BlurMaskFilter blurs[] = new BlurMaskFilter[] {
            new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID),
            new BlurMaskFilter(30, BlurMaskFilter.Blur.SOLID),
            new BlurMaskFilter(50, BlurMaskFilter.Blur.SOLID),
            new BlurMaskFilter(35, BlurMaskFilter.Blur.INNER),
            null};
    public CircleLightRenderer(Paint paint) {
        super();
        mPaint = paint;

        mEdgePath = new Paint();
        mEdgePath.setStrokeWidth(3);
        mEdgePath.setAntiAlias(true);

        mEdgePath.setColor(Color.WHITE);
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

        int[] colors = new int[]{Color.WHITE,getColor()};
      //  float[] position = new float[]{0, 0.3f, 0.5f, 0.625f, 0.75f, 0.875f, 1f};
        mRadialGradient = new RadialGradient(rect.width() / 2, rect.height() / 2, (float) mRadius, colors, null, Shader.TileMode.REPEAT);
        mPaint.setShader(mRadialGradient);
        for(int i = 0;i < blurs.length;i++){
            mPaint.setMaskFilter(blurs[i]);
            canvas.drawCircle(rect.width() / 2, rect.height() / 2, (float) mRadius/2, mPaint);
        }

       // canvas.drawCircle(rect.width() / 2, rect.height() / 2, (float) mRadius/2, mEdgePath);
        // @重绘
        modulation += 0.04;

    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

    }

    private double mRadius;
    private float colorCounter = 0;
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

    private int getColor() {
        int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 1));
        int g = (int) Math.floor(128 * (Math.sin(colorCounter + 2) + 1));
        int b = (int) Math.floor(128 * (Math.sin(colorCounter + 4) + 1));
        colorCounter += 1;
        return Color.argb(128, r, g, b);
    }

    private int getColor1() {
        int r = (int) Math.floor(128 * (Math.sin(modulation) + 1));
        int g = (int) Math.floor(128 * (Math.sin(modulation + 2) + 1));
        int b = (int) Math.floor(128 * (Math.sin(modulation + 4) + 1));
        modulation += 0.02;
        return Color.argb(128, r, g, b);
    }
}
