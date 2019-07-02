package jan.jason.androidalldemos.player;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.devbrackets.android.exomediademo.ui.activity.StartupActivity;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityPlayerMainBinding;


public class PlayerMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityPlayerMainBinding mainPlayerBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPlayerBinding=DataBindingUtil.setContentView(this,R.layout.activity_player_main);

        mainPlayerBinding.pmBtn1.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.pm_btn1:
                startActivity(new Intent(PlayerMainActivity.this,StartupActivity.class));
                break;

        }
    }
}
