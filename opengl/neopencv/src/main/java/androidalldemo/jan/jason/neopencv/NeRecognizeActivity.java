package androidalldemo.jan.jason.neopencv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class NeRecognizeActivity extends AppCompatActivity {

    static {
        System.loadLibrary("neopen-lib");
    }

    static native String stringFromJNI();

    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ne_recognize);


    }

    public void touchTxt(View view) {

        ((TextView)findViewById(R.id.anr_txt1)).setText((value++%2==1)?"":stringFromJNI());

    }
}
