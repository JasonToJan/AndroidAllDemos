package immortalz.me.transitionhelper.demo.fragment;

import android.support.design.widget.FloatingActionButton;

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

public class FActivity extends BaseActivity {
    @BindView(R2.id.btn_circle)
    FloatingActionButton btnCommit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fragment_th;
    }

    @OnClick(R2.id.btn_circle)
    public void onClick() {
        TransitionsHeleper.startActivity(this, FDetailActivity.class, btnCommit);
    }

}
