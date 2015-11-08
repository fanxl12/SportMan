package com.agitation.sportman.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;

import com.agitation.sportman.utils.Base64Coder;
import com.loopj.android.image.SmartImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * 自定义控件
 * 封装功能：
 * 【1】圆形图片 —— onDraw(Canvas canvas) & Bitmap getCroppedBitmap(Bitmap bmp, int radius)
 * 【2】图片上传 —— My_Post(String filePath , final Context context) & My_Base64Coder(String base64code , final Context context)
 * 【3】 创建文件 —— CreateFile()
 * 【4】 图片展示 —— ShowView(Intent picdata , My_View my_icon , Context context , int post_way)
 */

public class My_View extends SmartImageView
{

	public My_View(Context context) 
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public My_View(Context context,AttributeSet attrs) 
	{ 
        super(context,attrs); 
    }
	
	//绘制控件方法
	protected void onDraw(Canvas canvas) {

	    Drawable drawable = getDrawable();

	    if (drawable == null) {
	        return;
	    }

	    if (getWidth() == 0 || getHeight() == 0) {
	        return; 
	    }
	    
	    Bitmap b =  ((BitmapDrawable)drawable).getBitmap();
	    
	    if(null == b)
	    {
	    	return;
	    }
	    
	    Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

		int w = getWidth() ;


	    Bitmap roundBitmap =  getCroppedBitmap(bitmap, w);
	    canvas.drawBitmap(roundBitmap, 0,0, null);

	}

	//计算圆形控件的宽高方法
	public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
	    Bitmap sbmp;
	    if(bmp.getWidth() != radius || bmp.getHeight() != radius)
	        sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
	    else
	        sbmp = bmp;
	    Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
	            sbmp.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	    
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

	    paint.setAntiAlias(true);
	    paint.setFilterBitmap(true);
	    paint.setDither(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(Color.parseColor("#BAB399"));
	    canvas.drawCircle(sbmp.getWidth() / 2+0.7f, sbmp.getHeight() / 2+0.7f,
	            sbmp.getWidth() / 2+0.1f, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(sbmp, rect, rect, paint);
	    
	    return output;
	}
	
	//图片上传方法1，传统图片上传方式，将图片资源直接Post到服务器
//	public void My_Post(String filePath , final Context context , String url)
//	{
//		/*
//		 * 参数说明 ：
//		 * filePath : 图片资源的地址
//		 * context  : 当前Activity
//		 * url      : Post地址
//		 */
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("name", new File(filePath));	//name 是你post字段的字段名
//		HttpUtils http = new HttpUtils();
//		http.send(HttpRequest.HttpMethod.POST,
//				  url,
//				  params,
//				  new RequestCallBack<String>() {
//			@Override
//			public void onStart()
//			{
//
//			}
//			public void onFailure(HttpException arg0, String arg1)
//			{
//
//			}
//			public void onSuccess(ResponseInfo<String> responseInfo)
//			{
//				Toast.makeText(context , responseInfo.result , Toast.LENGTH_SHORT).show();
//			}
//		    });
//	}
	
	//图片上传方法2，利用Base64Code 编码将图片资源资源以字符串的形式Post到服务器
//	public void My_Base64Coder(String base64code , final Context context , String url)
//	{
//		/*
//		 * 参数说明：
//		 * base64code : 图片资源 转换后的 base64code 字符串
//		 * context    : 当前Activity
//		 * url        : Post地址
//		 */
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("name", base64code);	//name 是你post字段的字段名 , 逗号后面是你post的值
//		HttpUtils http = new HttpUtils();
//		http.send(HttpRequest.HttpMethod.POST,
//				  url,
//				  params,
//				  new RequestCallBack<String>() {
//			@Override
//			public void onStart()
//			{
//
//			}
//			@Override
//			public void onFailure(HttpException arg0, String arg1)
//			{
//
//			}
//			@Override
//			public void onSuccess(ResponseInfo<String> responseInfo)
//			{
//				Toast.makeText(context , responseInfo.result , Toast.LENGTH_SHORT).show();
//			}
//		    });
//	}
	
	public void CreateFile()
	{
		//如果在手机内存中不存在名为“my_icon”的文件夹则创建它
		  File destDir = new File(Environment.getExternalStorageDirectory() + "/my_icon");
		  if (!destDir.exists()) 
		  {
			  destDir.mkdirs();
		  }		  
	}
	
	public void SaveBitmap(Bitmap mBitmap) throws IOException 
	{			  
		//在“my_icon”文件夹中创建 “my_icon.jpg”图片文件保存裁剪的图片资源
		File f = new File(Environment.getExternalStorageDirectory() + "/my_icon/my_icon.jpg"); 
	    f.createNewFile();  
	    FileOutputStream fOut = null;  
	    try 
	    {
	    	fOut = new FileOutputStream(f);  
	    } 
	    catch (FileNotFoundException e) 
	    {
	    	e.printStackTrace();  
	    }  
	    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);  
	    try 
	    {
	    	fOut.flush();  
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();  
	    }  
	    try 
	    {
	    	fOut.close();  
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();  
	    } 
	}
	
	@SuppressWarnings("deprecation")
	//显示图片并上传
	public void ShowView(Intent picdata , My_View my_icon , Context context , int post_way , String url) 
	{
		/*
		 * 参数说明：
		 * my_icon  : 控件实例
		 * context  : 当前Activity 
		 * post_way : 图片上传方式（1/2）
		 */
		Bundle extras = picdata.getExtras();
		if (extras != null) 
		{
			Bitmap photo = extras.getParcelable("data");
			try 
			{
				SaveBitmap(photo);
								
			}			
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			Drawable drawable = new BitmapDrawable(photo);			
			my_icon.setImageDrawable(drawable);
			
			switch(post_way)
			{
			case 1 :			
//					My_Post(Environment.getExternalStorageDirectory()+ "/my_icon/my_icon.jpg" , context , url);
				
				break ;
				
			case 2 :
				    //将图片资源转换为 Base64Code 字符串
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 60, stream);
					byte[] b = stream.toByteArray();			
					String base64code = new String(Base64Coder.encodeLines(b));
					
//					My_Base64Coder(base64code , context , url);
				break ;
				
			default:
	            break;
			}
		}
	}
}
