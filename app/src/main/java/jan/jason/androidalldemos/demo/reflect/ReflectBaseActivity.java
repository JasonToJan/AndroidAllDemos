package jan.jason.androidalldemos.demo.reflect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidalldemo.jan.jason.reflectlibrary.InjectManager;
import jan.jason.androidalldemos.R;

public class ReflectBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InjectManager.inject(this);//加载布局
    }
}
