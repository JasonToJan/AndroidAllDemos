package com.coocent.visualizerlib.test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.VisualizerFragment;
import com.coocent.visualizerlib.core.VisualizerManager;
import com.coocent.visualizerlib.utils.Constants;
import com.coocent.visualizerlib.utils.LogUtils;

public class TestFragementActivity extends AppCompatActivity implements View.OnClickListener{

    private Button nextBtn;
    private Button previousBtn;
    private EditText typeChooseEt;
    private Button goBtn;
    private Fragment fragment;

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

        nextBtn.setOnClickListener(this);
        previousBtn.setOnClickListener(this);
        goBtn.setOnClickListener(this);
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
    public void onClick(View v) {
        if(VisualizerManager.getInstance().getControlVisualizer()==null) return;
        if(v==previousBtn){
            VisualizerManager.getInstance().getControlVisualizer().nextVisualizer();
        }else if(v==nextBtn){
            VisualizerManager.getInstance().getControlVisualizer().previousVisualizer();
        }else if(v==goBtn){
            if(!TextUtils.isEmpty(typeChooseEt.getText().toString().trim())){
                try{
                    int type=Integer.parseInt(typeChooseEt.getText().toString().trim());
                    VisualizerManager.getInstance().getControlVisualizer().someVisualizer(type);
                }catch (Throwable e){
                   LogUtils.d("","异常##"+e.getMessage());
                }
            }
        }
    }
}
