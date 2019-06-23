package jan.jason.bulk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import jan.jason.bulk.helper.Item;

/**
 * desc: 批量操作工具类
 * *
 * user: JasonJan 1211241203@qq.com
 * time: 2019/6/21 17:05
 **/
public class BulkUtils {

    public static void keepToBulkActivity(Activity activity, ArrayList<Item> list){
        BulkData.getInstance().setBulkList(list);

        activity.startActivity(new Intent(activity,BulkActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    public static void keepToBulkActivity(Activity activity, ArrayList<Item> list,int primaryColor,int accentColor){
        BulkData.getInstance().setBulkList(list);
        BulkData.getInstance().setPrimaryColor(primaryColor);
        BulkData.getInstance().setAccentColor(accentColor);

        activity.startActivity(new Intent(activity,BulkActivity.class));
        activity.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

}
