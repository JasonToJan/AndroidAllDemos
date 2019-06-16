package immortalz.me.transitionhelper.demo.recyclerview;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.library.bean.InfoBean;
import immortalz.me.library.method.InflateShowMethod;
import immortalz.me.transitionhelper.R;
import immortalz.me.transitionhelper.R2;
import immortalz.me.transitionhelper.base.BaseActivity;

/**
 * Created by Mr_immortalZ on 2016/10/29.
 * email : mr_immortalz@qq.com
 */

public class RvDetailActivity extends BaseActivity {


    @BindView(R2.id.iv_detail)
    ImageView ivDetail;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recyclerview_detail_th;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransitionsHeleper.build(this)
                .setShowMethod(new InflateShowMethod(this, R.layout.activity_rv_inflate_th) {
                    @Override
                    public void loadPlaceholder(InfoBean bean, ImageView placeholder) {
                        Glide.with(RvDetailActivity.this)
                                .load(bean.getLoad())
                                .into(placeholder);
                    }

                    @Override
                    public void loadTargetView(InfoBean bean, View targetView) {
                        Glide.with(RvDetailActivity.this)
                                .load(bean.getLoad())
                                .into((ImageView) targetView);
                    }
                })
                .setExposeColor(getResources().getColor(R.color.bg_teal))
                .intoTargetView(ivDetail)
                .show();
    }
}
