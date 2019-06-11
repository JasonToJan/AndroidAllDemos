/**
 * Copyright 2011, Felix Palmer
 * <p>
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class BarGraphRenderer extends Renderer {
    private int mDivisions;
    private Paint mPaint;
    private boolean mTop;
    private int mHeight = 4;

    /**
     * Renders the FFT data as a series of lines, in histogram form
     *
     * @param divisions - must be a power of 2. Controls how many lines to draw
     * @param paint     - Paint to draw lines with
     * @param top       - whether to draw the lines at the top of the canvas, or the bottom
     */
    public BarGraphRenderer(int divisions, Paint paint, boolean top) {
        super();
        mDivisions = divisions;
        mPaint = paint;
        mTop = top;
    }

    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        for (int i = 0; i < data.length / mDivisions; i++) {
            mFFTPoints[i * mHeight] = i * mHeight * mDivisions;
            mFFTPoints[i * mHeight + 2] = i * mHeight * mDivisions;
            byte rfk = data[mDivisions * i];
            byte ifk = data[mDivisions * i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (10 * Math.log10(magnitude));

            if (mTop) {
                mFFTPoints[i * mHeight + 1] = 0;
                mFFTPoints[i * mHeight + 3] = (dbValue * 8 - 10);
            } else {
                mFFTPoints[i * mHeight + 1] = rect.height();
                mFFTPoints[i * mHeight + 3] = rect.height() - (dbValue * 8 - 10);
            }
        }

        canvas.drawLines(mFFTPoints, mPaint);
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

    }
}
