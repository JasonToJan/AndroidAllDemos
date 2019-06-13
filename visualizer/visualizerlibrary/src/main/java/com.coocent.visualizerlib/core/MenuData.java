package com.coocent.visualizerlib.core;

import android.content.Context;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.entity.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * desc: 菜单数据资源
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/6 9:27
 **/
public class MenuData {

    /**
     * 横屏
     */
    public static final int LANDSCAPE=10;

    /**
     * 竖屏
     */
    public static final int PORTAIT=11;

    /**
     * 选择图片
     */
    public static final int CHOOSEIMAGE=12;

    /**
     * 清除图片
     */
    public static final int CLEARIMAGE=122;

    /**
     * 改变颜色
     */
    public static final int CHANGECOLOR=13;

    /**
     * 移动设备频谱页的差异01
     */
    public static final int DIFFUSION01=15;

    /**
     * 移动设备频谱页的差异02
     */
    public static final int DIFFUSION02=16;

    /**
     * 移动设备频谱页的速度01
     */
    public static final int SPEED01=17;

    /**
     * 移动设备频谱页的速度02
     */
    public static final int SPEED02=18;

    private static ArrayList<MenuItem> menuItems=new ArrayList<>();

    public static List<MenuItem> getCurrentMenus(Context context){
        switch (VisualizerManager.getInstance().visualizerDataType[VisualizerManager.getInstance().visualizerIndex]){
            case VisualizerManager.LIQUID_TYPE:
                return getLiquidMenus(context);

            case VisualizerManager.SPECTRUM2_TYPE:
                return getSPECTRUM2Menus(context);

            case VisualizerManager.COLOR_WAVES_TYPE:
                return getColorWavesMenus(context);

            case VisualizerManager.PARTICLE_TYPE:
                return getPARTICLEMenus(context);

            case VisualizerManager.NORMAL_TYPE:
                return getNormalMenus(context);

            case VisualizerManager.SPIN_TYPE:
                return getSpinMenus(context);

            case VisualizerManager.PARTICLE_IMMERSIVE:
                return getPARTICLE2Menus(context);

            case VisualizerManager.PARTICLE_VR:
                return getPARTICLEVRMenus(context);

            case VisualizerManager.LIQUID_POWER_SAVER:
                return getLiquidPowerMenus(context);
        }

        menuItems.clear();
        return menuItems;
    }

    /**
     * 获取液体频谱菜单
     * @return
     */
    public static ArrayList<MenuItem> getLiquidMenus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.LIQUID_TYPE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.LIQUID_TYPE));
            }
        }
        menuItems.add(new MenuItem(CHOOSEIMAGE,context.getResources().getString(R.string.choose_image),VisualizerManager.LIQUID_TYPE));
        menuItems.add(new MenuItem(CLEARIMAGE,context.getResources().getString(R.string.clear_image),VisualizerManager.LIQUID_TYPE));

        return menuItems;
    }

    /**
     * 获取上下频谱菜单
     * @return
     */
    public static ArrayList<MenuItem> getSPECTRUM2Menus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.SPECTRUM2_TYPE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.SPECTRUM2_TYPE));
            }
        }
        menuItems.add(new MenuItem(CHANGECOLOR,context.getResources().getString(R.string.change_color),VisualizerManager.SPECTRUM2_TYPE));

        return menuItems;
    }

    /**
     * 获取彩色波浪菜单
     * @return
     */
    public static ArrayList<MenuItem> getColorWavesMenus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.COLOR_WAVES_TYPE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.COLOR_WAVES_TYPE));
            }
        }
        return menuItems;
    }

    /**
     * 获取普通频谱菜单，蓝色频谱
     * @return
     */
    public static ArrayList<MenuItem> getNormalMenus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.NORMAL_TYPE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.NORMAL_TYPE));
            }
        }

        menuItems.add(new MenuItem(CHANGECOLOR,context.getResources().getString(R.string.change_color),VisualizerManager.NORMAL_TYPE));

        return menuItems;
    }

    /**
     * 获取小花点菜单
     * @return
     */
    public static ArrayList<MenuItem> getPARTICLEMenus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.PARTICLE_TYPE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.PARTICLE_TYPE));
            }
        }
        return menuItems;
    }

    /**
     * 获取彩虹菜单
     * @return
     */
    public static ArrayList<MenuItem> getSpinMenus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.SPIN_TYPE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.SPIN_TYPE));
            }
        }
        return menuItems;
    }

    /**
     * 获取小花点相机菜单
     * @return
     */
    public static ArrayList<MenuItem> getPARTICLE2Menus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.PARTICLE_IMMERSIVE));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.PARTICLE_IMMERSIVE));
            }
        }
//        menuItems.add(new MenuItem(DIFFUSION01,context.getResources().getString(R.string.diffusion)+":1",VisualizerManager.PARTICLE_IMMERSIVE));
//        menuItems.add(new MenuItem(DIFFUSION01,context.getResources().getString(R.string.diffusion)+":2",VisualizerManager.PARTICLE_IMMERSIVE));
//        menuItems.add(new MenuItem(SPEED01,context.getResources().getString(R.string.speed)+":1",VisualizerManager.PARTICLE_IMMERSIVE));
//        menuItems.add(new MenuItem(SPEED02,context.getResources().getString(R.string.speed)+":2",VisualizerManager.PARTICLE_IMMERSIVE));
        return menuItems;
    }

    /**
     * 获取小花点相机VR菜单
     * @return
     */
    public static ArrayList<MenuItem> getPARTICLEVRMenus(Context context){
        menuItems.clear();
//        menuItems.add(new MenuItem(DIFFUSION01,context.getResources().getString(R.string.diffusion)+":1",VisualizerManager.PARTICLE_VR));
//        menuItems.add(new MenuItem(DIFFUSION01,context.getResources().getString(R.string.diffusion)+":2",VisualizerManager.PARTICLE_VR));
//        menuItems.add(new MenuItem(SPEED01,context.getResources().getString(R.string.speed)+":1",VisualizerManager.PARTICLE_VR));
//        menuItems.add(new MenuItem(SPEED02,context.getResources().getString(R.string.speed)+":2",VisualizerManager.PARTICLE_VR));
        return menuItems;
    }

    /**
     * 获取液体频谱菜单
     * @return
     */
    public static ArrayList<MenuItem> getLiquidPowerMenus(Context context){
        menuItems.clear();
        if(VisualizerManager.getInstance().isAllowLandscape){
            if(VisualizerManager.getInstance().isLandscape){
                menuItems.add(new MenuItem(LANDSCAPE,context.getResources().getString(R.string.landscape),VisualizerManager.LIQUID_POWER_SAVER));
            }else{
                menuItems.add(new MenuItem(PORTAIT,context.getResources().getString(R.string.portrait),VisualizerManager.LIQUID_POWER_SAVER));
            }
        }
        menuItems.add(new MenuItem(CHOOSEIMAGE,context.getResources().getString(R.string.choose_image),VisualizerManager.LIQUID_POWER_SAVER));
        menuItems.add(new MenuItem(CLEARIMAGE,context.getResources().getString(R.string.clear_image),VisualizerManager.LIQUID_POWER_SAVER));
        return menuItems;
    }

}
