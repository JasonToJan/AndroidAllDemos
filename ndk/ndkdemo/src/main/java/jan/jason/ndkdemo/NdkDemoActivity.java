package jan.jason.ndkdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class NdkDemoActivity extends AppCompatActivity implements View.OnClickListener{

    static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();

    public native void accessField();

    public String showText="Hello World!";
    private TextView txtNdk;
    private TextView resultTxt;
    private Button btnChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndkdemo_ndk);

        initView();
        initData();
    }

    @Override
    public void onClick(View v) {
        accessField();
    }

    private void initView(){
        txtNdk=(TextView) findViewById(R.id.amn_demo_txt1);
        btnChange=(Button) findViewById(R.id.amn_demo_btn1);
        resultTxt=(TextView) findViewById(R.id.amn_demo_txt2);
    }

    private void initData(){
        String text=stringFromJNI();
        txtNdk.setText(text);

        btnChange.setOnClickListener(this);
    }

}
