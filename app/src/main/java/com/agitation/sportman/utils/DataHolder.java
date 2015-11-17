package com.agitation.sportman.utils;

import com.androidquery.auth.BasicHandle;

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

    public BasicHandle getBasicHandle() {
        return basicHandle;
    }

    public void setBasicHandle(BasicHandle basicHandle) {
        this.basicHandle = basicHandle;
    }


}
