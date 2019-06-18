package jan.jason.androidalldemos.tools;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityMainToolsBinding;

/**
 * desc: 工具类主页
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/17 10:11
 **/
public class ToolsMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainToolsBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this, R.layout.activity_main_tools);

        mainBinding.amtBtn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.amt_btn1:

                break;
        }
    }
}
