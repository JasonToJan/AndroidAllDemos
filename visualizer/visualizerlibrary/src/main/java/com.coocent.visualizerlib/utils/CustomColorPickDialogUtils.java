package com.coocent.visualizerlib.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.ui.CircleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * *
 * Creator: Wang
 * Date: 2019/6/7 9:11
 */
public class CustomColorPickDialogUtils {

    /**
     * 对话框宽度
     */
    public static int dialogWidth;

    /**
     * 对话框高度
     */
    public static int dialogHeight;

    /**
     * 宽度偏移距离
     */
    public static int paddingWidth;

    /**
     * 圆圈数量
     */
    public static int circleNums;

    /**
     * 一行多少个
     */
    public static int spanCount=3;

    /**
     * 选择项
     */
    public static int colorPosition;

    public static void showColorPickDialog(final Context context,int oldColor,final DialogOKOrCancel dialogOKOrCancel){

        View view = View.inflate(context, R.layout.dialog_visualizer_pick, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();

        RecyclerView mRecyclerView=view.findViewById(R.id.dvp_color_list);
        Button btnVisualizerCancel = view.findViewById(R.id.btn_visualizer_cancel);
        Button btnVisualizerOk = view.findViewById(R.id.btn_visualizer_ok);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final ArrayList<ColorCirlceEntity> mListData=new ArrayList<>();
        for(int i=0;i<Constants.colors.length;i++){
            ColorCirlceEntity entity=new ColorCirlceEntity(Constants.colors[i]);
            if(oldColor==Constants.colors[i]){
               entity.setChecked(true);
            }
            mListData.add(entity);
        }
        circleNums=mListData.size();

        final RecycleViewMyAdapter adapter=new RecycleViewMyAdapter(mListData,context);

        /**
         * 实现RecycleView的item的点击效果
         */
        adapter.setOnItemClickListener(new RecycleViewMyAdapter.onItemClickListener() {

            //单点击
            @Override
            public void onItemClick(CircleView view, int position) {
                colorPosition=position;
                if(mListData.size()>0){
                    for(int i=0;i<mListData.size();i++){
                        if(i!=position){
                            mListData.get(i).setChecked(false);
                        }else{
                            mListData.get(i).setChecked(true);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            //长按点击
            @Override
            public void onItemLonfClick(CircleView view, final int position) {

                colorPosition=position;
                dialog.dismiss();
                dialogOKOrCancel.onClickOK(position);

            }
        });
        mRecyclerView.setAdapter(adapter);


        dialog.setIcon(R.drawable.ic_dialog_color_pick);
        dialog.setTitle(R.string.change_color);
        dialog.setView(view);
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE,context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                dialogOKOrCancel.onClickOK(colorPosition);
//            }
//        });
//        dialog.setButton(DialogInterface.BUTTON_NEUTRAL,context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        btnVisualizerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnVisualizerOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogOKOrCancel.onClickOK(colorPosition);
            }
        });

        dialog.show();

        Window dialogWindow = dialog.getWindow();//获取window对象
        dialogWindow.setGravity(Gravity.CENTER);//设置对话框位置


        if(CommonUtils.isScreenOriatationPortrait(context)){
            dialogWidth=CommonUtils.getScreenWidth(context)*8/10;
            dialogHeight=CommonUtils.getScreenWidth(context)*8/10;
        }else{
            dialogWidth=CommonUtils.getScreenHight(context)*8/10;
            dialogHeight=CommonUtils.getScreenHight(context)*8/10;
        }

        paddingWidth=CommonUtils.dip2px(context,40);
        dialogWindow.setLayout(dialogWidth,dialogHeight);//设置横向全屏
        dialogWindow.setWindowAnimations(R.style.ZoomFadeAnimation);

    }

    public interface DialogOKOrCancel {
        void onClickOK(int colorPosition);
    }

    public static class ColorCirlceEntity{
        int color;
        boolean isChecked;

        public ColorCirlceEntity(int color) {
            this.color = color;
        }

        public ColorCirlceEntity(int color, boolean isChecked) {
            this.color = color;
            this.isChecked = isChecked;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public static class RecycleViewMyAdapter extends RecyclerView.Adapter<RecycleViewMyAdapter.ViewHolder> {
        private List<ColorCirlceEntity>  mListDatas;
        private Context mContext;
        private static final String TAG = "RecycleViewMyAdapter";
        /**
         * 通过接口来实现点击、长按等事件
         */
        private onItemClickListener mOnItemClickListener;
        private List<Integer> mHeights;

        public interface onItemClickListener {
            void onItemClick(CircleView view,int position);
            void onItemLonfClick(CircleView view,int position);
        }

        public void setOnItemClickListener(onItemClickListener monItemClickListener){
            this.mOnItemClickListener=monItemClickListener;
        }

        public RecycleViewMyAdapter(List<ColorCirlceEntity> mListData, Context mainActivity) {

            this.mContext=mainActivity;
            this.mListDatas=mListData;

        }

        public void removeData(int position){
            mListDatas.remove(position);
            notifyItemRemoved(position);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.item_dialog_color_pick, parent,false);

            view.getLayoutParams().width=(dialogWidth-paddingWidth)/spanCount;
            view.getLayoutParams().height=(dialogWidth-paddingWidth)/spanCount;

            ViewHolder holder=new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.circleView.setColor(mListDatas.get(position).getColor());
            holder.circleView.setChecked(mListDatas.get(position).isChecked());
            holder.circleView.setRadiusDP(24);//dp

            if (mOnItemClickListener!=null){
                holder.circleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos=holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.circleView,pos);
                    }
                });


                holder.circleView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos=holder.getLayoutPosition();
                        mOnItemClickListener.onItemLonfClick(holder.circleView,pos);
                        return false;
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mListDatas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            private CircleView circleView;

            public ViewHolder(View itemView) {
                super(itemView);
                circleView= (CircleView) itemView.findViewById(R.id.idcp_circleView);
            }
        }
    }

}
