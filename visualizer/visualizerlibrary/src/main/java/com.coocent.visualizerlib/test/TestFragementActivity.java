package com.coocent.visualizerlib.test;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.VisualizerFragment;
import com.coocent.visualizerlib.core.MenuData;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.entity.MenuItem;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.ImageUtils;
import com.coocent.visualizerlib.utils.KeepToUtils;
import com.coocent.visualizerlib.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class TestFragementActivity extends AppCompatActivity implements View.OnClickListener{

    private Button nextBtn;
    private Button previousBtn;
    private Button menuBtn;
    private EditText typeChooseEt;
    private Button goBtn;
    private Button otherBtn;
    private Fragment fragment;
    private ListPopupWindow mListPop;
    private ArrayList<MenuItem> currentMeuns;
    public static final int CHOOSEIMAGE_CODE=10011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_fragment);
        initView();

        setFragment();
    }

    private void initView(){
        nextBtn=findViewById(R.id.atf_next_btn);
        previousBtn=findViewById(R.id.atf_previous_btn);
        typeChooseEt=findViewById(R.id.atf_type_choose_et);
        goBtn=findViewById(R.id.atf_go_btn);
        menuBtn=findViewById(R.id.atf_visualizer_menu);
        otherBtn=findViewById(R.id.atf_visualizer_some_activity);

        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        goBtn.setOnClickListener(this);
        menuBtn.setOnClickListener(this);
        otherBtn.setOnClickListener(this);
    }

    private void setFragment(){
        fragment=new VisualizerFragment();

        //组装传递参数，详情查看VisualizerManager中的频谱类型
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.FRAGMENT_ARGUMENTS_INDEX, 0);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.atf_visualizer_test_fl, fragment)
                .commit();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CHOOSEIMAGE_CODE){
            if (resultCode == Activity.RESULT_OK){
                Uri selectedUri = ((Intent)data).getData();
                LogUtils.d("返回图片URI为："+selectedUri);
                if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                    VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(selectedUri);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
//        if(VisualizerManager.getInstance().getControlVisualizer()==null) return;
        if(v==previousBtn){
            VisualizerManager.getInstance().getControlVisualizer().previousVisualizer();
        }else if(v==nextBtn){
            VisualizerManager.getInstance().getControlVisualizer().nextVisualizer();
        }else if(v==goBtn){
            if(!TextUtils.isEmpty(typeChooseEt.getText().toString().trim())){
                try{
                    int type=Integer.parseInt(typeChooseEt.getText().toString().trim());
                    VisualizerManager.getInstance().getControlVisualizer().someVisualizer(type);
                }catch (Throwable e){
                   LogUtils.d("","异常##"+e.getMessage());
                }
            }
        }else if(v==menuBtn){
            showPopup(v);
        }else if(v==otherBtn){
//            KeepToUtils.keepToVisualizerActivity(TestFragementActivity.this,0);
            startActivity(new Intent(this,TestSegmentTabActivity.class));
        }

    }

    public void showPopup(View view){
        if(mListPop!=null) {
            mListPop.dismiss();
            mListPop=null;
        }
        mListPop = new ListPopupWindow(this);
        mListPop.setAdapter(new MyAdapter(this, VisualizerManager.getInstance().getCurrentTypeMenus(this)));
        mListPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPop.setModal(true);//设置是否是模
        mListPop.setAnchorView(view);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPop.show();

    }

    public class MyAdapter extends BaseAdapter {
        private List<MenuItem> Datas;
        private Context mContext;

        public MyAdapter(Context mContext,List<MenuItem> datas) {
            Datas = datas;
            this.mContext = mContext;
        }

        /**
         * 返回item的个数
         * @return
         */
        @Override
        public int getCount() {
            return Datas.size();
        }

        /**
         * 返回每一个item对象
         * @param i
         * @return
         */
        @Override
        public Object getItem(int i) {
            return Datas.get(i);
        }

        /**
         * 返回每一个item的id
         * @param i
         * @return
         */
        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_fragment_pop_menu, null, false);
                holder.itemTextView = (TextView) convertView.findViewById(R.id.list_pop_tv);
                holder.root=convertView.findViewById(R.id.list_pop_rl);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.itemTextView.setText(Datas.get(position).getDescription());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Datas.get(position).getMenuCode()==MenuData.CHOOSEIMAGE){

                        ImageUtils.getImageBySystemInActivity(TestFragementActivity.this,CHOOSEIMAGE_CODE);

                    }else if(Datas.get(position).getMenuCode()==MenuData.CHANGECOLOR){
                        if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                            VisualizerManager.getInstance().getVisualizerMenu().changeColor();
                        }
                    }else if(Datas.get(position).getMenuCode()==MenuData.CLEARIMAGE){
                        //清除图片
                        if(VisualizerManager.getInstance().getVisualizerMenu()!=null){
                            VisualizerManager.getInstance().getVisualizerMenu().changeImageUri(null);
                        }
                    }
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        TextView itemTextView;
        RelativeLayout root;
    }
}
