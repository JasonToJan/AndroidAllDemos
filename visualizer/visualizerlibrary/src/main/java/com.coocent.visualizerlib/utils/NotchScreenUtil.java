package com.coocent.visualizerlib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;

import java.lang.reflect.Method;

public final class NotchScreenUtil {

    /**
     * 华为start
     */
    // 判断是否是华为刘海屏
    public static boolean hasNotchInScreenAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (Boolean) get.invoke(HwNotchSizeUtil);
            Log.d("NotchScreenUtil", "this Huawei device has notch in screen？"+ret);
        } catch (ClassNotFoundException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen NoSuchMethodException", e);
        } catch (Exception e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen Exception", e);
        }
        return ret;
    }

    /**
     * 获取华为刘海的高
     * @param context
     * @return
     */
    public static int getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[] { 0, 0 };
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
            
        } catch (ClassNotFoundException e) {
            Log.e("NotchScreenUtil", "getNotchSize ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e("NotchScreenUtil", "getNotchSize NoSuchMethodException");
        } catch (Exception e) {
            Log.e("NotchScreenUtil", "getNotchSize Exception");
        }
        return ret[1];
    }

    /**
     * 华为end
     */

    /**
     * Oppo start
     */
    public static boolean hasNotchInScreenAtOppo(Context context) {
        boolean hasNotch = context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        Log.d("NotchScreenUtil", "this OPPO device has notch in screen？"+hasNotch);
        return hasNotch;
    }

    public static int getNotchSizeAtOppo() {
        return 80;
    }

    /**
     * Oppo end
     */

    /**
     * vivo start
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;// 是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;// 是否有圆角

    public static boolean hasNotchInScreenAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> FtFeature = cl.loadClass("com.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (Boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);
            Log.d("NotchScreenUtil", "this VIVO device has notch in screen？" + ret);
        } catch (ClassNotFoundException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen NoSuchMethodException", e);
        } catch (Exception e) {
            Log.e("NotchScreenUtil", "hasNotchInScreen Exception", e);
        }
        return ret;
    }
    
    public static int getNotchSizeAtVivo(Context context){
        return dp2px(context, 32);
    }

    /**
     * vivo end
     */


    /**
     * dp转px
     * @param context
     * @param dpValue
     * @return
     */
    private static int dp2px(Context context, int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,context.getResources().getDisplayMetrics());
    }
    
    /** 
     * 获取手机厂商 
     * 
     * @return  手机厂商 
     */  
    
    public final static int DEVICE_BRAND_OPPO = 0x0001;
    public final static int DEVICE_BRAND_HUAWEI = 0x0002;
    public final static int DEVICE_BRAND_VIVO = 0x0003;
    public final static int DEVICE_BRAND_XIAOMI=0x0004;
    
    
    @SuppressLint("DefaultLocale")
    public static int getDeviceBrand() {
        String brand = android.os.Build.BRAND.trim().toUpperCase();
        if (brand.contains("HUAWEI")) {
            Log.d("device brand", "HUAWEI");
            return DEVICE_BRAND_HUAWEI;
        }else if (brand.contains("OPPO")) {
            Log.d("device brand", "OPPO");
            return DEVICE_BRAND_OPPO;
        }else if (brand.contains("VIVO")) {
            Log.d("device brand", "VIVO");
            return DEVICE_BRAND_VIVO;
        }else if(brand.contains("XIAOMI")){
            Log.d("device brand","XIAOMI");
            return DEVICE_BRAND_XIAOMI;
        }
        return 0;
    }

    /**
     * 获取刘海屏高度
     */
    public static int getNotchScreenHeight(Context context){
        switch (getDeviceBrand()){
            case DEVICE_BRAND_HUAWEI:
                getNotchSizeAtHuawei(context);
                break;
            case DEVICE_BRAND_OPPO:
                getNotchSizeAtOppo();
                break;

            case DEVICE_BRAND_VIVO:
                getNotchSizeAtVivo(context);
                break;

            case DEVICE_BRAND_XIAOMI:

                break;

            default:

                break;
        }

        return 0;
    }

}