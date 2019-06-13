package com.coocent.visualizerlib.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * desc: 权限工具
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/6 13:36
 **/
public class PermissionUtils {

    /**
     * 请求录音权限,在Activity中
     */
    public static void requestRecordAudioPermissionInActivity(Activity activity,int requestCode){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
                LogUtils.d("activity这里开始申请权限了");
                return;
            }
        }
    }

    /**
     * 请求录音权限，在Fragment中
     */
    public static void requestRecordAudioPermissionInFragment(Fragment fragment,int requestCode){
        if(fragment.getActivity()==null) return;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragment.getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, requestCode);
                LogUtils.d("activity这里开始申请权限了");
                return;
            }
        }
    }

    /**
     * 请求读写权限,在Activity中
     */
    public static void requestWriteAndReadPermissionInActivity(Activity activity,int requestCode){
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                LogUtils.d("activity这里开始申请读写权限了");
                return;
            }
        }
    }

    /**
     * 请求读写权限，在Fragment中
     */
    public static void requestWriteAndReadPermissionInFragment(Fragment fragment,int requestCode){
        if(fragment.getActivity()==null) return;
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragment.getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ||fragment.getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                LogUtils.d("Fragment这里开始申请读写权限了");
                return;
            }
        }
    }

    /**
     * 检测是否有录音权限
     * @return
     */
    public static boolean hasRecordPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 检测是否有相机权限
     * @return
     */
    public static boolean hasCameraPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    /**
     * 检测文件读写权限，保证可以更换图片
     * @return
     */
    public static boolean hasWriteAndReadPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.checkSelfPermission(context,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }


}
