package com.agitation.sportman.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by fanwl on 2015/9/21.
 */
public class UtilsHelper {


    /**
     * 获取软件版本号
     * @param context
     * @return 软件版本号
     */
    public static String getAppVersion(Context context){
        StringBuffer sb = new StringBuffer();
        PackageInfo pkg = null;
        try {
            pkg = context.getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            String appName = pkg.applicationInfo.loadLabel(context.getPackageManager()).toString();
            String versionName = pkg.versionName;
            sb.append(appName).append(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


//    public static AlertDialog.Builder getDialog(Context context, String title){
//        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//        View customTitleView = LayoutInflater.from(context).inflate(R.layout.dialog_title, null);
//        TextView tips = (TextView) customTitleView.findViewById(R.id.dialog_tips);
//        tips.setText(title);
//        dialog.setCustomTitle(customTitleView);
//        return dialog;
//    }

    /**
     * @param source
     * @param sqlWhere
     * @return
     */
    public static List<Map<String, Object>> selectMapList(List<Map<String, Object>> source, String sqlWhere) {
        if (source == null)
            return null;

        Query q = new Query();
        q.addFunctionHandler(new MapValue());
        // Parse the SQL you are going to use.
        try {
            q.parse("SELECT * FROM java.util.Map WHERE " + sqlWhere);
            QueryResults qr = q.execute(source);
            return qr.getResults();
        } catch (QueryParseException e) {
            e.printStackTrace();
        } catch (QueryExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }
        return screenHeight;
    }

    public static String getPriority(Object object){
        int priority = Integer.parseInt(object.toString());
        switch (priority){
            case 0:
                return "无";
            case 2:
                return "低";
            case 4:
                return "中";
            case 6:
                return "高";
            case 8:
                return "加急";
            case 10:
                return "特急";
        }
        return "无";
    }

    public static String getSeriousness(Object object){
        int priority = Integer.parseInt(object.toString());
        switch (priority){
            case 0:
                return "无";
            case 2:
                return "细节";
            case 4:
                return "小调整";
            case 6:
                return "新功能";
            case 8:
                return "小错误";
            case 10:
                return "很严重";
            case 12:
                return "崩溃";
        }
        return "无";
    }

    public static String getUploadDate(String uploadDay, String uploadDate){
        int day = Integer.parseInt(uploadDay);
        if (day>=3 && day<=30){
            return uploadDay+"天前 "+uploadDate;
        }else{
            return uploadDate;
        }
    }

    public static String getFinishDate(String finishDay, String finishDate){
        int day = Integer.parseInt(finishDay);
        if (day>=3){
            return finishDate+" "+ finishDay +"天后";
        }else if (day<0){
            return finishDate+" "+ Math.abs(day) +"天前";
        }else{
            return finishDate;
        }
    }

//    public static String formatDate(String dateTime){
//        if (dateTime==null || TextUtils.isEmpty(dateTime) || "null".equals(dateTime))return "";
//        Date date = null;
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//        SimpleDateFormat sourceSf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a", Locale.US);
//        try {
//            date = sourceSf.parse(dateTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        if (date==null)return "";
//        return sf.format(date);
//    }

    public static String formatDate(Date dateTime){
        if (dateTime==null)return "";
        SimpleDateFormat sourceSf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return sourceSf.format(dateTime);
    }
    /*
    获取当前系统时间
     */

    public static String getCurrentTime(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
//        String mHour =String.valueOf(c.get(Calendar.HOUR_OF_DAY)) ;//获取小时
//        String mMinute =String.valueOf(c.get(Calendar.MINUTE)) ;//获取分钟
//        String mSecond =String.valueOf(c.get(Calendar.SECOND)) ;//获取秒
        String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(mWay)){
            mWay ="天";
        }
        else if("2".equals(mWay)){
            mWay ="一";
        }
        else if("3".equals(mWay)){
            mWay ="二";
        }
        else if("4".equals(mWay)){
            mWay ="三";
        }
        else if("5".equals(mWay)){
            mWay ="四";
        }
        else if("6".equals(mWay)){
            mWay ="五";
        }
        else if("7".equals(mWay)){
            mWay ="六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日"+"/星期"+mWay;
    }


}
