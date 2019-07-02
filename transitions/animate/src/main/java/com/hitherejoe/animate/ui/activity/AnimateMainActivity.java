package com.hitherejoe.animate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hitherejoe.animate.R;
import com.hitherejoe.animate.R2;
import com.hitherejoe.animate.ui.adapter.AnimationApisAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AnimateMainActivity extends BaseActivity {

    @BindView(R2.id.recycler_animation_apis)
    RecyclerView mAnimationApisRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animate_main);
        ButterKnife.bind(this);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        mAnimationApisRecycler.setLayoutManager(new LinearLayoutManager(this));
        String[] apiArray = getResources().getStringArray(R.array.animation_apis);
        AnimationApisAdapter animationApisAdapter = new AnimationApisAdapter(apiArray, onRecyclerItemClick);
        mAnimationApisRecycler.setAdapter(animationApisAdapter);
    }

    private AnimationApisAdapter.OnRecyclerItemClick onRecyclerItemClick =
            new AnimationApisAdapter.OnRecyclerItemClick() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = null;
                    switch (position) {
                        case 0:
                            intent = new Intent(AnimateMainActivity.this, ViewPropertyAnimatorActivity.class);
                            break;
                        case 1:
                            intent = new Intent(AnimateMainActivity.this, ObjectAnimatorActivity.class);
                            break;
                        case 2:
                            intent = new Intent(AnimateMainActivity.this, InterpolatorActivity.class);
                            break;
                        case 3:
                            intent = new Intent(AnimateMainActivity.this, CircularRevealActivity.class);
                            break;
                        case 4:
                            intent = new Intent(AnimateMainActivity.this, MorphTransitionsActivity.class);
                            break;
                        case 5:
                            intent = new Intent(AnimateMainActivity.this, SharedTransitionsActivity.class);
                            break;
                        case 6:
                            intent = new Intent(AnimateMainActivity.this, WindowTransitionsActivity.class);
                            break;
                        case 7:
                            intent = new Intent(
                                    AnimateMainActivity.this, WindowTransitionsActivityExplode.class);
                            break;
                        case 8:
                            intent = new Intent(
                                    AnimateMainActivity.this, AnimatedVectorDrawablesActivity.class);
                            break;
                    }
                    if (intent != null) startActivity(intent);
                }
    };
}
