package jan.jason.androidalldemos.transitions.activitytransition;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import jan.jason.androidalldemos.R;

public class TransitionDemoActivity extends AppCompatActivity {


    private TransitionDemoActivity selfActivity = TransitionDemoActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置允许使用转场动画 此属性同样可以在style设置
        // <item name="android:windowContentTransitions">true</item>
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_demo);
    }

    public void getOverridePendingTransition(View view) {
        startActivity(new Intent(selfActivity, TransitionOverridePendingActivity.class));
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public void getStyleActivity(View view) {
        startActivity(new Intent(selfActivity, TransitionStyleActivity.class));
    }

    public void getActivityOptions(View view) {
        Button btn = findViewById(R.id.btn);
        ImageView cat=findViewById(R.id.atd_cat);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        this, // 当前Activity
                        cat, // 共享元素View
                        "activityOption"); // 共享元素名称
        Intent intent = new Intent(selfActivity, TransitionOptionsActivity.class);
        startActivity(intent, optionsCompat.toBundle());
    }

    public void getActivityOptionsMore(View view) {
        Button btn = findViewById(R.id.btn1);
        ImageView iv = findViewById(R.id.atd_cat);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation(
                        this,
                        Pair.create((View) btn, "btn1"),
                        new Pair<View, String>(iv, "img1")
                );
        Intent intent = new Intent(selfActivity, TransitionOptionsMoreActivity.class);
        startActivity(intent, optionsCompat.toBundle());
    }

    /**
     * 转场自带的滑动动画
     * @param view
     */
    public void getActivityOptionSlide(View view) {
        Slide slide = new Slide();
        slide.setDuration(800);
        getWindow().setExitTransition(slide);
        getWindow().setEnterTransition(slide);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(selfActivity);
        Intent intent = new Intent(selfActivity, TransitionOptionsActivity.class);
        startActivity(intent, optionsCompat.toBundle());
    }

    public void getActivityOptionsExplode(View view) {
        Explode explode = new Explode();
        explode.setDuration(800);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(selfActivity);
        Intent intent = new Intent(selfActivity, TransitionOptionsActivity.class);
        startActivity(intent, optionsCompat.toBundle());
    }

    public void getActivityOptionsFade(View view) {
        Fade fade = new Fade();
        fade.setDuration(800);
        getWindow().setExitTransition(fade);
        getWindow().setEnterTransition(fade);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(selfActivity);
        Intent intent = new Intent(selfActivity, TransitionOptionsActivity.class);
        startActivity(intent, optionsCompat.toBundle());
    }
}
