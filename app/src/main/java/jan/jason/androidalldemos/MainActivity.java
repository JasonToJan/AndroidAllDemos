package jan.jason.androidalldemos;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidalldemo.popupwindowdemo.PopupMainActivity;
import jan.jason.androidalldemos.databinding.ActivityMainAllBinding;
import jan.jason.androidalldemos.tools.ToolsMainActivity;
import jan.jason.androidalldemos.transitions.TransitionMainActivity;
import jan.jason.androidalldemos.visualizer.VisualizerMainActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainAllBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=DataBindingUtil.setContentView(this,R.layout.activity_main_all);

        mainBinding.amBtn1.setOnClickListener(this);
        mainBinding.amBtn2.setOnClickListener(this);
        mainBinding.amBtn3.setOnClickListener(this);
        mainBinding.amBtn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.am_btn1:
                startActivity(new Intent(this,TransitionMainActivity.class));
                break;

            case R.id.am_btn2:
                startActivity(new Intent(this,VisualizerMainActivity.class));
                break;

            case R.id.am_btn3:
                startActivity(new Intent(this,PopupMainActivity.class));
                break;

            case R.id.am_btn4:
                startActivity(new Intent(this, ToolsMainActivity.class));
                break;
        }
    }
}
