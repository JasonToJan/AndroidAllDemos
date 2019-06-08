package com.coocent.visualizerlib.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.coocent.visualizerlib.R;
import com.coocent.visualizerlib.utils.KeepToUtils;


public class TestVisualizerActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_visualizer_demo_);

        ((Button)findViewById(R.id.avd_visualizer_btn1)).setOnClickListener(this);
        ((Button)findViewById(R.id.avd_visualizer_btn2)).setOnClickListener(this);
        ((Button)findViewById(R.id.avd_visualizer_btn3)).setOnClickListener(this);
        ((Button)findViewById(R.id.avd_visualizer_btn4)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v.getId()==R.id.avd_visualizer_btn1){

        }else if(v.getId()==R.id.avd_visualizer_btn2){
            KeepToUtils.keepToVisualizerActivity(TestVisualizerActivity.this,0);
        }else if(v.getId()==R.id.avd_visualizer_btn3){
            KeepToUtils.keepToSimpleVisualizerActivity(TestVisualizerActivity.this,0);
        }else if(v.getId()==R.id.avd_visualizer_btn4){
            KeepToUtils.keepToVisualizerFragment(TestVisualizerActivity.this,0);
        }
    }
}
