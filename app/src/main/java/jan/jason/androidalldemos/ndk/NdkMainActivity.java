package jan.jason.androidalldemos.ndk;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityMainNdkBinding;

/**
 * Description: NDK主页
 * *
 * Creator: Wang
 * Date: 2019/9/23 22:46
 */
public class NdkMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainNdkBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this, R.layout.activity_main_ndk);

        mainBinding.amnBtn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.amn_btn1:
                startActivity(new Intent(this, NdkMainActivity.class));
                break;

        }
    }

}
