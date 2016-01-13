package com.agitation.sportman.utils;


import android.os.Environment;

import com.agitation.sportman.BuildConfig;

/**
 * Created by fanwl on 2015/9/21.
 */
public class Mark {

    private static boolean TEST = BuildConfig.TEST;

    //http://192.168.1.200:8088/tickey
    public static String getServerIp(){
        if (TEST){
//            return "http://192.168.1.200:8083/sport";
            return "http://www.highyundong.com:8080/sport";
        }else {
            return "http://www.highyundong.com:8080/sport";
        }
    }

    //APP文件和数据存放位置
    public static String getFilePath(){
        return Environment.getExternalStorageDirectory() + "/HighSport";
    }

    public static int phoneWidth = 0; //设备屏幕的宽度

    public static int phoneHeight = 0; //设备屏幕的高度

    public static final int ORDER_STATUS_UNPAY = 0;
    public static final int ORDER_STATUS_PAYED = 1;
    public static final int ORDER_STATUS_UNADVICES = 2;
    public static final int ORDER_STATUS_DONE = 3;

    public static final int DATA_REFRESH_SUCCEED = 120;

}
