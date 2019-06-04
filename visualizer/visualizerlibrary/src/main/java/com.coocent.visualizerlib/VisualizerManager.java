package com.coocent.visualizerlib;


import android.app.Application;

/**
 * Description: 管理应用
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

    //如果要增加在这里面增加即可
    public String[] visualizerDataType={LIQUID_TYPE,SPECTRUM2_TYPE,COLOR_WAVES_TYPE,PARTICLE_TYPE,
            NORMAL_TYPE,SPIN_TYPE};
    public static final String LIQUID_TYPE="LIQUID";
    public static final String SPECTRUM2_TYPE="SPECTRUM2";
    public static final String COLOR_WAVES_TYPE="COLOR_WAVES";
    public static final String PARTICLE_TYPE="PARTICLE";
    public static final String NORMAL_TYPE="NORMAL";
    public static final String SPIN_TYPE="SPIN";

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