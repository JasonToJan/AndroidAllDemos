//
// FPlayAndroid is distributed under the FreeBSD License
//
// Copyright (c) 2013-2014, Carlos Rafael Gimenes das Neves
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice, this
//    list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright notice,
//    this list of conditions and the following disclaimer in the documentation
//    and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
// ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
// The views and conclusions contained in the software and documentation are those
// of the authors and should not be interpreted as representing official policies,
// either expressed or implied, of the FreeBSD Project.
//
// https://github.com/carlosrafaelgn/FPlayAndroid
//
package com.coocent.visualizerlib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.coocent.visualizerlib.core.MainHandler;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.core.VisualizerService;
import com.coocent.visualizerlib.inter.IControlVisualizer;
import com.coocent.visualizerlib.inter.IVisualizer;
import com.coocent.visualizerlib.ui.UI;
import com.coocent.visualizerlib.utils.CommonUtils;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.LogUtils;
import com.coocent.visualizerlib.utils.PermissionUtils;

import br.com.carlosrafaelgn.fplay.visualizer.OpenGLVisualizerJni;


/**
 * 频谱主页
 */
public final class VisualizerSimpleActivity extends Activity implements
        VisualizerService.Observer,
        MainHandler.Callback,
        IControlVisualizer {

    //基本数据
    private boolean visualizerRequiresHiddenControls, visualizerPaused;
    private boolean isFinishChange=true;//是否已经完成了改变，重复调用会猜测会异常
    private int requiredOrientation;
    private static final int MSG_HIDE = 0x0400;
    private static final int MSG_SYSTEM_UI_CHANGED = 0x0401;
    public static final int CAMERA_PERMISSION_CODE=1001;//照相机权限回调Code
    public static final int RECORD_PERMISSION_CODE=1002;//录音权限

    private IVisualizer visualizer;
    private VisualizerService visualizerService;
    private FrameLayout visualizerRoot;


    @SuppressLint("InlinedApi")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        LogUtils.d("lib2打开了频谱页~");

        //初始化相关
        init();
        setContentView(R.layout.activity_simple_visualizer);

        initView();
        addVisualizerView();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    protected void onResume() {
        if (visualizerService != null){
            visualizerService.resetAndResume();
        }
        if (visualizer != null && visualizerPaused) {
            visualizerPaused = false;
            visualizer.onActivityResume();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        //changed from onPaused() to onStop()
        //https://developer.android.com/guide/topics/ui/multi-window.html#lifecycle
        //In multi-window mode, an app can be in the paused state and still be visible to the user.
        if (visualizer != null && !visualizerPaused) {
            visualizerPaused = true;
            visualizer.onActivityPause();
        }
        if (visualizerService != null){
            visualizerService.pause();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        finalCleanup();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IVisualizer v = visualizer;
        if (v != null){
            v.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==CAMERA_PERMISSION_CODE){
            if(PermissionUtils.hasCameraPermission(this)){
                //照相机权限获取到了，需要重新进入
                startActivity((new Intent(this, VisualizerActivity.class)).
                        putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName())
                        .putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE_VR));
            }
        }else if(requestCode==RECORD_PERMISSION_CODE){
            if(PermissionUtils.hasRecordPermission(this)){
                LogUtils.d("Activity权限请求成功");
                changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_UP){
            LogUtils.d("接收到点击事件，即将切换到下一个频谱页~");
            if(VisualizerManager.getInstance().visualizerIndex
                    ==VisualizerManager.getInstance().visualizerDataType.length-1){
                VisualizerManager.getInstance().visualizerIndex=0;
            }else{
                VisualizerManager.getInstance().visualizerIndex++;
            }

            changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onFinalCleanup() {
        if (visualizer != null) {
            if (!visualizerPaused) {
                visualizerPaused = true;
                visualizer.onActivityPause();
            }
            visualizer.releaseView();
            visualizer = null;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_HIDE:

                break;
            case MSG_SYSTEM_UI_CHANGED:

                break;
        }
        return true;
    }

    @Override
    public void nextVisualizer() {
        if(VisualizerManager.getInstance().visualizerIndex
                ==VisualizerManager.getInstance().visualizerDataType.length-1){
            VisualizerManager.getInstance().visualizerIndex=0;
        }else{
            VisualizerManager.getInstance().visualizerIndex++;
        }

        changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
    }

    @Override
    public void previousVisualizer() {
        if(VisualizerManager.getInstance().visualizerIndex ==0){
            VisualizerManager.getInstance().visualizerIndex=
                    VisualizerManager.getInstance().visualizerDataType.length-1;
        }else{
            VisualizerManager.getInstance().visualizerIndex--;
        }
        changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
    }

    @Override
    public void someVisualizer(int type) {
        changeVisualizer(type);
    }

    private void finalCleanup() {
        if (visualizerService != null) {
            visualizerService.destroy();
            visualizerService = null;
        } else if (visualizer != null) {
            visualizer.cancelLoading();
            visualizer.release();
            onFinalCleanup();
        }
    }

    /**
     * 初始化所有
     */
    private void init(){
        initManager();
        initHandlerAndUI();
        initVisualizer();
        initStatus();
    }

    /**
     * 初始化Application
     */
    private void initManager(){
        VisualizerManager.getInstance().init(this.getApplication());
        VisualizerManager.getInstance().setControlVisualizer(this);
    }

    /**
     * 初始化Handler
     */
    private void initHandlerAndUI(){
        UI.initialize(this, CommonUtils.getScreenWidth(this),CommonUtils.getScreenWidth(this));
        UI.loadCommonColors(true);
        UI.initColorDefault();//默认的相关颜色值
        MainHandler.initialize();
        PermissionUtils.requestRecordAudioPermissionInActivity(this,RECORD_PERMISSION_CODE);
    }

    /**
     * 使用反射 初始化Visualizer
     */
    private void initVisualizer(){
        String name = null;
        final Intent si = setIntent(VisualizerManager.getInstance().visualizerIndex);
        if (si != null && (name = si.getStringExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME)) != null) {
            if (!name.startsWith("br.com.carlosrafaelgn.fplay")){
                return;
            }
        }

        if(visualizer!=null){
            visualizer.release();
            visualizer=null;
        }

        visualizer = new OpenGLVisualizerJni(this, true, si);

//        if (name != null) {
//            try {
//                final Class<?> clazz = Class.forName(name);
//                if (clazz != null) {
//                    try {
//                        visualizer = (IVisualizer)clazz.getConstructor(Activity.class, boolean.class, Intent.class).newInstance(this, Constants.isLandscape, si);
//                    } catch (Throwable ex) {
//                        LogUtils.d("异常"+ex.getMessage());
//                    }
//                }
//            } catch (Throwable ex) {
//                LogUtils.d("异常"+ex.getMessage());
//            }
//        }

        final boolean visualizerRequiresThread;
        if (visualizer != null) {
            requiredOrientation = visualizer.requiredOrientation();
            visualizerRequiresHiddenControls = visualizer.requiresHiddenControls();
            visualizerRequiresThread = (visualizer.requiredDataType() != IVisualizer.DATA_NONE);
        } else {
            requiredOrientation = IVisualizer.ORIENTATION_NONE;
            visualizerRequiresHiddenControls = false;
            visualizerRequiresThread = false;
        }

        visualizerService = null;
        if (visualizer != null) {
            visualizerPaused = false;
            visualizer.onActivityResume();
            if (!visualizerRequiresThread){
                visualizer.load();
            }else{
                visualizerService = new VisualizerService(visualizer, this);
            }
        }
    }

    /**
     * 初始化状态栏相关
     */
    private void initStatus(){
        setRequestedOrientation((requiredOrientation == IVisualizer.ORIENTATION_NONE) ?
                (Constants.visualizerPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                : (requiredOrientation == IVisualizer.ORIENTATION_PORTRAIT
                ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE));

        getWindow().setBackgroundDrawable(new ColorDrawable(Constants.color_visualizer565));

        if (visualizerRequiresHiddenControls) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            if (Constants.allowPlayerAboveLockScreen){
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
            else{
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void initView(){
        visualizerRoot=findViewById(R.id.av_activity_root);
    }

    /**
     * 添加频谱视图
     */
    private void addVisualizerView(){
        if (visualizer != null) {
            VisualizerManager.getInstance().visualizerIndex=0;//默认是水波纹

            visualizerRoot.addView((View)visualizer);
        }
    }

    /**
     * 改变频谱类型
     * @param type
     */
    private void changeVisualizer(int type){

        LogUtils.d("当前选择的频谱类型为："+type+" 名称为："+VisualizerManager.getInstance().visualizerDataType[type]);
        if(!isFinishChange) return;
        isFinishChange=false;

        try{
            if (visualizer != null) {
                visualizerRoot.removeView((View) visualizer);
                visualizer.release();
                visualizer = null;
            }
            if(visualizerService!=null){
                visualizerService.pause();
                visualizerService=null;
            }
        }catch (Throwable e){
            LogUtils.d("release异常##"+e.getMessage());
        }


        Intent intent=setIntent(type);
        visualizer = new OpenGLVisualizerJni(this, true, intent);

        final boolean visualizerRequiresThread;
        if (visualizer != null) {
            requiredOrientation = visualizer.requiredOrientation();
            visualizerRequiresHiddenControls = visualizer.requiresHiddenControls();
            visualizerRequiresThread = (visualizer.requiredDataType() != IVisualizer.DATA_NONE);
        } else {
            requiredOrientation = IVisualizer.ORIENTATION_NONE;
            visualizerRequiresHiddenControls = false;
            visualizerRequiresThread = false;
        }

        visualizerService = null;
        if (visualizer != null) {
            visualizerPaused = false;
            visualizer.onActivityResume();
            if (!visualizerRequiresThread){
                visualizer.load();
            }else{
                visualizerService = new VisualizerService(visualizer, this);
            }
        }

        if (visualizer != null) {
            visualizerRoot.addView((View)visualizer);
        }

        isFinishChange=true;
        LogUtils.d("当前选择的频谱类型 切换成功结束！！！");
    }

    private Intent setIntent(int type){
        Intent intent=new Intent();
        switch (VisualizerManager.getInstance().visualizerDataType[type]){
            case VisualizerManager.LIQUID_TYPE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID);
                break;

            case VisualizerManager.SPECTRUM2_TYPE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPECTRUM2);
                break;

            case VisualizerManager.COLOR_WAVES_TYPE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_COLOR_WAVES);
                break;

            case VisualizerManager.PARTICLE_TYPE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_PARTICLE);
                break;

            case VisualizerManager.NORMAL_TYPE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                break;

            case VisualizerManager.SPIN_TYPE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_SPIN);
                break;

            case VisualizerManager.PARTICLE_IMMERSIVE:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE);
                break;

            case VisualizerManager.PARTICLE_VR:
                //需要照相机支持
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        break;
                    }
                }
                //沉浸式Particle_VR版
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE_VR);
                break;

            case VisualizerManager.LIQUID_POWER_SAVER:
                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_LIQUID_POWER_SAVER);
                break;
        }

        return intent;
    }

}
