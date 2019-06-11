package com.coocent.visualizerlib.test;

import android.content.Context;

import com.coocent.visualizerlib.test.renderer.CirclePointRenderer;
import com.coocent.visualizerlib.test.renderer.PinkCircleRenderer;
import com.coocent.visualizerlib.test.renderer.RotateRenderer;
import com.coocent.visualizerlib.test.renderer.SparkCircleRenderer;
import com.coocent.visualizerlib.test.renderer.SparkRenderer;
import com.coocent.visualizerlib.test.renderer.VisualizerView;


/**

 */
public class PlayRendererUtils {
    private Context mContext;
    public static final int PINK_CIRCLE = 0;
    public static final int ROTATE = 1;
    public static final int CIRCLE_POINT = 2;
  //  public static final int SPARK_CIRCLE = 3;
  //  public static final int SPARK = 4;
    public static final int NULL = 5;

    public PlayRendererUtils(Context context) {
        this.mContext = context;
    }

    public void addSpectrum(VisualizerView mVisualizerView, int type, int color) {
       // LogUtils.e("PlayRendererUtils"," nsc =="+mVisualizerView + " type="+type);
        if (mVisualizerView == null) return;
        mVisualizerView.clearRenderers();
        switch (type) {
            case PINK_CIRCLE:
                addPinkCircle(mVisualizerView,color);
                break;

            case ROTATE:
                addRotateRenderer(mVisualizerView,color);
                break;

            case CIRCLE_POINT:
                addCirclePointRenderer(mVisualizerView,color);
                break;

//            case SPARK_CIRCLE:
//                addSparkCircleRenderer(mVisualizerView,color);
//                break;
//
//            case SPARK:
//                addSparkRenderer(mVisualizerView);
//                break;

            case NULL:

                break;

        }

    }

    /**
     * @param visualizerView
     */
    public void addPinkCircle(VisualizerView visualizerView,int color) {

        PinkCircleRenderer pinkCircleRenderer = new PinkCircleRenderer(mContext,color);
        visualizerView.addRenderer(pinkCircleRenderer);
    }

    public void addRotateRenderer(VisualizerView visualizerView,int color) {
        int initCircle = 12;
        RotateRenderer rotateRenderer = new RotateRenderer(mContext,initCircle,color);
        rotateRenderer.layout(visualizerView.getWidth(), visualizerView.getHeight());
        //rotateRenderer.setPlaying(visualizerView.isPlay());
        visualizerView.addRenderer(rotateRenderer);

    }

    public void addCirclePointRenderer(VisualizerView visualizerView,int color) {
        CirclePointRenderer circlePointRenderer = new CirclePointRenderer(mContext,color);
        visualizerView.addRenderer(circlePointRenderer);
    }

    public void addSparkCircleRenderer(VisualizerView visualizerView,int color) {
        SparkCircleRenderer sparkCircleRenderer = new SparkCircleRenderer(mContext,color);
        visualizerView.addRenderer(sparkCircleRenderer);
    }

    /**
     * 鐖嗙偢鏁堟灉
     *
     * @param visualizerView
     */
    public void addSparkRenderer(VisualizerView visualizerView) {
        //speed 瓒婂ぇ閫熷害瓒婂揩 锛宺adius 鐩稿弽
        SparkRenderer customRenderer = new SparkRenderer(0.005, 0.9f);
        visualizerView.addRenderer(customRenderer);
    }
}
