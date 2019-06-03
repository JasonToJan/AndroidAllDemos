package jan.jason.androidalldemos;

import android.app.Application;



/**
 * Description: App全局入口
 * *
 * Creator: Wang
 * Date: 2019/5/26 10:22
 */
public class MyApplication extends Application {

    private static MyApplication myApplication;


    public static MyApplication getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication=this;

        initDebug();

    }



    /**
     * 调试相关
     */
    private void initDebug(){
        if(BuildConfig.DEBUG){

        }
    }

}
