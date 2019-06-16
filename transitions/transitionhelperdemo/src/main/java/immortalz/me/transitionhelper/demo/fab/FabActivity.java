package immortalz.me.transitionhelper.demo.fab;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.transitionhelper.R;
import immortalz.me.transitionhelper.R2;
import immortalz.me.transitionhelper.base.BaseActivity;

/**
 * Created by Mr_immortalZ on 2016/10/29.
 * email : mr_immortalz@qq.com
 */

public class FabActivity extends BaseActivity {
    @BindView(R2.id.btn_circle)
    FloatingActionButton btnCircle;
    @BindView(R2.id.btn_no)
    FloatingActionButton btnNo;
    @BindView(R2.id.btn)
    Button btn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fab_th;
    }

    @OnClick({R2.id.btn_no, R2.id.btn_circle,R2.id.btn})
    public void onClick(View view) {
        if(view.getId()==R.id.btn_no){
            TransitionsHeleper.startActivity(FabActivity.this, FabNoCircleActivity.class, btnNo);

        }else if(view.getId()==R.id.btn_circle){
            TransitionsHeleper.startActivity(FabActivity.this, FabCircleActivity.class, btnCircle);

        }else if(view.getId()==R.id.btn){
            TransitionsHeleper.startActivity(FabActivity.this, ButtonActivity.class, btn);
        }
    }

}
