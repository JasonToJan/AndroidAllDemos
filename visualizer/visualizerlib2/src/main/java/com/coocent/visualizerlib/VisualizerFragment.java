package com.coocent.visualizerlib;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.coocent.visualizerlib.core.MainHandler;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.core.VisualizerService;
import com.coocent.visualizerlib.inter.IControlVisualizer;
import com.coocent.visualizerlib.inter.IVisualizer;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.LogUtils;

import br.com.carlosrafaelgn.fplay.visualizer.OpenGLVisualizerJni;

/**
 * desc: 频谱片段
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/5 13:36
 **/
public class VisualizerFragment extends Fragment implements
        VisualizerService.Observer,
        MainHandler.Callback,
        IControlVisualizer {

    //基本数据
    private boolean visualizerPaused;
    private boolean isFinishChange=true;//是否已经完成了改变，重复调用会猜测会异常
    private static final int CAMERA_PERMISSION_CODE=1001;//照相机权限回调Code

    private IVisualizer visualizer;
    private VisualizerService visualizerService;
    private RelativeLayout visualizerRoot;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_layout_visualizer, container, false);

        visualizerRoot=rootView.findViewById(R.id.flv_visualizer_root);

        Bundle bundle = getArguments();
        if(bundle!=null){
            VisualizerManager.getInstance().visualizerIndex=bundle.getInt(Constants.FRAGMENT_ARGUMENTS_INDEX,0);
            LogUtils.d("Fragment中拿到数据为："+ VisualizerManager.getInstance().visualizerIndex);
        }

        initVisualizer();
        addVisualizerView();

        return rootView;
    }

    @Override
    public void onResume() {
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
    public void onStop() {
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
    public void onDestroyView() {
        finalCleanup();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
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
    public void nextVisualizer() {
        goToNextVisualizer();
    }

    @Override
    public void previousVisualizer() {
        goToPreviousVisualizer();
    }

    @Override
    public void someVisualizer(int type) {
        changeVisualizer(type);
    }

    /**
     * 初始化所有
     */
    private void init(){

        VisualizerManager.getInstance().init(getActivity().getApplication());
        VisualizerManager.getInstance().setControlVisualizer(this);

        MainHandler.initialize();
    }

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

        visualizer = new OpenGLVisualizerJni(getActivity(), true, si);

        final boolean visualizerRequiresThread;
        if (visualizer != null) {
            visualizerRequiresThread = (visualizer.requiredDataType() != IVisualizer.DATA_NONE);
        } else {
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
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

    /**
     * 改变频谱类型
     * @param type
     */
    public void changeVisualizer(int type){

        if(!isFinishChange) return;
        if(getActivity()==null) return;
        LogUtils.d("当前选择的频谱类型为："+type+" 名称为："+VisualizerManager.getInstance().visualizerDataType[type]);

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
        visualizer = new OpenGLVisualizerJni(getActivity(), true, intent);

        final boolean visualizerRequiresThread;
        if (visualizer != null) {
            visualizerRequiresThread = (visualizer.requiredDataType() != IVisualizer.DATA_NONE);
        } else {
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

    /**
     * 跳转到下一个频谱
     */
    public void goToNextVisualizer(){
        if(VisualizerManager.getInstance().visualizerIndex
                ==VisualizerManager.getInstance().visualizerDataType.length-1){
            VisualizerManager.getInstance().visualizerIndex=0;
        }else{
            VisualizerManager.getInstance().visualizerIndex++;
        }

        changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
    }

    /**
     * 跳转到上一个频谱
     */
    public void goToPreviousVisualizer(){
        if(VisualizerManager.getInstance().visualizerIndex ==0){
            VisualizerManager.getInstance().visualizerIndex=
                    VisualizerManager.getInstance().visualizerDataType.length-1;
        }else{
            VisualizerManager.getInstance().visualizerIndex--;
        }
        changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
    }

    /**
     * 添加频谱视图
     */
    private void addVisualizerView(){
        if (visualizer != null) {
            visualizerRoot.addView((View)visualizer);
        }
    }

    /**
     * 退出时释放
     */
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


}
