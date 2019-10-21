package jan.jason.androidalldemos;

import android.app.Application;
import android.support.annotation.Nullable;

import com.devbrackets.android.exomediademo.manager.PlaylistManager;

import androidalldemo.jan.jason.myimageloader.ApplicationUtils;


/**
 * Description: App全局入口
 * *
 *
 *
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

        initData();

    }

    /**
     * 初始化数据相关
     */
    private void initData(){
        ApplicationUtils.doOnCreate(this);
    }



    /**
     * 调试相关
     */
    private void initDebug(){
        if(BuildConfig.DEBUG){

            //CrashReport.initCrashReport(getApplicationContext(), "ec8aa931b4", false);
        }
    }

//    /**
//     * 配置媒体资源缓存
//     */
//    private void configureExoMedia() {
//        // Registers the media sources to use the OkHttp client instead of the standard Apache one
//        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
//        ExoMedia.setDataSourceFactoryProvider(new ExoMedia.DataSourceFactoryProvider() {
//
//            /**
//             * 配置资源缓存，缓存路径，缓存大小
//             */
//            @Nullable
//            private CacheDataSourceFactory instance;
//
//            @NonNull
//            @Override
//            public DataSource.Factory provide(@NonNull String userAgent, @Nullable TransferListener listener) {
//                if (instance == null) {
//                    // Updates the network data source to use the OKHttp implementation
//                    DataSource.Factory upstreamFactory = new OkHttpDataSourceFactory(new OkHttpClient(), userAgent, listener);
//
//                    // Adds a cache around the upstreamFactory
//                    Cache cache = new SimpleCache(new File(getCacheDir(), "ExoMediaCache"), new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));
//                    instance = new CacheDataSourceFactory(cache, upstreamFactory, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
//                }
//
//                return instance;
//            }
//        });
//    }

}
