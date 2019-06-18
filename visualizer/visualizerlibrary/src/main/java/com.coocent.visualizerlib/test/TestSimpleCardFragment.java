package com.coocent.visualizerlib.test;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.test.renderer.VisualizerView;
import com.coocent.visualizerlib.utils.LogUtils;


@SuppressLint("ValidFragment")
public class TestSimpleCardFragment extends LazyFragment {
    private String mTitle;
    private VisualizerView visualizerView;
    private PlayRendererUtils playRendererUtils;
    public volatile boolean isVisible;

    public static TestSimpleCardFragment getInstance(String title) {
        TestSimpleCardFragment sf = new TestSimpleCardFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(, null);
//
//
//
//        return v;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(visualizerView!=null){
            visualizerView.release();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.test_fr_simple_card;
    }

    @Override
    protected void initView(View rootView) {
        TextView card_title_tv = (TextView) rootView.findViewById(R.id.card_title_tv);
        card_title_tv.setText(mTitle);
        visualizerView=rootView.findViewById(R.id.test_fragment_visualizerView);

        if(playRendererUtils==null){
            playRendererUtils=new PlayRendererUtils(getActivity());
        }
        playRendererUtils.addSpectrum(visualizerView,0, Color.BLACK);
        visualizerView.link(0,null);
        visualizerView.setVisualizerEnable(true);


        if(mTitle.equals(TestSegmentTabActivity.mTitles_3[0])){

            playRendererUtils.addSpectrum(visualizerView,0, Color.BLACK);

        }else if(mTitle.equals(TestSegmentTabActivity.mTitles_3[1])){

            playRendererUtils.addSpectrum(visualizerView,1, Color.BLUE);

        }else if(mTitle.equals(TestSegmentTabActivity.mTitles_3[2])){

            playRendererUtils.addSpectrum(visualizerView,2, Color.RED);
        }
    }

    @Override
    public void onFragmentPause() {
        super.onFragmentPause();
        if(visualizerView!=null){
            LogUtils.d("TestSimple不可见");
            //visualizerView.setListenNull();
            visualizerView.setVisualizerEnable(false);
        }
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();
        if(visualizerView!=null){
            LogUtils.d("TestSimple可见");
            //visualizerView.setListenResume();
            visualizerView.setVisualizerEnable(true);
        }
    }
}