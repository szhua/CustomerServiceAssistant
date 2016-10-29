package com.pcjh.liabrary.utils;

import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

/**
 * Create by szhua
 * 2016/3/11
 *
 *
 *
 */
public class DeviceUtil {
    /**
     * 获取屏幕宽度
     * @param activity
     * @return
     */
    public static int getDeviceWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    /**
     * 获取屏幕高度
     * @param activity
     * @return
     */
    public static int getDeviceHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取手机imsi
     * @param context
     * @return
     */
    public static String getDeviceIMSI(Context context){
        if (context == null){
            return "";
        }
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * 获取手机imsi
     * @param context
     * @return
     */
    public static String getDeviceIMEI(Context context){
        if (context == null){
            return "";
        }
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }
}
