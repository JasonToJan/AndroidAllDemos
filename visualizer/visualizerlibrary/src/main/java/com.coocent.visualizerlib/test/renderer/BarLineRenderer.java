package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;

/**

 */
public class BarLineRenderer extends Renderer {

    private Paint mPaint;
    private Shader shader;
    private int mDefaultHeight = 0;

    public BarLineRenderer(int height) {
        mPaint = new Paint();
        mPaint.setStrokeWidth(1f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 255, 102, 204));
        //  paint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        shader = new LinearGradient(0,
                0,
                0,
                height,
                Color.BLUE,
                Color.GREEN,
                Shader.TileMode.MIRROR /*or REPEAT*/);
    }

    @Override
    public void onAudioRender(Canvas canvas, byte[] bytes, Rect rect) {


        if (bytes != null) {
            mPaint.setShader(shader);
            for (int i = 0, k = 0; i < (bytes.length - 1) && k < bytes.length; i++, k++) {
                int top = rect.height()*2/3  +
                        ((byte) (Math.abs(bytes[k]) + 128)) * rect.height() / 128 + mDefaultHeight;
                canvas.drawLine(i, rect.height()*2/3 , i, top, mPaint);
            }
        }
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

    }
}
