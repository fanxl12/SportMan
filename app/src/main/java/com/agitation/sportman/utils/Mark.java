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
            return "http://192.168.1.200:8083/sport";
//            return "http://www.fanxl.cn:8080/sport";
        }else {
            return "http://www.fanxl.cn:8080/sport";
        }
    }

    //APP文件和数据存放位置
    public static String getFilePath(){
        return Environment.getExternalStorageDirectory() + "/HighSport";
    }

    public static int phoneWidth = 0; //设备屏幕的宽度

    public static int phoneHeight = 0; //设备屏幕的高度

}
