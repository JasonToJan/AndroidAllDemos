package com.hitherejoe.animate.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hitherejoe.animate.R;
import com.hitherejoe.animate.R2;
import com.hitherejoe.animate.ui.adapter.GridAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WindowTransitionsActivityExplode extends BaseActivity {

    @BindView(R2.id.recycler_view_team)
    RecyclerView mTeamRecycler;

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_transitions_explode);
        ButterKnife.bind(this);
        setupToolbar();
        setupRecyclerView();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupRecyclerView() {
        mTeamRecycler.setHasFixedSize(true);
        String[] items = getResources().getStringArray(R.array.items);
        mTeamRecycler.setAdapter(new GridAdapter(items, onRecyclerItemClick));
    }

    private GridAdapter.OnRecyclerItemClick onRecyclerItemClick = new GridAdapter.OnRecyclerItemClick() {
        @Override
        public void onItemClick(View view) {
            Pair squareParticipant = new Pair<>(view, ViewCompat.getTransitionName(view));
            ActivityOptionsCompat transitionActivityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            WindowTransitionsActivityExplode.this, squareParticipant);
            Intent intent = new Intent(
                    WindowTransitionsActivityExplode.this, SharedTransitionsInActionbarActivity.class);
            startActivity(intent, transitionActivityOptions.toBundle());
        }
    };

}
