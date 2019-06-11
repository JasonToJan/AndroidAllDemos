package com.coocent.visualizerlib.test.renderer.particle;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class RainItem {

    private int height;
    private int width;
    private float startx;
    private float starty;
    private float stopx;
    private float stopy;
    private float sizex;
    private float sizey;
    private float of = 0.5f;
    private Paint paint;
    private Random random = new Random();

    public RainItem(int height, int width) {
        this.height = height;
        this.width = width;
        init();
    }

    public void init() {

        //startx和y对应的分别是起止位置
        sizex = 1 + random.nextInt(10);
        sizey = 10 + random.nextInt(20);
        startx = random.nextInt(width);
        starty = random.nextInt(height);
        stopx = startx + sizex;
        stopy = starty + sizey;
        of = (float) (0.2 + random.nextFloat());
        paint = new Paint();
    }

    /**
     * 绘画雨滴
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        paint.setColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        canvas.drawLine(startx, starty, stopx, stopy, paint);
    }

    /**
     * 雨滴的移动行为
     */
    public void movestep() {
        //size*of这个是用来控制速度,所谓的速度就是线条增加的速度
        startx += sizex * of;
        stopx += sizex * of;

        starty += sizey * of;
        stopy += sizey * of;
        //如果超出边界则重新运行
        if (starty > height) {
            init();
        }
    }
}