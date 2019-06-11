/**
 * Copyright 2011, Felix Palmer
 * <p>
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Rect;


 public abstract class Renderer {
    // Have these as members, so we don't have to re-create them each time
    protected float[] mPoints;
    protected float[] mFFTPoints;
    public boolean isPlaying = true;

    public Renderer() {
    }

    // As the display of raw/FFT audio will usually look different, subclasses
    // will typically only implement one of the below methods

    /**
     * Implement this method to audioRender the audio data onto the canvas
     *
     * @param canvas - Canvas to draw on
     * @param data   - Data to audioRender
     * @param rect   - Rect to audioRender into
     */
    abstract public void onAudioRender(Canvas canvas, byte[] data, Rect rect);

    /**
     * Implement this method to audioRender the FFT audio data onto the canvas
     *
     * @param canvas - Canvas to draw on
     * @param data   - Data to audioRender
     * @param rect   - Rect to audioRender into
     */
    abstract public void onFftRender(Canvas canvas, byte[] data, Rect rect);


    // These methods should actually be called for rendering

    /**
     * Render the audio data onto the canvas
     *
     * @param canvas - Canvas to draw on
     * @param data   - Data to audioRender
     * @param rect   - Rect to audioRender into
     */
    final public void audioRender(Canvas canvas, byte[] data, Rect rect) {
        // if (isPlaying) {
        if (mPoints == null || mPoints.length < data.length * 4) {
            mPoints = new float[data.length * 4];
        }
        onAudioRender(canvas, data, rect);
        //  }
    }

    /**
     * Render the FFT data onto the canvas
     *
     * @param canvas - Canvas to draw on
     * @param data   - Data to audioRender
     * @param rect   - Rect to audioRender into
     */
    final public void fftRender(Canvas canvas, byte[] data, Rect rect) {
        // if (isPlaying) {
        if (mFFTPoints == null || mFFTPoints.length < data.length * 4) {
            mFFTPoints = new float[data.length * 4];
        }

        onFftRender(canvas, data, rect);
        //  }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
