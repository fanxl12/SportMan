package com.agitation.sportman.utils;


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
        }else {
            return "http://bug.tsoft.cn:8080/t";
        }
    }

    public static String IMAGE_URL = "";
}
