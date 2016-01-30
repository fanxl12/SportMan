package com.agitation.sportman;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.agitation.sportman.utils.ImageOptHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
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
        initSecurity();
//        refWatcher = LeakCanary.install(this);
        initImageLoader();
        initShare();
        initPush();
    }

    private void initSecurity() {
        //初始化
//        try {
//            SecurityInit.Initialize(getApplicationContext());
//        } catch (JAQException e) {
//            Log.e("SecurityInit", "errorCode =" + e.getErrorCode());
//        }
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

    private void initPush() {
        //开启推送服务
        PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
        mPushAgent.enable();
        mPushAgent.onAppStart();
//        String device_token = UmengRegistrar.getRegistrationId(this);
//        Log.i("device_token", device_token);

        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            /**
             * 参考集成文档的1.6.3
             * http://dev.umeng.com/push/android/integration#1_6_3
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if(isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

        };
        mPushAgent.setMessageHandler(messageHandler);

        /**
         * 该Handler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * 参考集成文档的1.6.2
         * http://dev.umeng.com/push/android/integration#1_6_2
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };

        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }
}
