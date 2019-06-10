package jan.jason.androidalldemos.transitions;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityTransitionMainBinding;
import jan.jason.androidalldemos.transitions.activitytransition.TransitionDemoActivity;
import jan.jason.easyanimationsdemo.AnimationListActivity;
import jan.jason.transitioneverywheredemo.MainActivity;

public class TransitionMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityTransitionMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=DataBindingUtil.setContentView(this,R.layout.activity_transition_main);

        mainBinding.atmEverywhereBtn1.setOnClickListener(this);
        mainBinding.atmTransitionBtn2.setOnClickListener(this);
        mainBinding.atmTransitionBtn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.atm_everywhere_btn1:
                startActivity(new Intent(this,MainActivity.class));
                break;

            case R.id.atm_transition_btn2:
                startActivity(new Intent(this,TransitionDemoActivity.class));
                break;

            case R.id.atm_transition_btn3:
                startActivity(new Intent(this,AnimationListActivity.class));
                break;
        }
    }
}
