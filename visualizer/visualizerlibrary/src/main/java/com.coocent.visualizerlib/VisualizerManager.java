package com.coocent.visualizerlib;


import android.app.Application;

import com.coocent.visualizerlib.inter.MusicVisualizerInter;
import com.coocent.visualizerlib.utils.LogUtils;

/**
 * Description: 管理频谱类，全局单例实现
 * *
 * Creator: Wang
 * Date: 2019/6/3 21:50
 */
public class VisualizerManager {

    static VisualizerManager instance=null;

    public Application application;

    private void VisualizerManager(){}

    public static VisualizerManager getInstance(){
        return SingletonTool.instance;
    }

    public int sessionId;

    public int visualizerIndex;//第0个为水波纹，第1个上下波纹，第2个波浪，第3个小花点,第4个中间蓝色波纹，第5个彩虹

    //实际的展示频谱类型，如果要增加在这里面增加即可
    public String[] visualizerDataType={
            LIQUID_TYPE,
            SPECTRUM2_TYPE,
            COLOR_WAVES_TYPE,
            NORMAL_TYPE,
            PARTICLE_TYPE,
            SPIN_TYPE,
            PARTICLE_IMMERSIVE,
            PARTICLE_VR,
            LIQUID_POWER_SAVER
    };

    //所有类型
    public static final String LIQUID_TYPE="LIQUID";                     //液体，支持更换图片
    public static final String SPECTRUM2_TYPE="SPECTRUM2";               //上下部波纹（New）
    public static final String COLOR_WAVES_TYPE="COLOR_WAVES";           //多彩波浪（New）
    public static final String PARTICLE_TYPE="PARTICLE";                 //中间小花点
    public static final String NORMAL_TYPE="NORMAL";                     //正常，中部蓝色波纹
    public static final String SPIN_TYPE="SPIN";                         //彩虹
    public static final String PARTICLE_IMMERSIVE="PARTICE_IMMERSIVE";   //需要移动设备
    public static final String PARTICLE_VR="PARTICE_VR";                 //需要照相机支持（New）
    public static final String LIQUID_POWER_SAVER="LIQUIE_POWER_SAVER";  //液体，效果更圆滑（New）


    //下面是所有类型，根据需要添加在上面实际中的展示频谱类型即可，想查看效果，直接复制到实际的展示频谱即可
    public String[] visualizerDataType1={
            LIQUID_TYPE,
            SPECTRUM2_TYPE,
            COLOR_WAVES_TYPE,
            NORMAL_TYPE,
            PARTICLE_TYPE,
            SPIN_TYPE,
            PARTICLE_IMMERSIVE,
            PARTICLE_VR,
            LIQUID_POWER_SAVER,
    };


    /**
     * 音乐接口，需要在服务中自行设置
     */
    public MusicVisualizerInter musicVisualizerInter;


    /****静态内部类****/
    private static class SingletonTool{
        private static final VisualizerManager instance=new VisualizerManager();
    }

    public void init(Application application){
        this.application=application;
    }

    public Application getApplication(){
        if(application==null) {
            LogUtils.d("糟了，这里的Application为空了！！！");
        }
        return application;
    }

    public int getSessionId(){
        return sessionId;
    }

    public void setSessionId(int sessionId){
        this.sessionId=sessionId;
    }

    /**
     * 设置接口，在服务中设置进去
     * @param musicVisualizerInter
     */
    public void setMusicVisualizerInter(MusicVisualizerInter musicVisualizerInter){
        this.musicVisualizerInter=musicVisualizerInter;
    }

    public MusicVisualizerInter getMusicVisualizerInter(){
        if(musicVisualizerInter==null){
            LogUtils.d("糟了，这里的MusicVisualizerInter为空了！！！");
        }
        return musicVisualizerInter;
    }

}
