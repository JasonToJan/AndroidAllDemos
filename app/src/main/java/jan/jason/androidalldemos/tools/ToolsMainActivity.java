package jan.jason.androidalldemos.tools;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.goyourfly.multiselectadapter.MainActivityMulti;

import java.util.ArrayList;

import jan.jason.androidalldemos.R;
import jan.jason.androidalldemos.databinding.ActivityMainToolsBinding;
import jan.jason.androidalldemos.demo.reflect.ReflectActivity;
import jan.jason.bulk.BulkUtils;
import jan.jason.bulk.helper.Item;

/**
 * desc: 工具类主页
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/17 10:11
 **/
public class ToolsMainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainToolsBinding mainBinding;
    private ArrayList<Item> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding= DataBindingUtil.setContentView(this, R.layout.activity_main_tools);

        mainBinding.amtBtn1.setOnClickListener(this);
        mainBinding.amtBtn2.setOnClickListener(this);
        mainBinding.amtBtn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.amt_btn1:
                startActivity(new Intent(this, MainActivityMulti.class));
                break;

            case R.id.amt_btn2:
                list.clear();
                for(int i=0;i<10;i++){
                    list.add(new Item("title"+i,"title"+i+i,"title"+i+i+i));
                }
                BulkUtils.keepToBulkActivity(this,list);
                break;

            case R.id.amt_btn3:
                startActivity(new Intent(this, ReflectActivity.class));
                break;
        }
    }

    public class BulkEntity{

        String title1;
        String title2;
        String title3;

        public BulkEntity(String title1, String title2, String title3) {
            this.title1 = title1;
            this.title2 = title2;
            this.title3 = title3;
        }
    }
}
