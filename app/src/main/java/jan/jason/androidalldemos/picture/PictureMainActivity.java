package jan.jason.androidalldemos.picture;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.devio.simple.MainActivityTakePhoto;

import androidalldemo.jan.jason.myimageloader.ImageLoaderMainActivity;
import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityMainPhotoBinding;

/**
 * desc: 图片类主页
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/17 10:11
 **/
public class PictureMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainPhotoBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this, R.layout.activity_main_photo);

        mainBinding.ampBtn1.setOnClickListener(this);
        mainBinding.ampBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.amp_btn1:
                startActivity(new Intent(this, MainActivityTakePhoto.class));
                break;

            case R.id.amp_btn2:
                startActivity(new Intent(this, ImageLoaderMainActivity.class));
                break;
        }
    }
}
