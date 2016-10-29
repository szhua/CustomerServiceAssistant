package com.pcjh.liabrary.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.pcjh.liabrary.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/11/2 0002.
 */
public class UiUtil {
    /**
     * 自定义toast样式
     *
     * @param context
     * @param message
     */


    public static Toast toast;

    public static void showLongToast(Context context, String message) {
        if (context == null) {
            return;
        }
        //这样做的原因是连续的出现Toast ；
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
        TextView textMsg = (TextView) view.findViewById(R.id.toastMsg);
        textMsg.setText(message);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 在屏幕中间的toast
     *
     * @param context
     * @param message
     */
    public static void showLongToastCenter(Context context, String message) {
        if (context == null) {
            return;
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout_center, null);
        TextView textMsg = (TextView) view.findViewById(R.id.toastMsg);
        textMsg.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * 在屏幕中间的toast
     *
     * @param context
     * @param message
     */
    public static void showLongToastCenterWithMsg(Context context, String message, int res) {
        if (context == null) {
            return;
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_layout_center_with_img, null);
        TextView textMsg = (TextView) view.findViewById(R.id.toastMsg);
        textMsg.setText(message);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        iv.setImageResource(res);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    /*
     * 验证手机号码，（请自觉使用规范的正则表达式）
     *
     * @param mobileNo
     * @return
     */
    public static boolean isValidMobileNo(String mobileNo) {
        boolean flag = false;
        // Pattern p = Pattern.compile("^(1[358][13567890])(\\d{8})$");
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher match = p.matcher(mobileNo);
        if (mobileNo != null) {
            flag = match.matches();
        }
        return flag;
    }


    /**
     * 隐藏从底部弹出的view
     *
     * @param context
     * @param v
     */
    public static void hide_menu_alpha(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        v.setVisibility(View.GONE);
        v.setAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_out));
    }


    /**
     * Scale in ；
     *
     * @param context
     * @param v
     */
    public static void show_menu_scale(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        v.setVisibility(View.VISIBLE);
        v.setAnimation(AnimationUtils.loadAnimation(context,
                R.anim.scale_in));
    }

    /**
     * Scale in ；
     *
     * @param context
     * @param v
     */
    public static void hide_menu_scale(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        v.setVisibility(View.GONE);
        v.setAnimation(AnimationUtils.loadAnimation(context,
                R.anim.scale_out));
    }


    /**
     * 渐变显示menu ；
     *
     * @param context
     * @param v
     */
    public static void show_menu_alpha(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        v.setVisibility(View.VISIBLE);
        v.setAnimation(AnimationUtils.loadAnimation(context,
                R.anim.alpha_in));


    }


    /**
     * 隐藏从底部弹出的view
     *
     * @param context
     * @param v
     */
    public static void hide_menu(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        v.setVisibility(View.GONE);
        v.setAnimation(AnimationUtils.loadAnimation(context,
                R.anim.push_top_out));
    }

    /**
     * 从底部弹出view
     *
     * @param context
     * @param v
     */
    public static void show_menu(Context context, View v) {
        if (context == null || v == null) {
            return;
        }
        v.setVisibility(View.VISIBLE);
        v.setAnimation(AnimationUtils.loadAnimation(context,
                R.anim.push_top_in));
    }
}
