package com.agitation.sportman.utils;

import android.os.Handler;
import android.os.Message;

import com.agitation.sportman.entity.FormFile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 上传文件到服务器
 * 
 * @author yjw
 *
 */
public class HttpURLConnectionRequester {

    public static final int UPLOAD_PROGRESS = 235;
    public static final int UPLOAD_SUCCESS = 200;

    /**
     * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能:
     *   <FORM METHOD=POST ACTION="http://yjc-pc:8080/WebAppProject/main.do" enctype="multipart/form-data">
            <INPUT TYPE="text" NAME="name">
            <INPUT TYPE="text" NAME="id">
            <input type="file" name="imagefile"/>
            <input type="file" name="zip"/>
         </FORM>
     * @param path 上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://www.iteye.cn或http://192.168.1.101:8083这样的路径测试)
     * @param params 请求参数 key为参数名,value为参数值
     * @param files 上传文件
     */
	public static String post(String path, Map<String, String> params, FormFile[] files,Handler handler) throws Exception{
        try {
		final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
        final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志
        int fileDataLength = 0;	//文件长度
        for(FormFile uploadFile : files){//得到文件类型数据的总长度
            StringBuilder fileExplain = new StringBuilder();
             fileExplain.append("--");
             fileExplain.append(BOUNDARY);
             fileExplain.append("\r\n");
             fileExplain.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() + "\"\r\n");
             fileExplain.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
             fileExplain.append("\r\n");
             fileDataLength += fileExplain.length();
            if(uploadFile.getInStream()!=null){
            	if (uploadFile.getFile() != null) {
            		fileDataLength += uploadFile.getFile().length();
            	} else {
            		fileDataLength += uploadFile.getFileSize();
            	}
             }else{
                 fileDataLength += uploadFile.getData().length;
             }
        }
        StringBuilder textEntity = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {//构造文本类型参数的实体数据
            textEntity.append("--");
            textEntity.append(BOUNDARY);
            textEntity.append("\r\n");
            textEntity.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"\r\n\r\n");
            textEntity.append(entry.getValue());
            textEntity.append("\r\n");
        }
        //计算传输给服务器的实体数据总长度
        int dataLength = textEntity.toString() .getBytes().length + fileDataLength +  endline.getBytes().length;

        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        String auth = params.get("userName")+":"+params.get("passWord");

        //设置用户认证信息
        String encoded = Base64.encode(auth.getBytes());
        urlConnection.setRequestProperty("Authorization", "Basic "+encoded);

        //设置连接超时时间
        urlConnection.setConnectTimeout(3000);
        //打开输入流，以便从服务器获取数据
        urlConnection.setDoInput(true);
        //打开输出流，以便向服务器提交数据
        urlConnection.setDoOutput(true);
        //使用Post方式不能使用缓存
        urlConnection.setUseCaches(false);
        // 设置以POST方式进行传送
        urlConnection.setRequestMethod("POST");

        // 设置RequestProperty
        urlConnection.setRequestProperty("Connection", "Keep-Alive");
        urlConnection.setRequestProperty("Charset", "UTF-8");
        //设置请求的类型，文件上传
        urlConnection.setRequestProperty("Content-Type",
                "multipart/form-data;boundary=" + BOUNDARY + "\r\n");
        //设置请求体的长度
        urlConnection.setRequestProperty("Content-Length", String.valueOf(dataLength));

        // 构造DataOutputStream流
        DataOutputStream ds = new DataOutputStream(urlConnection.getOutputStream());

        //把所有文本类型的实体数据发送出来
        ds.write(textEntity.toString().getBytes());

        //把所有文件类型的实体数据发送出来
        for(FormFile uploadFile : files){
            int lenTotal = 0;
            StringBuilder fileEntity = new StringBuilder();
             fileEntity.append("--");
             fileEntity.append(BOUNDARY);
             fileEntity.append("\r\n");
             fileEntity.append("Content-Disposition: form-data;name=\""+ uploadFile.getParameterName()+"\";filename=\""+ uploadFile.getFilname() + "\"\r\n");
             fileEntity.append("Content-Type: "+ uploadFile.getContentType()+"\r\n\r\n");
            ds.write(fileEntity.toString().getBytes());
             InputStream is = uploadFile.getInStream();
             if(is!=null) {
                 byte[] buffer = new byte[1024];
                 int len = 0;
                 int totalLength = is.available();
	                 while((len = is.read(buffer, 0, 1024))!=-1){
                         ds.write(buffer, 0, len);
	                     lenTotal += len;	//每次上传的长度
	                     Message message = new Message();
	                     message.what = UPLOAD_PROGRESS;
	                     message.obj = uploadFile;
                         message.arg1 = lenTotal;
                         message.arg2 = totalLength;
	                     handler.sendMessage(message);
	                 }
	                 is.close();   
             }else{
                 ds.write(uploadFile.getData(), 0, uploadFile.getData().length);
             }
            ds.write("\r\n".getBytes());
        }
        //下面发送数据结束标志，表示数据已经结束
        ds.write(endline.getBytes());
        ds.flush();
        String resultStr = null;
        int reposeCode = urlConnection.getResponseCode();
        if (reposeCode==HttpURLConnection.HTTP_OK){
            InputStream inptStream = urlConnection.getInputStream();
            resultStr = dealResponseResult(inptStream);
        }
        // 关闭DataOutputStream
        ds.close();
        return resultStr;
        } catch(Exception e) {
        	e.printStackTrace();
        	 return null;
        }
    }
    
    /**
     * 提交数据到服务器
     * @param path 上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://www.itcast.cn或http://192.168.1.10:8080这样的路径测试)
     * @param params 请求参数 key为参数名,value为参数值
     * @param file 上传文件
     */
    public static String post(String path, Map<String, String> params, FormFile file,Handler handler) throws Exception{
       return post(path, params, new FormFile[]{file},handler);
    }

    /*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     * Author    :   博客园-依旧淡然
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }
    
}