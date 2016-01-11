package com.agitation.sportman.utils;

import android.graphics.Bitmap;

import com.androidquery.auth.BasicHandle;

import java.util.Map;

/**
 * Created by fanwl on 2015/9/21.
 */
public class DataHolder {

    private static volatile DataHolder dataHolder;
    private DataHolder(){}

    public static DataHolder getInstance(){
        if (dataHolder==null){
            synchronized (DataHolder.class){
                if (dataHolder==null){
                    dataHolder = new DataHolder();
                }
            }
        }
        return dataHolder;
    }
    private BasicHandle basicHandle;
    private String userName;
    private String passWord;

    public BasicHandle getBasicHandle() {
        return basicHandle;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setBasicHandle(String userName, String passWord) {
        this.userName=userName;
        this.passWord=passWord;
        this.basicHandle = new BasicHandle(userName, passWord);
    }

    private Map<String,Object> userData;

    public Map<String, Object> getUserData() {
        return userData;
    }

    private String imageProfix;

    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }

    public void setImageProfix(String imageProfix) {
        this.imageProfix = imageProfix;
    }

    public String getImageProfix() {
        return imageProfix;
    }

    private boolean isLogin=false;

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    private Bitmap centerHeadBit;

    public Bitmap getCenterHeadBit() {
        return centerHeadBit;
    }

    public void setCenterHeadBit(Bitmap centerHeadBit) {
        this.centerHeadBit = centerHeadBit;
    }

    private double latitude; //纬度
    private double longitude; //经度

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private String deviceTokens;

    public String getDeviceTokens() {
        return deviceTokens;
    }

    public void setDeviceTokens(String deviceTokens) {
        this.deviceTokens = deviceTokens;
    }
}
