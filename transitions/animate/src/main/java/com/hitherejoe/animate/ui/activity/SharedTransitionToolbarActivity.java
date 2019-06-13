package com.hitherejoe.animate.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;

import com.hitherejoe.animate.R;
import com.hitherejoe.animate.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SharedTransitionToolbarActivity extends BaseActivity {

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Slide slide = new Slide(Gravity.BOTTOM);
        slide.addTarget(R.id.text_detail);
        slide.addTarget(R.id.text_close);
        slide.addTarget(R.id.view_separator);
        getWindow().setEnterTransition(slide);
        setContentView(R.layout.activity_shared_transition_in);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R2.id.text_close)
    public void onCloseTextClicked() {
        finishAfterTransition();
    }

}
