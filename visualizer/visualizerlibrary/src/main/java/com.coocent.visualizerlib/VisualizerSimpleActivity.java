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
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coocent.visualizerlib.core.MainHandler;
import com.coocent.visualizerlib.core.MenuData;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.core.VisualizerService;
import com.coocent.visualizerlib.entity.MenuItem;
import com.coocent.visualizerlib.inter.IControlVisualizer;
import com.coocent.visualizerlib.inter.IVisualizer;
import com.coocent.visualizerlib.ui.UI;
import com.coocent.visualizerlib.utils.CommonUtils;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.ImageUtils;
import com.coocent.visualizerlib.utils.LogUtils;
import com.coocent.visualizerlib.utils.PermissionUtils;
import com.coocent.visualizerlib.view.CustomPopWindow;

import java.util.List;

import br.com.carlosrafaelgn.fplay.visualizer.OpenGLVisualizerJni;


/**
 * 频谱主页
 */
public final class VisualizerSimpleActivity extends AppCompatActivity implements
        VisualizerService.Observer,
        MainHandler.Callback,
        IControlVisualizer,View.OnClickListener {

    //基本数据
    private boolean visualizerRequiresHiddenControls, visualizerPaused;
    private boolean isFinishChange=true;//是否已经完成了改变，重复调用会猜测会异常
    private int requiredOrientation;
    private static final int MSG_HIDE = 0x0400;
    private static final int MSG_SYSTEM_UI_CHANGED = 0x0401;
    public static final int CAMERA_PERMISSION_CODE=1001;//照相机权限回调Code
    public static final int RECORD_PERMISSION_CODE=1002;//录音权限
    public static final int CHOOSEIMAGE_CODE=10011;
    public static final int READ_WRITE_PERMISSION_CODE=10012;

    private IVisualizer visualizer;
    private VisualizerService visualizerService;
    private RelativeLayout visualizerContainer;
    private CustomPopWindow mListPopWindow;
    private ImageView visualizerMenu;
    private View clickView;


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
        if(requestCode==CHOOSEIMAGE_CODE){
            if (resultCode == Activity.RESULT_OK){
                Uri selectedUri = ((Intent)data).getData();
                LogUtils.d("Simple返回图片URI为："+selectedUri);
                if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                    VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(selectedUri);
                }
            }
        }
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
        }else if(requestCode==READ_WRITE_PERMISSION_CODE){
            if(PermissionUtils.hasWriteAndReadPermission(VisualizerSimpleActivity.this)){
                ImageUtils.getImageBySystemInActivity(VisualizerSimpleActivity.this,CHOOSEIMAGE_CODE);
            }
        }
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
        visualizerContainer=findViewById(R.id.asv_visualizer_containner);
        clickView=findViewById(R.id.asv_visualizer_click_view);
        visualizerMenu=findViewById(R.id.asv_visualizer_more_iv);

        visualizerMenu.setOnClickListener(this);
        clickView.setOnClickListener(this);
    }

    /**
     * 添加频谱视图
     */
    private void addVisualizerView(){
        if (visualizer != null) {
            VisualizerManager.getInstance().visualizerIndex=VisualizerManager.getInstance().visualizerIndex;

            visualizerContainer.addView((View)visualizer);

            if(VisualizerManager.getInstance().getIsShowActivityMenu()
                    &&VisualizerManager.getInstance().getCurrentTypeMenus(this).size()>0){
                visualizerMenu.bringToFront();
                visualizerMenu.setVisibility(View.VISIBLE);
            }else{
                visualizerMenu.setVisibility(View.GONE);
            }
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
                visualizerContainer.removeView((View) visualizer);
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
            visualizerContainer.addView((View)visualizer);

            if(VisualizerManager.getInstance().getIsShowActivityMenu()
                    &&VisualizerManager.getInstance().getCurrentTypeMenus(this).size()>0){
                visualizerMenu.bringToFront();
                visualizerMenu.setVisibility(View.VISIBLE);
            }else{
                visualizerMenu.setVisibility(View.GONE);
            }
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

    @Override
    public void onClick(View v) {
        if(v==visualizerMenu){
            showPopup(v);
        }else if(v==clickView){
            nextVisualizer();
        }
    }

    public void showPopup(View view){

        if(mListPopWindow!=null){
            mListPopWindow.dissmiss();
            mListPopWindow=null;
        }

        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_list_visualizer_menu,null);
        handleListView(contentView);
        mListPopWindow= new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .setOutsideTouchable(true)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(view,0,20);

    }

    private void handleListView(View contentView){
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        MyAdapter adapter = new MyAdapter(this,VisualizerManager.getInstance().getCurrentTypeMenus(this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public class MyAdapter extends RecyclerView.Adapter{

        private List<MenuItem> mData;
        private Context mContext;

        public MyAdapter(Context mContext,List<MenuItem> datas) {
            mData = datas;
            this.mContext = mContext;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fragment_pop_menu,null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ((ViewHolder) holder).itemTextView.setText(mData.get(position).getDescription());
            ((ViewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mData.get(position).getMenuCode()==MenuData.CHOOSEIMAGE){
                        if(mListPopWindow!=null){
                            mListPopWindow.dissmiss();
                        }
                        //判断有没有权限
                        if(PermissionUtils.hasWriteAndReadPermission(VisualizerSimpleActivity.this)){
                            ImageUtils.getImageBySystemInActivity(VisualizerSimpleActivity.this,CHOOSEIMAGE_CODE);
                        }else{
                            PermissionUtils.requestWriteAndReadPermissionInActivity(VisualizerSimpleActivity.this,READ_WRITE_PERMISSION_CODE);
                        }

                    }else if(mData.get(position).getMenuCode()==MenuData.CHANGECOLOR){
                        if(mListPopWindow!=null){
                            mListPopWindow.dissmiss();
                        }
                        if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                            VisualizerManager.getInstance().getVisualizerMenu().changeColor();
                        }
                    }else if(mData.get(position).getMenuCode()==MenuData.CLEARIMAGE){
                        //清除图片
                        if(mListPopWindow!=null){
                            mListPopWindow.dissmiss();
                        }
                        if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                            VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(null);
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mData == null ? 0:mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView itemTextView;
            RelativeLayout root;
            public ViewHolder(View itemView) {
                super(itemView);
                itemTextView = (TextView) itemView.findViewById(R.id.list_pop_tv);
                root=itemView.findViewById(R.id.list_pop_rl);
            }
        }
    }

}
