package com.agitation.sportman.utils;

import android.os.Handler;
import android.os.Message;

import com.agitation.sportman.entity.FormFile;

import java.util.List;
import java.util.Map;

/**
 * 文件管理的批量上传功能
 * Created by Fanxl on 2015/10/29.
 */
public class MultiUploadThread implements Runnable {


    private FormFile[] formFile;
    private Handler mHandler;
    private Map<String, String> params;
    private String url;

    public static final int UPLOAD_PROGRESS = 235;
    public static final int UPLOAD_SUCCESS = 200;

    //单文件上传
    public MultiUploadThread(String url, FormFile formFile, Handler mHandler, Map<String, String> params) {
        super();
        this.url = url;
        this.formFile = new FormFile[1];
        this.formFile[0] = formFile;
        this.mHandler = mHandler;
        this.params = params;
    }

    //多文件上传
    public MultiUploadThread(String url, List<FormFile> formFile, Handler mHandler, Map<String, String> params) {
        super();
        if (formFile == null || formFile.isEmpty()) {
            throw new NullPointerException("param formFile is null or empty");
        }
        FormFile[] tempFile = new FormFile[formFile.size()];
        this.formFile = formFile.toArray(tempFile);
        this.mHandler = mHandler;
        this.params = params;
        this.url = url;
    }

    @Override
    public void run() {
        String result = uploadFile(formFile, params);
        Message msg= mHandler.obtainMessage();
        if (msg != null && result!=null) {
            msg.obj = result;
            msg.what = UPLOAD_SUCCESS;
            mHandler.sendMessage(msg);
        }
    }

    public String uploadFile(FormFile[] formFiles, Map<String, String> params) {
        //请求普通信息参数
        String result = "";
        try {
            result = HttpURLConnectionRequester.post(url, params, formFiles, mHandler);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
