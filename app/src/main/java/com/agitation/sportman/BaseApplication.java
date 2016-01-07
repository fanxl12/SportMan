package com.agitation.sportman;

import android.app.Application;

import com.agitation.sportman.utils.ImageOptHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by fanxl on 2015/7/15.
 */
public class BaseApplication extends Application {

    //OOM监控类 github https://github.com/square/leakcanary
//    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
//        refWatcher = LeakCanary.install(this);
        initImageLoader();
        initShare();
    }

    private void initShare() {
        //微信分享
        PlatformConfig.setWeixin("wxa14ff301f487441b", "9d84dccdf623e88d96f401b380cad623");
        //QQ和空间的分享
        PlatformConfig.setQQZone("1105016705", "u5VZKTmYAhbEBRTw");
    }

    private void initImageLoader() {

		//缓存目录
//		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageloader/Cache");

        @SuppressWarnings("deprecation")
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2) //缓存池的优先级
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .discCacheSize(10 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
//                .discCache() //设置自定义的缓存目录
                .build();
		//Initialize ImageLoader with configuration
		ImageLoader.getInstance().init(config);
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        BaseApplication application = (BaseApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }
}
