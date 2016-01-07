package com.agitation.sportman.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    public static String formatDateToHour(Date time){
        if (time==null)return "";
        SimpleDateFormat sourceSf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        return sourceSf.format(time);
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

    /**
     * 把一个Double类型的数字格式化两位小数
     * @param number
     * @return formatResult
     */
    public static double format1Decimal(double number){
        BigDecimal format = new BigDecimal(number);
        return format.setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue();
    }

}
