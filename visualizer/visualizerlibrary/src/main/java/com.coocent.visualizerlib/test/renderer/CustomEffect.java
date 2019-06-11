package com.coocent.visualizerlib.test.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.SystemClock;


import com.coocent.visualizerlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**

 */
public class CustomEffect extends Renderer implements Runnable{
    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    public CustomEffect(Paint mPaint) {
        this.mPaint = mPaint;
      //  new Thread(this).start();
    }

    public void layout(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        LogUtils.e("nsc", "width =" + width + " mHeight=" + mHeight);
    }


    @Override
    public void onAudioRender(Canvas canvas, byte[] data, Rect rect) {
        this.canvas = canvas;

        for (int i = 0; i < data.length - 1; i++) {
            float[] cartPoint = {
                    (float) i / (data.length - 1),
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

        canvas.drawLines(mPoints, mPaint);
    }

    @Override
    public void onFftRender(Canvas canvas, byte[] data, Rect rect) {

    }

    float modulation = 0;
    float aggresive = 0.93f;

    private float[] toPolar(float[] cartesian, Rect rect) {
        double cX = rect.width() / 2;
        double cY = rect.height() / 2;
        double angle = (cartesian[0]) * 2 * Math.PI;
        double radius = ((rect.width() / 2) * (1 - aggresive) + aggresive * cartesian[1] / 2) * (1.2 + Math.sin(modulation)) / 2.2;
        float[] out = {
                (float) (cX + radius * Math.sin(angle)),
                (float) (cY + radius * Math.cos(angle))
        };
        return out;
    }


    private double moveSpeed = 0.2;
    private static Long startTime = System.currentTimeMillis();
    private static List<Triangle> triangleList = new ArrayList<>();
    private static int addTriangleOnece = 5;
    //添加两次三角形的间隔
    private static int addTriangleInterval = 500;
    private int allTriangleCount = 500;

    private void manageTriangle(int distence, Canvas canvas) {
        Iterator iter = triangleList.iterator();
        while (iter.hasNext()) {
            Triangle triangle = (Triangle) iter.next();
            if (triangle.isOut(mWidth, mHeight)) {
                iter.remove();
            } else {
                triangle.move(distence);
            }
            drawTriangle(canvas, triangle, Color.argb(255, 222, 92, 143));
        }

        if (System.currentTimeMillis() - startTime > addTriangleInterval && triangleList.size() < allTriangleCount) {
            for (int i = 0; i < addTriangleOnece; i++) {
                triangleList.add(Triangle.getRandomTriangle(mWidth / 2, mHeight / 2));
            }
            startTime = System.currentTimeMillis();
        }
    }

    public void drawTriangle(Canvas canvas, Triangle triangle, int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAlpha(getAlpha(triangle));
        paint.setStrokeWidth(5);

        Path path = new Path();
        path.moveTo(triangle.topPoint1.x, triangle.topPoint1.y);
        path.lineTo(triangle.topPoint2.x, triangle.topPoint2.y);
        path.lineTo(triangle.topPoint3.x, triangle.topPoint3.y);
        path.close();
        canvas.drawPath(path, paint);
    }

    public int getAlpha(Triangle triangle) {
        double distence1 = Math.sqrt(Math.pow((triangle.topPoint1.x - mWidth / 2), 2) + Math.pow((triangle.topPoint1.y - mHeight / 2), 2));
        double distence2 = Math.sqrt(Math.pow((triangle.topPoint2.x - mHeight / 2), 2) + Math.pow((triangle.topPoint2.y - mHeight / 2), 2));
        double distence3 = Math.sqrt(Math.pow((triangle.topPoint3.x - mWidth / 2), 2) + Math.pow((triangle.topPoint3.y - mHeight / 2), 2));

        double distence = Math.max(Math.max(distence1, distence2), distence3);

        if (distence < mWidth * (1.5 / 5)) {
            return 255;
        } else {
            double alpha = ((-1275 / (2 * (double) mWidth)) * distence + 1275 / 2) - 280;
            if (alpha < 0) {
                alpha = 0;
            }
            return (int) alpha;
        }
    }
    Canvas canvas = null;
    private static int refreshTime = 20;
    @Override
    public void run() {
        if (canvas==null)return;
        long t = System.currentTimeMillis();
        try {
            //canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            manageTriangle((int) ((System.currentTimeMillis() - t) * moveSpeed), canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SystemClock.sleep(Math.max(refreshTime - (System.currentTimeMillis() - t), 0));
        }
    }


}
