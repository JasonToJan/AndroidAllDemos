package com.coocent.visualizerlib.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * desc:
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/6 16:00
 **/
public class ImageUtils {

    /**
     * 获取一张图片
     */
    public static void getImageBySystemInFragment(Fragment fragment,int requestCode){
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取一张图片
     */
    public static void getImageBySystemInActivity(Activity activity, int requestCode){
        final Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, requestCode);
    }
}
