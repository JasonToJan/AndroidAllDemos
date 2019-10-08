package jan.jason.opencv;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import jan.jason.opencv.databinding.ActivityOpenGlBinding;
import jan.jason.opencv.lessons.lesson1.LessonOneActivity;

public class OpenGlActivity extends AppCompatActivity implements View.OnClickListener{

    static {
        System.loadLibrary("lesson-lib");
    }

    static native String stringFromJNI();

    private int times;
    private ActivityOpenGlBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this,R.layout.activity_open_gl);

        mainBinding.aoDemoBtn.setOnClickListener(this);
        mainBinding.aoDemoTriangleBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==mainBinding.aoDemoBtn){
            mainBinding.aoDemoBtn.setText(times++%2==1?stringFromJNI():"Click Me");
        }else if(v==mainBinding.aoDemoTriangleBtn){
            startActivity(new Intent(this, LessonOneActivity.class));
        }
    }
}
