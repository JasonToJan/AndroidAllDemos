package com.coocent.visualizerlib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * desc: 频谱片段
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/5 13:36
 **/
public class VisualizerFragment extends Fragment implements
        VisualizerService.Observer,
        MainHandler.Callback,
        IControlVisualizer,
        View.OnClickListener{

    //基本数据
    private boolean visualizerPaused;
    private boolean isFinishChange=true;//是否已经完成了改变，重复调用会猜测会异常
    public static final int CAMERA_PERMISSION_CODE=1001;//照相机权限回调Code
    public static final int RECORD_AUDIO_CODE = 1002;//录音权限回调Code
    public static final int CHOOSEIMAGE_CODE=10011;
    public static final int READ_WRITE_PERMISSION_CODE=10012;

    private IVisualizer visualizer;
    private VisualizerService visualizerService;
    private RelativeLayout visualizerRoot;
    private View viewClick;
    private ImageView visualizerMenu;
    private CustomPopWindow mListPopWindow;


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
        visualizerMenu=rootView.findViewById(R.id.flv_visualizer_more_iv);
        viewClick=rootView.findViewById(R.id.flv_visualizer_click_view);

        visualizerMenu.setOnClickListener(this);
        viewClick.setOnClickListener(this);

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
//        if (visualizerService != null){
//            visualizerService.resetAndResume();
//        }
//        if (visualizer != null && visualizerPaused) {
//            visualizerPaused = false;
//            visualizer.onActivityResume();
//        }

        super.onResume();
    }

    @Override
    public void onStop() {
//        if (visualizer != null && !visualizerPaused) {
//            visualizerPaused = true;
//            visualizer.onActivityPause();
//        }
//        if (visualizerService != null){
//            visualizerService.pause();
//        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==RECORD_AUDIO_CODE){
            if(PermissionUtils.hasRecordPermission(getActivity())){
                LogUtils.d("Fragment权限请求成功");
                changeVisualizer(VisualizerManager.getInstance().visualizerIndex);
            }
        }else if(requestCode==READ_WRITE_PERMISSION_CODE){
            if(PermissionUtils.hasWriteAndReadPermission(getActivity())){
                ImageUtils.getImageBySystemInFragment(VisualizerFragment.this,CHOOSEIMAGE_CODE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSEIMAGE_CODE){
            if (resultCode == Activity.RESULT_OK){
                Uri selectedUri = ((Intent)data).getData();
                if(selectedUri==null){
                    selectedUri= ImageUtils.geturi(getActivity(),data);
                }
                LogUtils.d("Fragment返回图片URI为："+selectedUri);
                if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                    VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(selectedUri);
                }
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onClick(View v) {
        if(v==visualizerMenu){
            showPopup(v);
        }else if(v==viewClick){
            if(VisualizerManager.getInstance().isClickNextForFragment){
                nextVisualizer();
            }
        }
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
        UI.initialize(getActivity(), CommonUtils.getScreenWidth(getActivity()), CommonUtils.getScreenWidth(getActivity()));
        UI.loadCommonColors(true);
        UI.initColorDefault();//默认的相关颜色值
        PermissionUtils.requestRecordAudioPermissionInFragment(this,RECORD_AUDIO_CODE);
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
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
//                        break;
//                    }
//                }
//                //沉浸式Particle_VR版
//                intent.putExtra(IVisualizer.EXTRA_VISUALIZER_CLASS_NAME, OpenGLVisualizerJni.class.getName());
//                intent.putExtra(OpenGLVisualizerJni.EXTRA_VISUALIZER_TYPE, OpenGLVisualizerJni.TYPE_IMMERSIVE_PARTICLE_VR);
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

        if(VisualizerManager.getInstance().getIsShowFragmentMenu()
                && VisualizerManager.getInstance().getCurrentTypeMenus(getActivity()).size()>0){
            visualizerMenu.bringToFront();
            visualizerMenu.setVisibility(View.VISIBLE);
        }else{
            visualizerMenu.setVisibility(View.GONE);
        }


        isFinishChange=true;
    }

    /**
     * 跳转到下一个频谱
     */
    public void goToNextVisualizer(){
        if(VisualizerManager.getInstance().visualizerIndex
                == VisualizerManager.getInstance().visualizerDataType.length-1){
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
        if(VisualizerManager.getInstance().getIsShowFragmentMenu()
                && VisualizerManager.getInstance().getCurrentTypeMenus(getActivity()).size()>0){
            visualizerMenu.bringToFront();
            visualizerMenu.setVisibility(View.VISIBLE);
        }else{
            visualizerMenu.setVisibility(View.GONE);
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

    public void showPopup(View view){

        if(mListPopWindow!=null){
            mListPopWindow.dissmiss();
            mListPopWindow=null;
        }

        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_list_visualizer_menu,null);
        handleListView(contentView);
        mListPopWindow= new CustomPopWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .setOutsideTouchable(true)
                .create()
                .showAsDropDown(view,0,20);

    }

    private void handleListView(View contentView){
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        MyAdapter adapter = new MyAdapter(getActivity(), VisualizerManager.getInstance().getCurrentTypeMenus(getActivity()));
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
            return new MyAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fragment_pop_menu,null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) holder;
            ((MyAdapter.ViewHolder) holder).itemTextView.setText(mData.get(position).getDescription());
            ((MyAdapter.ViewHolder) holder).root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mData.get(position).getMenuCode()== MenuData.CHOOSEIMAGE){
                        if(mListPopWindow!=null){
                            mListPopWindow.dissmiss();
                        }
                        //判断有没有权限
                        if(PermissionUtils.hasWriteAndReadPermission(getActivity())){
                            ImageUtils.getImageBySystemInFragment(VisualizerFragment.this,CHOOSEIMAGE_CODE);
                        }else{
                            PermissionUtils.requestWriteAndReadPermissionInFragment(VisualizerFragment.this,READ_WRITE_PERMISSION_CODE);
                        }

                    }else if(mData.get(position).getMenuCode()== MenuData.CHANGECOLOR){
                        if(mListPopWindow!=null){
                            mListPopWindow.dissmiss();
                        }
                        if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                            VisualizerManager.getInstance().getVisualizerMenu().changeColor();
                        }
                    }else if(mData.get(position).getMenuCode()== MenuData.CLEARIMAGE){
                        //清除图片
                        if(mListPopWindow!=null){
                            mListPopWindow.dissmiss();
                        }
                        if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                            VisualizerManager.getInstance().getVisualizerMenu().changeImagePath(null);
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
