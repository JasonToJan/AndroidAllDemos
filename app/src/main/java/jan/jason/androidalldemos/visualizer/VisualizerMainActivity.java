package jan.jason.androidalldemos.visualizer;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.coocent.visualizerlib.test.TestVisualizerActivity;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityVisualizerMainBinding;

public class VisualizerMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityVisualizerMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding=DataBindingUtil.setContentView(this,R.layout.activity_visualizer_main);
        mainBinding.avmVisualizerBtn1.setOnClickListener(this);
        mainBinding.avmVisualizerBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avm_visualizer_btn1:
                startActivity(new Intent(this,TestVisualizerActivity.class));
               // KeepToUtils.keepToVisualizerActivity(this,0);
                break;

            case R.id.avm_visualizer_btn2:
                startActivity(new Intent(this,VisualizerDemoActivity.class));
                break;
        }
    }
}
