package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**

 */
public class ColumnarRenderer extends Renderer {
    private int mDivisions;
    private Paint mPaint;
    private int mSpectrumNum = 96;

    public ColumnarRenderer(int divisions, Paint paint) {
        super();
        mDivisions = divisions;
        mPaint = paint;
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {

    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
        final int baseX = rect.width() / mSpectrumNum;
        final int height = rect.height();
        for (int i = 0; i < mSpectrumNum; i++) {

            float magnitude = baseX * i + baseX / 2;
            mFFTPoints[i * 4] = magnitude;
            mFFTPoints[i * 4 + 1] = height / 2;

            mFFTPoints[i * 4 + 2] = magnitude;
            mFFTPoints[i * 4 + 3] = height / 2 - data[i] * 4;
        }
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawLines(mFFTPoints, mPaint);
        // mPaint.setXfermode(null);
    }
}
