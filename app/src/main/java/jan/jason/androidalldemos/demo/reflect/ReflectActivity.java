package jan.jason.androidalldemos.demo.reflect;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidalldemo.jan.jason.reflectlibrary.ContentView;
import androidalldemo.jan.jason.reflectlibrary.InjectView;
import androidalldemo.jan.jason.reflectlibrary.OnClick;
import jan.jason.androidalldemos.R;

@ContentView(R.layout.activity_reflect)
public class ReflectActivity extends ReflectBaseActivity {

    @InjectView(R.id.re_tv1)
    TextView textView;

    @InjectView(R.id.re_tv2)
    TextView textView2;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.re_tv1,R.id.re_tv2})
    public void click222222222(View view){
        if(view.getId()==R.id.re_tv1){
            Toast.makeText(this, "click tv1", Toast.LENGTH_SHORT).show();
        }else if(view.getId()==R.id.re_tv2){
            Toast.makeText(this, "click tv2", Toast.LENGTH_SHORT).show();
        }
    }
}
