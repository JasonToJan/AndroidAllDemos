package com.toddway.sandbox;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balysv.materialmenu.MaterialMenuDrawable;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThingListFragment extends TransitionHelper.BaseFragment {
    @BindView(R2.id.recycler)
    RecyclerView recyclerView;
    ThingRecyclerAdapter recyclerAdapter;

    public ThingListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_thing_list, container, false);
        ButterKnife.bind(this, rootView);
        initRecyclerView();
        return rootView;
    }

    private void initRecyclerView() {
        recyclerAdapter = new ThingRecyclerAdapter();
        recyclerAdapter.updateList(getThings());
        recyclerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Thing>() {
            @Override
            public void onItemClick(View view, Thing item, boolean isLongClick) {
                if (isLongClick) {
                    TransitionMaterialBaseActivity.of(getActivity()).animateHomeIcon(MaterialMenuDrawable.IconState.X);
                } else {
                    Navigator.launchDetail(TransitionMaterialBaseActivity.of(getActivity()), view, item, recyclerView);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerAdapter);

        TransitionMaterialBaseActivity.of(getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigator.launchOverlay(TransitionMaterialBaseActivity.of(getActivity()), v, getActivity().findViewById(R.id.base_fragment_container));
            }
        });
    }

    @Override
    public boolean onBeforeBack() {
        TransitionMaterialBaseActivity activity = TransitionMaterialBaseActivity.of(getActivity());
        if (!activity.animateHomeIcon(MaterialMenuDrawable.IconState.BURGER)) {
            activity.drawerLayout.openDrawer(Gravity.START);
        }
        return super.onBeforeBack();
    }

    public static List<Thing> getThings() {
        ArrayList<Thing> list = new ArrayList<>();
        for (int l = 0; l < 100; l++) {
            list.add(new Thing("Thing "+l, null));
        }
        return list;
    }

}

