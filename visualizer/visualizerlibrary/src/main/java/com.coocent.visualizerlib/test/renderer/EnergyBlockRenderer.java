package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.util.Random;

/**

 */
public class EnergyBlockRenderer extends Renderer {
    private final String TAG = "EnergyBlockRenderer";
    private Paint mPaint;
    protected final static int MAX_LEVEL = 30;//音量柱·音频块 - 最大个数
    protected final static int CYLINDER_NUM = 26;//音量柱 - 最大个数
    protected byte[] mData = new byte[CYLINDER_NUM];//音量柱 数组
    private int hGap = 0;
    private int vGap = 0;
    private int levelStep = 0;
    private float strokeWidth = 0;
    private float strokeLength = 0;
    boolean mDataEn = true;
    private static final int DN_W = 470;//view宽度与单个音频块占比 - 正常480 需微调
    private static final int DN_H = 300;//view高度与单个音频块占比
    private static final int DN_SL = 10;//单个音频块宽度
    private static final int DN_SW = 2;//单个音频块高度

    public EnergyBlockRenderer(Paint mPaint) {
        this.mPaint = mPaint;
        levelStep = 230 / MAX_LEVEL;
    }

    public void onLayout(int left, int top, int right, int bottom) {

        float w, h, xr, yr;

        w = right - left;
        h = bottom - top;
        xr = w / (float) DN_W;
        yr = h / (float) DN_H;

        strokeWidth = DN_SW * yr;
        strokeLength = DN_SL * xr;
        hGap = (int) ((w - strokeLength * CYLINDER_NUM) / (CYLINDER_NUM + 1));
        vGap = (int) (h / (MAX_LEVEL + 2));//频谱块高度

        mPaint.setStrokeWidth(strokeWidth); //设置频谱块宽度
    }


    //绘制频谱块和倒影
    protected void drawCylinder(Canvas canvas, float x, byte value, Rect rect) {
        if (value == 0) {
            value = 1;
        }//最少有一个频谱块
        for (int i = 0; i < value; i++) { //每个能量柱绘制value个能量块
            float y = (rect.height() / 2 - i * vGap / 2 - vGap);//计算y轴坐标
            float y1 = (rect.height() / 2 + i * vGap / 2 + vGap);
            //绘制频谱块
            mPaint.setColor(getColor());//画笔颜色
            canvas.drawLine(x, y, (x + strokeLength), y, mPaint);//绘制频谱块

            //绘制音量柱倒影
            if (i <= 6 && value > 0) {
                mPaint.setColor(Color.WHITE);//画笔颜色
                mPaint.setAlpha(100 - (100 / 6 * i));//倒影颜色
                canvas.drawLine(x, y1, (x + strokeLength), y1, mPaint);//绘制频谱块
            }
        }
    }

    private int getColor() {
        int[] ranColor = {Color.RED, Color.YELLOW, Color.MAGENTA, Color.BLUE, Color.GREEN, Color.GRAY,
                Color.CYAN, Color.LTGRAY, Color.TRANSPARENT};
        Random random = new Random();
        int value = random.nextInt(ranColor.length - 1);
        return ranColor[value];
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {

    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {
        // mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        byte[] fft = data;
        byte[] model = new byte[fft.length / 2 + 1];
        if (mDataEn) {
            model[0] = (byte) Math.abs(fft[1]);
            int j = 1;
            for (int i = 2; i < fft.length; ) {
                model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
                i += 2;
                j++;
            }
        } else {
            for (int i = 0; i < CYLINDER_NUM; i++) {
                model[i] = 0;
            }
        }
        for (int i = 0; i < CYLINDER_NUM; i++) {
            final byte a = (byte) (Math.abs(model[CYLINDER_NUM - i]) / levelStep);

            final byte b = mData[i];
            if (a > b) {
                mData[i] = a;
            } else {
                if (b > 0) {
                    mData[i]--;
                }
            }
        }

        int j = -4;
        for (int i = 0; i < CYLINDER_NUM / 2 - 4; i++) {
            drawCylinder(canvas, strokeWidth / 2 + hGap + i * (hGap + strokeLength), mData[i], rect);
        }
        for (int i = CYLINDER_NUM; i >= CYLINDER_NUM / 2 - 4; i--) {
            j++;
            drawCylinder(canvas, strokeWidth / 2 + hGap + (CYLINDER_NUM / 2 + j - 1) * (hGap + strokeLength), mData[i - 1], rect);
        }
        //  mPaint.setXfermode(null);
    }
}
