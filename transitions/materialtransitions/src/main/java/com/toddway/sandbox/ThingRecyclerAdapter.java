package com.toddway.sandbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;


public class ThingRecyclerAdapter extends BaseRecyclerAdapter<Thing> {

    @Override
    public ThingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transition_m_list_item, parent, false);
        return new ThingHolder(view);
    }

    public class ThingHolder extends BaseRecyclerAdapter<Thing>.ViewHolder {

        @BindView(R2.id.tmli_title)
        TextView titleTextView;

        public ThingHolder(View itemView) {
            super(itemView);
        }

        public void populate(Thing item) {
            titleTextView.setText(item.text);
        }
    }
}
