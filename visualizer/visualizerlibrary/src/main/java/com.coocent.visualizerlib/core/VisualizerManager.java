package com.coocent.visualizerlib.core;


import android.app.Application;
import android.content.Context;
import android.media.audiofx.Visualizer;

import com.coocent.visualizerlib.entity.MenuItem;
import com.coocent.visualizerlib.inter.IControlVisualizer;
import com.coocent.visualizerlib.inter.IVisualizerMenu;
import com.coocent.visualizerlib.inter.MusicVisualizerInter;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.LogUtils;
import com.coocent.visualizerlib.utils.VisualizerSharePareUtils;

import java.util.List;

/**
 * Description: 管理频谱类，全局单例实现
 * *
 * Creator: Wang
 * Date: 2019/6/3 21:50
 */
public class VisualizerManager {

    public Visualizer oneVisualizer;

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
     * Fragment是否展示菜单
     */
    public boolean isShowFragmentMenu=true;
    /**
     * Activity是否展示菜单
     */
    public boolean isShowActivityMenu=true;
    /**
     * Fragment点击是否到下一个频谱
     */
    public boolean isClickNextForFragment=true;
    /**
     * 记录类型1选择的url
     */
    public String liquidType1Url="";
    /**
     * 记录类型2选择的url
     */
    public String liquidType2Url="";


    //实际的展示频谱类型，如果要增加在这里面增加即可
    public String[] visualizerDataType={
            LIQUID_TYPE,
            COLOR_WAVES_TYPE,
            PARTICLE_IMMERSIVE,
            SPECTRUM2_TYPE,
            NORMAL_TYPE,
            PARTICLE_TYPE,
            LIQUID_POWER_SAVER,
            SPIN_TYPE

//            PARTICLE_VR,

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

    public void setIsShowFragmentMenu(boolean isShowFragmentMenu){
        this.isShowFragmentMenu=isShowFragmentMenu;
    }

    public boolean getIsShowFragmentMenu(){
        return isShowFragmentMenu;
    }

    public void setIsShowActivityMenu(boolean isShowActivityMenu){
        this.isShowActivityMenu=isShowActivityMenu;
    }

    public boolean getIsShowActivityMenu(){
        return isShowActivityMenu;
    }

    /**
     * 外部设置频谱类型，以及默认索引
     * @param visualizerDataType
     * @param defaultIndex
     */
    public void setVisualizerDataType(String[] visualizerDataType,int defaultIndex){
        this.visualizerDataType=visualizerDataType;
        this.visualizerIndex=defaultIndex;
    }

    /**
     * 获取液体类型1选择的url
     * @return
     */
    public String getLiquidType1Url() {
         if(liquidType1Url!=null&&liquidType1Url.equals("")){
             //第一次进来，默认为“”，读一下sp
             if(VisualizerManager.getInstance().getApplication()!=null){
                 liquidType1Url=(String) VisualizerSharePareUtils.getParam(VisualizerManager.getInstance().getApplication(),
                         VisualizerSharePareUtils.URL_FOR_LIQUID_TYPE, Constants.DEFUALUT_URL_EMPTY);

                 if(liquidType1Url.equals(Constants.DEFUALUT_URL_EMPTY)) liquidType1Url=null;

                 return liquidType1Url;
             }
         }

         return liquidType1Url;
    }

    public void setLiquidType1Url(String liquidType1Url) {
        this.liquidType1Url = liquidType1Url;
        //记录到文件
        if(VisualizerManager.getInstance().getApplication()!=null&&liquidType1Url!=null){

            VisualizerSharePareUtils.setParam(VisualizerManager.getInstance().getApplication(),
                    VisualizerSharePareUtils.URL_FOR_LIQUID_TYPE,(String)liquidType1Url);
        }
    }

    /**
     * 获取液体类型2选择的url
     * @return
     */
    public String getLiquidType2Url() {
        if(liquidType2Url!=null&&liquidType2Url.equals("")){
            //第一次进来，默认为“”，读一下sp
            if(VisualizerManager.getInstance().getApplication()!=null){
                liquidType2Url=(String) VisualizerSharePareUtils.getParam(VisualizerManager.getInstance().getApplication(),
                        VisualizerSharePareUtils.URL_FOR_LIQUID_POWER_SAVER, Constants.DEFUALUT_URL_EMPTY);

                if(liquidType2Url.equals(Constants.DEFUALUT_URL_EMPTY)) liquidType2Url=null;

                return liquidType2Url;
            }
        }

        return liquidType2Url;
    }

    public void setLiquidType2Url(String liquidType2Url) {
        this.liquidType2Url = liquidType2Url;
        //记录到文件
        if(VisualizerManager.getInstance().getApplication()!=null&&liquidType2Url!=null){
            VisualizerSharePareUtils.setParam(VisualizerManager.getInstance().getApplication(),
                    VisualizerSharePareUtils.URL_FOR_LIQUID_POWER_SAVER,(String)liquidType2Url);
        }

    }

    public Visualizer getOneVisualizer(boolean forceNewVisualizer) {
        if(oneVisualizer==null||forceNewVisualizer){
            LogUtils.d("这里，直接new 一个Visualizer");
            oneVisualizer=new Visualizer(sessionId);
        }
        return oneVisualizer;
    }

    public void setOneVisualizer(Visualizer oneVisualizer) {
        this.oneVisualizer = oneVisualizer;
    }
}
