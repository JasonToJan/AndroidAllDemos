package androidalldemo.jan.jason.myimageloader;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import androidalldemo.jan.jason.myimageloader.permission.PermissionDenied;
import androidalldemo.jan.jason.myimageloader.permission.PermissionHelper;
import androidalldemo.jan.jason.myimageloader.permission.PermissionSucceed;
import androidalldemo.jan.jason.myimageloader.selectimage.ChoosePictureActivity;


public class ImageLoaderMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageloader_main);
    }

    public void jump(View view) {
        PermissionHelper.with(this).requestCode(100).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}).request();

    }

    @PermissionSucceed(requestCode = 100)
    public void onSuccess(){
        startActivity(new Intent(getApplicationContext(), ChoosePictureActivity.class));
    }
    @PermissionDenied(requestCode = 100)
    public void onFailed(){

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.requestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    public void net(View view) {
        PermissionHelper.with(this).requestCode(200).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}).request();

    }
    @PermissionSucceed(requestCode = 200)
    public void onSuccess2(){
        startActivity(new Intent(getApplicationContext(), ChoosePictureActivity.class).putExtra("isNet",true));
    }
    @PermissionDenied(requestCode = 200)
    public void onFailed2(){

    }
}
