package com.agitation.sportman.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 上传文件实体类
 */
public class FormFile {
    /* 上传文件的数据 */
    private byte[] data;
    private InputStream inStream;
    private File file;
    //上传文件大小
    private int fileSize;
    /* 上传文件名称，可以作为服务器上显示的文件名称*/
    private String filename;
    /* 请求参数名称 <input type="file" name="file" /> 对应的是input中的name*/
    private String parameterName;
    /* 内容类型 */
    private String contentType = "application/octet-stream";
    
    public FormFile(String filname, File file, String parameterName, String contentType) {
        this.filename = filname;
        this.parameterName = parameterName;
        this.file = file;
        if (this.file == null) {
        	throw new NullPointerException("file 不能为空");
        }
        if(contentType!=null) this.contentType = contentType;
    }
    
	public int getFileSize() {
		return fileSize;
	}

	public File getFile() {
        return file;
    }

    public InputStream getInStream() {
    	if (inStream != null) {
    		try {
				inStream.close();
				inStream = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	try {
            this.inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inStream;
    }

    public byte[] getData() {
        return data;
    }

    public String getFilname() {
        return filename;
    }

    public void setFilname(String filname) {
        this.filename = filname;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
}