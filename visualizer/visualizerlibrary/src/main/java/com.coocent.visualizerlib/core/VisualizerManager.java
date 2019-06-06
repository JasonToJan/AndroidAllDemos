package com.coocent.visualizerlib.core;


import android.app.Application;
import android.content.Context;

import com.coocent.visualizerlib.entity.MenuItem;
import com.coocent.visualizerlib.inter.IControlVisualizer;
import com.coocent.visualizerlib.inter.IVisualizerMenu;
import com.coocent.visualizerlib.inter.MusicVisualizerInter;
import com.coocent.visualizerlib.utils.LogUtils;

import java.util.List;

/**
 * Description: 管理频谱类，全局单例实现
 * *
 * Creator: Wang
 * Date: 2019/6/3 21:50
 */
public class VisualizerManager {

    /**
     * 全局上下文
     */
    public Application application;
    /**
     * 当前频谱类型索引
     */
    public int visualizerIndex;
    /**
     * 当前SessionId
     */
    private int sessionId;
    /**
     * 控制频谱切换接口
     */
    public IControlVisualizer controlVisualizer;
    /**
     * 控制频谱菜单接口
     */
    public IVisualizerMenu visualizerMenu;
    /**
     * 是否允许横屏
     */
    public boolean isAllowLandscape=false;
    /**
     * 是否是横屏
     */
    public boolean isLandscape;
    /**
     * 频谱颜色，是否是绿色
     */
    public boolean isGreen;
    /**
     * 当前频谱颜色
     */
    public int currentColor;


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

    static VisualizerManager instance=null;

    private void VisualizerManager(){}

    public static VisualizerManager getInstance(){
        return SingletonTool.instance;
    }

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

    public void setControlVisualizer(IControlVisualizer controlVisualizer){
        this.controlVisualizer=controlVisualizer;
    }

    public IControlVisualizer getControlVisualizer(){
        if(controlVisualizer==null) {
            LogUtils.d("糟了，这里的controlVisualizer为空了！！！");
        }
        return controlVisualizer;
    }

    public void setVisualizerMenu(IVisualizerMenu visualizerMenu){
        this.visualizerMenu=visualizerMenu;
    }

    public IVisualizerMenu getVisualizerMenu(){
        if(visualizerMenu==null) {
            LogUtils.d("糟了，这里的controlVisualizer为空了！！！");
        }
        return visualizerMenu;
    }

    /**
     * 获取当前频谱的菜单项
     * @param context
     * @return
     */
    public List<MenuItem> getCurrentTypeMenus(Context context){
        return MenuData.getCurrentMenus(context);
    }

}
