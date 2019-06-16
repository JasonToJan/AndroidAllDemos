package immortalz.me.transitionhelper;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import immortalz.me.transitionhelper.base.BaseActivity;
import immortalz.me.transitionhelper.demo.fab.FabActivity;
import immortalz.me.transitionhelper.demo.fragment.FActivity;
import immortalz.me.transitionhelper.demo.image.ImageActivity;
import immortalz.me.transitionhelper.demo.intent.ForResultActivity;
import immortalz.me.transitionhelper.demo.intent.IntentActivity;
import immortalz.me.transitionhelper.demo.recyclerview.RvActivity;

public class MainActivityTransitionHelper extends BaseActivity {

    @BindView(R2.id.btn_image)
    Button btnImage;
    @BindView(R2.id.btn_recycleview)
    Button btnRecycleview;
    @BindView(R2.id.btn_fab)
    Button btnFab;
    @BindView(R2.id.btn_fragment)
    Button btnFragment;
    @BindView(R2.id.btn_intent)
    Button btnIntent;
    @BindView(R2.id.btn_for_result)
    Button btnForResult;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main_transitionhelper;
    }


    @OnClick({R2.id.btn_image, R2.id.btn_recycleview, R2.id.btn_fab, R2.id.btn_fragment, R2.id.btn_intent,R2.id.btn_for_result})
    public void onClick(View view) {

        if(view.getId()==R.id.btn_image){
            gotoNextActivity(ImageActivity.class);

        }else if(view.getId()==R.id.btn_recycleview){
            gotoNextActivity(RvActivity.class);

        }else if(view.getId()==R.id.btn_fab){
            gotoNextActivity(FabActivity.class);

        }else if(view.getId()==R.id.btn_fragment){
            gotoNextActivity(FActivity.class);

        }else if(view.getId()==R.id.btn_intent){
            gotoNextActivity(IntentActivity.class);

        }else if(view.getId()==R.id.btn_for_result){
            gotoNextActivity(ForResultActivity.class);

        }
    }

    private void gotoNextActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);

    }

}
