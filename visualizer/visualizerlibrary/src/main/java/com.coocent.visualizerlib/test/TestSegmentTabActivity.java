package com.coocent.visualizerlib.test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.VisualizerFragment;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.widget.MsgView;


import java.util.ArrayList;

public class TestSegmentTabActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Fragment> mFragments = new ArrayList<>();

    public static String[] mTitles_3 = {"首页", "消息", "联系人", "更多"};
    private View mDecorView;
    private SegmentTabLayout mTabLayout_3;
    private Button clickToOther;
    private Button clickPause;
    private Button clickResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segment_tab);

        mFragments.add(TestSimpleCardFragment.getInstance("SwitchViewPagerr1"));
//        for (int i=0;i<2;i++) {
//            mFragments.add(TestSimpleCardFragment.getInstance("Switch ViewPager " + mTitles_3[i]));
//        }
//        mFragments.add(new TestFragment());
//        mFragments.add(new VisualizerFragment());
        mFragments.add(new TestFragment());
        mFragments.add(new TestFragment());
        mFragments.add(new VisualizerFragment());

        mDecorView = getWindow().getDecorView();

        mTabLayout_3 = ViewFindUtils.find(mDecorView, R.id.tl_3);
        clickToOther=ViewFindUtils.find(mDecorView,R.id.test_fl_click_btn);
        clickPause=ViewFindUtils.find(mDecorView,R.id.test_fl_click_pause_btn);
        clickResume=ViewFindUtils.find(mDecorView,R.id.test_fl_click_resume_btn);

        clickToOther.setOnClickListener(this);
        clickPause.setOnClickListener(this);
        clickResume.setOnClickListener(this);

        tl_3();

        mTabLayout_3.showDot(1);

        //设置未读消息红点
        mTabLayout_3.showDot(2);
        MsgView rtv_3_2 = mTabLayout_3.getMsgView(2);
        if (rtv_3_2 != null) {
            rtv_3_2.setBackgroundColor(Color.parseColor("#6D8FB0"));
        }
    }

    @Override
    public void onClick(View v) {
        if(v==clickToOther){
            startActivity(new Intent(this,TestEmptyActivity.class));
        }else if(v==clickPause){
            //VisualizerManager.getInstance().getVisualizerService().playingChanged(false);
            //VisualizerManager.getInstance().getVisualizerMenu().onPauseDraw();
        }else if(v==clickResume){
            //VisualizerManager.getInstance().getVisualizerService().playingChanged(true);
            //VisualizerManager.getInstance().getVisualizerMenu().onResumeDraw();
        }

    }

    private void tl_3() {
        final ViewPager vp_3 = ViewFindUtils.find(mDecorView, R.id.vp_2);
        vp_3.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        mTabLayout_3.setTabData(mTitles_3);
        mTabLayout_3.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                vp_3.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });

        vp_3.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout_3.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        vp_3.setCurrentItem(1);
    }



    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles_3[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
