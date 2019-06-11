package com.coocent.visualizerlib.test.renderer;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

/**

 */
public class CircleLineRenderer extends Renderer {
    private Paint mPaint;
    protected int color = Color.BLUE;
    private float density = 50;
    private int gap;

    public CircleLineRenderer() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(5f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 0, 0, 255));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        BlurMaskFilter blurMaskFilter =  new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID);
        mPaint.setMaskFilter(blurMaskFilter);
    }

    public void setDensity(float density) {
        if (this.density > 180) {
            this.gap = 1;
        } else {
            this.gap = 4;
        }
        this.density = density;
        if (density > 256) {
            this.density = 250;
            this.gap = 0;
        } else if (density <= 10) {
            this.density = 10;
        }
    }

    @Override
    public void onAudioRender(Canvas canvas, byte[] bytes, Rect rect) {
        if (bytes != null) {
            float barWidth = rect.width() / density;
            float div = bytes.length / density;
            //canvas.drawLine(0, rect.height() / 2, rect.width(), rect.height() / 2, middleLine);
          //  mPaint.setStrokeWidth(barWidth - gap);

            for (int i = 0; i < density; i++) {
                int bytePosition = (int) Math.ceil(i * div);
                int top = rect.height() / 2
                        + (128 - Math.abs(bytes[bytePosition]))
                        * (rect.height() / 2) / 128;

                int bottom = rect.height() / 2
                        - (128 - Math.abs(bytes[bytePosition]))
                        * (rect.height() / 2) / 128;

                float barX = (i * barWidth) + (barWidth / 2);
              //  mPaint.setColor(getColor());
                canvas.drawLine(barX, bottom, barX, rect.height() / 2, mPaint);
                canvas.drawLine(barX, top, barX, rect.height() / 2, mPaint);
            }
        }

    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

    }

    private int getColor() {
        int[] ranColor = {Color.RED, Color.YELLOW, Color.BLACK, Color.BLUE, Color.GREEN, Color.GRAY,
                Color.CYAN, Color.LTGRAY, Color.TRANSPARENT};
        Random random = new Random();
        int value = random.nextInt(ranColor.length - 1);
        return ranColor[value];
    }
}
