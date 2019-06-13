package com.coocent.visualizerlib.utils;

import android.app.Activity;
import android.content.Intent;

import com.coocent.visualizerlib.VisualizerActivity;
import com.coocent.visualizerlib.VisualizerSimpleActivity;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.test.TestFragementActivity;

/**
 * Description: 跳转到频谱类型页面
 * *
 * Creator: Wang
 * Date: 2019/6/4 21:28
 */
public class KeepToUtils {

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
        activity.startActivity(new Intent(activity, VisualizerActivity.class));
    }

    /**
     * 跳转默认的频谱页，暂时默认水滴为第一个
     * @param activity
     * @param type 根据自己在VisualizerManager中设置的来
     */
    public static void keepToSimpleVisualizerActivity(Activity activity,int type){
        if(type>=0&&type<=VisualizerManager.getInstance().visualizerDataType.length-1){
            VisualizerManager.getInstance().visualizerIndex=type;
        }else{
            type=0;
        }
        VisualizerManager.getInstance().visualizerIndex=type;
        activity.startActivity(new Intent(activity, VisualizerSimpleActivity.class));
    }

    /**
     * 跳转测试页面，测试Fragment类型频谱，暂时默认水滴为第一个
     * @param activity
     * @type 根据自己在VisualizerManager中设置的来
     */
    public static void keepToVisualizerFragment(Activity activity,int type){
        if(type>=0&&type<=VisualizerManager.getInstance().visualizerDataType.length-1){
            VisualizerManager.getInstance().visualizerIndex=type;
        }else{
            type=0;
        }
        VisualizerManager.getInstance().visualizerIndex=type;
        activity.startActivity(new Intent(activity, TestFragementActivity.class));
    }

}
