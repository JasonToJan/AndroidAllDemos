package jan.jason.androidalldemos.photo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.devio.simple.MainActivityTakePhoto;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityMainPhotoBinding;
import jan.jason.androidalldemos.databinding.ActivityMainToolsBinding;

/**
 * desc: 图片类主页
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/17 10:11
 **/
public class PhotoMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainPhotoBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this, R.layout.activity_main_photo);

        mainBinding.ampBtn1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.amp_btn1:
                startActivity(new Intent(this, MainActivityTakePhoto.class));
                break;
        }
    }
}
