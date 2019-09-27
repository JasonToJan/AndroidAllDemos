package jan.jason.opencv;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import jan.jason.opencv.databinding.ActivityOpenGlBinding;

public class OpenGlActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityOpenGlBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this,R.layout.activity_open_gl);
        setContentView(R.layout.activity_open_gl);

        mainBinding.aoDemoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==mainBinding.aoDemoBtn){
            Toast.makeText(this, "click button!", Toast.LENGTH_SHORT).show();
        }
    }
}
