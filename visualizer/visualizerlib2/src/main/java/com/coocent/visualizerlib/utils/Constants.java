package com.coocent.visualizerlib.utils;

import android.graphics.Color;

/**
 * desc: 一些常量定义
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/5 13:31
 **/
public class Constants {

    public static int _1dp=1;

    public static int color_visualizer= Color.BLACK;

    public static int screenWidth=1080;

    public static int screenHeight=1920;

    public static int color_visualizer565=Color.BLACK;

    public static boolean visualizerPortrait=true;

    public static boolean allowPlayerAboveLockScreen=true;

    public static boolean isLandscape=false;

    /**
     * 传递频谱类型索引，按照VisualizerManager中的Type来
     */
    public static String FRAGMENT_ARGUMENTS_INDEX="Visualizer_Fragment_Index";//给Fragment封装的参数,第一次展示的频谱类型
}
