package jan.jason.androidalldemos.transitions;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hitherejoe.animate.ui.activity.AnimateMainActivity;
import com.lgvalle.material_animations.MaterialMainActivity;
import com.mjun.demo.EnterActivity;
import com.tandong.switchlayoutdemo.SwitchMainActivity;
import com.toddway.sandbox.TransitionMaterialBaseActivity;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityTransitionMainBinding;
import jan.jason.androidalldemos.transitions.activitytransition.TransitionDemoActivity;
import jan.jason.easyanimationsdemo.AnimationListActivity;
import jan.jason.transitioneverywheredemo.EveryWhereMainActivity;
import us.pinguo.shareelementdemo.YcShareMainActivity;

public class TransitionMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityTransitionMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=DataBindingUtil.setContentView(this,R.layout.activity_transition_main);

        mainBinding.atmEverywhereBtn1.setOnClickListener(this);
        mainBinding.atmTransitionBtn2.setOnClickListener(this);
        mainBinding.atmTransitionBtn3.setOnClickListener(this);
        mainBinding.atmTransitionBtn4.setOnClickListener(this);
        mainBinding.atmTransitionBtn5.setOnClickListener(this);
        mainBinding.atmTransitionBtn6.setOnClickListener(this);
        mainBinding.atmTransitionBtn7.setOnClickListener(this);
        mainBinding.atmTransitionBtn8.setOnClickListener(this);
        mainBinding.atmTransitionBtn9.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.atm_everywhere_btn1:
                startActivity(new Intent(this, EveryWhereMainActivity.class));
                break;

            case R.id.atm_transition_btn2:
                startActivity(new Intent(this,TransitionDemoActivity.class));
                break;

            case R.id.atm_transition_btn3:
                startActivity(new Intent(this,AnimationListActivity.class));
                break;

            case R.id.atm_transition_btn4:
                startActivity(new Intent(this,EnterActivity.class));
                break;

            case R.id.atm_transition_btn5:
                startActivity(new Intent(this,MaterialMainActivity.class));
                break;

            case R.id.atm_transition_btn6:
                startActivity(new Intent(this,TransitionMaterialBaseActivity.class));
                break;

            case R.id.atm_transition_btn7:
                startActivity(new Intent(this,AnimateMainActivity.class));
                break;

            case R.id.atm_transition_btn8:
                startActivity(new Intent(this, YcShareMainActivity.class));
                break;

            case R.id.atm_transition_btn9:
                startActivity(new Intent(this, SwitchMainActivity.class));
                break;
        }
    }
}
