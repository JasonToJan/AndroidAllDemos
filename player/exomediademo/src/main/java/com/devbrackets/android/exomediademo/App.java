//package com.devbrackets.android.exomediademo;
//
//import android.app.Application;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//import com.devbrackets.android.exomedia.ExoMedia;
//import com.devbrackets.android.exomediademo.manager.PlaylistManager;
//import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
//import com.google.android.exoplayer2.upstream.DataSource;
//import com.google.android.exoplayer2.upstream.TransferListener;
//import com.google.android.exoplayer2.upstream.cache.Cache;
//import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
//import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
//import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
//import com.google.android.exoplayer2.upstream.cache.SimpleCache;
//import com.squareup.leakcanary.LeakCanary;
//import com.tencent.bugly.Bugly;
//import com.tencent.bugly.crashreport.CrashReport;
//
//import java.io.File;
//
//import okhttp3.OkHttpClient;
//
//public class App extends Application {
//
//    /**
//     * 播放列表管理类，管理视频，音频，全局的一个实例，在App中初始化，确保只初始化一次
//     */
//    @Nullable
//    private PlaylistManager playlistManager;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//        playlistManager = new PlaylistManager(this);
//        //LeakCanary.install(this);
//
//        configureExoMedia();
//
//        //CrashReport.initCrashReport(getApplicationContext(), "ec8aa931b4", false);
//        //Bugly.init(getApplicationContext(), "ec8aa931b4", true);
//
//    }
//
//    @Nullable
//    public PlaylistManager getPlaylistManager() {
//        return playlistManager;
//    }
//
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
//}
