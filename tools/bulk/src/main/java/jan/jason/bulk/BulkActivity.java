package jan.jason.bulk;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import jan.jason.bulk.databinding.ActivityBulkBinding;
import jan.jason.bulk.helper.Item;
import jan.jason.bulk.helper.ItemAdapter;
import jan.jason.bulk.helper.SimpleItemTouchHelperCallback;


/**
 * 批量操作
 * 触发条件：长按歌曲，点击批量操作图标
 * 可以进入改页面，进行一些批量删除，批量添加到列表，添加到队列操作
 *
 * add by wja on 2019.6.21
 */
public class BulkActivity extends AppCompatActivity implements View.OnClickListener,ItemAdapter.OnStartDragListener {

    public static String EXTRA_PRIMARYCOLOR="primaryColor";
    public static String EXTRA_ACCENTCOLOR="accentColor";

    private ItemTouchHelper mItemTouchHelper;
    private ActivityBulkBinding bulkBinding;
    private RecyclerView recyclerView;
    private TextView tvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bulkBinding= DataBindingUtil.setContentView(this,R.layout.activity_bulk);

        initView();
        initData();

        Log.d("测试","##"+"进入BulkActivity");
    }

    private void initView(){
        recyclerView=bulkBinding.abRecyclerview;
        tvNumber=bulkBinding.abTopTv2;

        bulkBinding.abTopTv3.setOnClickListener(this);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        final ItemAdapter itemAdapter = new ItemAdapter(getApplicationContext(),this,tvNumber);
        recyclerView.setAdapter(itemAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(itemAdapter,this);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void initData(){

        ArrayList<Item> list=BulkData.getInstance().getBulkList();
        ItemAdapter.itemList.addAll(list);
        tvNumber.setText(String.valueOf(ItemAdapter.itemList.size()));
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ab_top_tv3){

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
