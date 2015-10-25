package com.agitation.sportman.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.agitation.sportman.R;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UpdateManager {

	public static final String UPDATE_SERVER = "http://app.tsoft.cn/mam/api/app/check/tsoftSystemManager";
	public static final String APP_PACKAGE = "com.tsoft.android.tsoftsystemmanager";

	private String fileurl;
	public static final String UPDATE_SAVENAME = "tsoftSystemManager_update.apk";
	public static final String ACTION_UPDATE = "ACTION_UPDATE";

	private static final String TAG = "UpdateManager";
	private Context mContext;
	public ProgressDialog pBar;
	private Handler handler = new Handler();
	private AQuery aq;
	private int newVerCode = 0;

	public UpdateManager(Context context) {
		this.mContext = context;
		aq = new AQuery(context);
	}

	public int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(APP_PACKAGE, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	public String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(APP_PACKAGE, 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;

	}

	public String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name).toString();
		return verName;
	}

	public boolean checkUpdate() {

		try {
			// 根据服务器返回的状态读取内容，如果服务器内容没有改变，则直接读取缓存内容，如果服务器内容已经修改，则从服务器拉取数据
			// 并刷新缓存内容
			aq.ajax(UPDATE_SERVER, null, JSONArray.class, new AjaxCallback<JSONArray>() {
				@Override
				public void callback(String url, JSONArray json, AjaxStatus status) {
					if (json != null) {
						Log.e(TAG, json.toString());
						if (json.length() > 0) {
							try {
								JSONObject obj = json.getJSONObject(0);
								newVerCode = Integer.parseInt(obj.getString("verCode"));
								fileurl = obj.getString("fileurl");
								int vercode = getVerCode(mContext);
								if (newVerCode > vercode) {
									doNewVersionUpdate();
								} else {
									// notNewVersionShow();
								}
								// newVerName = obj.getString("verName");
							} catch (Exception e) {
								newVerCode = -1;
							}
						}
					} else {
						Log.e(TAG, "检查更新" + status.getMessage());
						newVerCode = -1;
					}
				}
			});
			if (newVerCode == -1)
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

	private void doNewVersionUpdate() {

		StringBuffer sb = new StringBuffer();
		sb.append("软件有更新,是否现在更新?");
		Dialog dialog = new AlertDialog.Builder(mContext).setTitle("软件更新").setMessage(sb.toString())
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								pBar = new ProgressDialog(mContext);
//								pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
								pBar.setMax(100);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								if (fileurl != null)
									downFile(fileurl);
							}

						}).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"取消"按钮之后退出程序
						// finish();
					}
				}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	void downFile(final String url) {

		File apkfile = new File(Environment.getExternalStorageDirectory(), UPDATE_SAVENAME);
		apkfile.deleteOnExit();

		pBar.show();
		aq.ajax(url, InputStream.class, new AjaxCallback<InputStream>() {

			@Override
			public void callback(String url, InputStream is, AjaxStatus status) {
				FileOutputStream fileOutputStream = null;
				int mFinished = 0;
				try {
					if (is != null) {
						File file = new File(Environment.getExternalStorageDirectory(), UPDATE_SAVENAME);
						long length = is.available();
						fileOutputStream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
//						long time = System.currentTimeMillis();

						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							//把下载的进度发送广播
							mFinished += ch;

//							int progress = (int) (mFinished * 100 / length);
//							pBar.setProgress(progress);
//							Logger.show("downloadProgress", "mFinished:"+mFinished+"---length:"+length+"---progress:"+progress);
//							System.out.println("下载进度:" + progress + "%");
						}
						fileOutputStream.flush();
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}

						pBar.cancel();
						update();
					}else{
						if (status.getCode()==404){
							downCancel("服务器上的文件不存在！");
						}else{
							downCancel("下载文件时出错：" + status.getCode());
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	void downCancel(final String error) {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				Dialog dialog = new AlertDialog.Builder(mContext).setTitle("软件更新错误").setMessage(error)// 设置内容
						.setPositiveButton("确定",// 设置确定按钮
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// ....
									}

								}).create();// 创建
				// 显示对话框
				dialog.show();

			}
		});

	}

	void update() {
		File apkfile = new File(Environment.getExternalStorageDirectory(), UPDATE_SAVENAME);
		if (!apkfile.exists() || apkfile.length() == 0) {
			Toast.makeText(mContext, "文件下载不成功！", Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

}
