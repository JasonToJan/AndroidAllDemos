package jan.jason.androidalldemos.transitions.activitytransition;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import jan.jason.androidalldemos.R;

public class TransitionOverridePendingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_override_pending);
    }

    public void getFinish(View view) {
        finish();
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

}
