package com.coocent.visualizerlib.utils;

import android.app.Activity;
import android.content.Intent;

import com.coocent.visualizerlib.ActivityVisualizer;
import com.coocent.visualizerlib.VisualizerManager;

/**
 * Description: 跳转到频谱类型页面
 * *
 * Creator: Wang
 * Date: 2019/6/4 21:28
 */
public class KeepToVisualizerUtils {

    /**
     * 跳转默认的频谱页，暂时默认水滴为第一个
     * @param activity
     * @param type 根据自己在VisualizerManager中设置的来
     */
    public static void keepToVisualizerActivity(Activity activity,int type){
        if(type>=0&&type<=VisualizerManager.getInstance().visualizerDataType.length-1){
            VisualizerManager.getInstance().visualizerIndex=type;
        }else{
            type=0;
        }
        VisualizerManager.getInstance().visualizerIndex=type;
        activity.startActivity(new Intent(activity, ActivityVisualizer.class));
    }

//    /**
//     * 跳转到某个频谱类型页面
//     * 之后，则是根据ApplicationProxy中的类型数据循环切换
//     */
//    public static void keepToVisualizerType(Activity activity, int visulaizerType){
//        switch(visulaizerType){
//            case 1:
//                //简单频谱
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, SimpleVisualizerJni.class.getName()));
//                break;
//
//            case 2:
//                //OpenGL类型频谱
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName()));
//                break;
//
//            case 3:
//                //上下部分
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPECTRUM2));
//                break;
//
//            case 4:
//                //液体
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID));
//                break;
//
//            case 5:
//                //液体升级版
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID_POWER_SAVER));
//                break;
//
//            case 6:
//                //多彩版
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_COLOR_WAVES));
//                break;
//            case 7:
//                //spin版
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPIN));
//                break;
//            case 8:
//                //Particle版
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_PARTICLE));
//                break;
//            case 9:
//                //沉浸式Particle版
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE));
//                break;
//            case 10:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
//                        return;
//                    }
//                }
//                //沉浸式Particle_VR版
//                activity.startActivity((new Intent(activity, ActivityVisualizer.class)).
//                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
//                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE_VR));
//                break;
//
//        }
//    }
}
