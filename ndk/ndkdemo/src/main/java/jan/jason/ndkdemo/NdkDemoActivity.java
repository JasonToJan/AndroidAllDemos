package jan.jason.ndkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class NdkDemoActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();

    private TextView txtNdk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndkdemo_ndk);

        initView();
        initData();
    }

    private void initView(){
        txtNdk=(TextView) findViewById(R.id.amn_demo_txt1);
    }

    private void initData(){
        String text=stringFromJNI();
        txtNdk.setText(text);
    }
}
