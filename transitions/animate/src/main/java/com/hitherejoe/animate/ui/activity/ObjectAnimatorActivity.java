package com.hitherejoe.animate.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.AdapterViewAnimator;

import com.hitherejoe.animate.R;
import com.hitherejoe.animate.R2;
import com.hitherejoe.animate.ui.adapter.FrameAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ObjectAnimatorActivity extends BaseActivity {

    @BindView(R2.id.flipper_content)
    AdapterViewAnimator mContentFlipper;//多层布局

    private boolean isAnimatingUp;
    private int mContentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_animator);
        ButterKnife.bind(this);

        isAnimatingUp = true;
        mContentCount = 20;
        mContentFlipper.setAdapter(new FrameAdapter(this, mContentCount));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void showNext() {
        if (mContentCount > 1) {
            setAnimations();
            mContentFlipper.showNext();
            isAnimatingUp = !isAnimatingUp;
            mContentCount--;
        } else {
            finish();
        }
    }

    private void setAnimations() {
        mContentFlipper.setInAnimation(this, isAnimatingUp ? R.animator.slide_in_bottom : R.animator.slide_in_left);
        mContentFlipper.setOutAnimation(this, isAnimatingUp ? R.animator.slide_out_top : R.animator.slide_out_right);
    }

}
