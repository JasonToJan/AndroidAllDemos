package immortalz.me.transitionhelper.demo.image;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.transitionhelper.R;
import immortalz.me.transitionhelper.R2;
import immortalz.me.transitionhelper.base.BaseActivity;

/**
 * Created by Mr_immortalZ on 2016/10/27.
 * email : mr_immortalz@qq.com
 */

public class ImageActivity extends BaseActivity {


    @BindView(R2.id.activity_image_iv1)
    ImageView iv1;

    String imgUrl = "http://oss.suibianlu.com/zb_users/upload/2017/10/201710271161_64.jpg";
    String imgUrl2= "http://oss.suibianlu.com/zb_users/upload/2017/10/201710255100_555.jpg";
    String imgUrl3= "http://oss.suibianlu.com/zb_users/upload/2017/09/201709146534_548.jpg";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Glide.with(this)
                .load(imgUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transform(new FitCenter())
                        .skipMemoryCache(true))
                .into(iv1);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_th;
    }

    @OnClick(R2.id.activity_image_iv1)
    public void onClick() {
        TransitionsHeleper.startActivity(this, ImageDetailActivity.class, iv1, imgUrl);
    }
}
