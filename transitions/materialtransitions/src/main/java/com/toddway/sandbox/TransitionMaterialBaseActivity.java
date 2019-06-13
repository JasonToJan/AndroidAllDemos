package com.toddway.sandbox;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransitionMaterialBaseActivity extends TransitionHelper.BaseActivity {
    protected static String BASE_FRAGMENT = "base_fragment";
    public @BindView(R2.id.toolbar) Toolbar toolbar;
    public @BindView(R2.id.material_menu_button) MaterialMenuView homeButton;
    public @BindView(R2.id.toolbar_title) TextView toolbarTitle;
    public @BindView(R2.id.fab) Button fab;
    public @BindView(R2.id.drawerLayout) DrawerLayout drawerLayout;
    public @BindView(R2.id.base_fragment_background) View fragmentBackround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        //initToolbar();
        initBaseFragment(savedInstanceState);
    }

    private void initToolbar() {
        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setTitle("");
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    private void initBaseFragment(Bundle savedInstanceState) {
        //apply background bitmap if we have one
        if (getIntent().hasExtra("bitmap_id")) {
            fragmentBackround.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent())));
        }

        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = getFragmentManager().findFragmentByTag(BASE_FRAGMENT);
        }
        if (fragment == null) fragment = getBaseFragment();
        setBaseFragment(fragment);
    }

    protected int getLayoutResource() {
        return R.layout.activity_base;
    };

    protected Fragment getBaseFragment() {
        int fragmentResourceId = getIntent().getIntExtra("fragment_resource_id", R.layout.fragment_thing_list);

        //LogUtils.d("这里fragmentResourceId="+fragmentResourceId+" 1="+R.layout.fragment_thing_detail+" 3="+R.layout.fragment_thing_list);

        if(fragmentResourceId==R.layout.fragment_thing_detail){
            //LogUtils.d("这里fragmentResourceId=1");
            return ThingDetailFragment.create();
        }else if(fragmentResourceId==R.layout.fragment_overaly){
            //LogUtils.d("这里fragmentResourceId=2");
            return new OverlayFragment();
        }else if(fragmentResourceId==R.layout.fragment_thing_list){
            //LogUtils.d("这里fragmentResourceId=3");
            return new ThingListFragment();
        }else{
            //LogUtils.d("这里fragmentResourceId=4");
            return new ThingListFragment();
        }
    }

    public void setBaseFragment(Fragment fragment) {
        if (fragment == null) return;

        //LogUtils.d("这里设置了BaseFragment : "+fragment.getClass().getSimpleName());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.base_fragment, fragment, BASE_FRAGMENT);
        transaction.commit();
    }


    private MaterialMenuDrawable.IconState currentIconState;
    public boolean animateHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return false;
        currentIconState = iconState;
        homeButton.animateState(currentIconState);
        return true;
    }

    public void setHomeIcon(MaterialMenuDrawable.IconState iconState) {
        if (currentIconState == iconState) return;
        currentIconState = iconState;
        homeButton.setState(currentIconState);

    }

    @Override
    public boolean onBeforeBack() {
        ActivityCompat.finishAfterTransition(this);
        return false;
    }

    public static TransitionMaterialBaseActivity of(Activity activity) {
        return (TransitionMaterialBaseActivity) activity;
    }

}
