package androidalldemo.jan.jason.myimageloader;

import android.app.Application;

import androidalldemo.jan.jason.myimageloader.imageloader.cache.MemoryDiskCache;
import androidalldemo.jan.jason.myimageloader.imageloader.config.ImageLoaderConfig;
import androidalldemo.jan.jason.myimageloader.imageloader.loader.SimpleImageLoader;
import androidalldemo.jan.jason.myimageloader.imageloader.policy.SerialPolicy;

/**
 * Application配置
 */
public class ApplicationUtils {

    public static void doOnCreate(Application application) {

        initExceptionHandler();
        initImageLoader(application);
    }


    private static void initImageLoader(Application application) {
        //配置
        ImageLoaderConfig.Builder build = new ImageLoaderConfig.Builder();
        build.setThreadCount(3) //线程数量
                .setLoadPolicy(new SerialPolicy()) //加载策略
                .setCachePolicy(new MemoryDiskCache(application)) //缓存策略
                .setLoadingImage(R.drawable.loading)
                .setErrorImage(R.drawable.not_found);

        ImageLoaderConfig config = build.build();
        //初始化
        SimpleImageLoader.init(config);
    }


    private static void initExceptionHandler() {

    }
}
