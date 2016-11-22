package com.pcjh.assistant;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pcjh.assistant.util.SharedPrefsUtil;

/**
 * Created by szhua  on 2016/11/18.
 */
public class MyNetWorkChangedListener extends BroadcastReceiver {
    private NetworkInfo netInfo;
    private ConnectivityManager  mConnectivityManager;
    boolean isServiceRunning = false;
    /////////////监听网络状态变化的广播接收器
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        /**
         * 监测service是否在运行 ;
         */
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
            if("com.pcjh.assistant.WorkService".equals(service.service.getClassName()))
            {
                isServiceRunning = true;
            }
        }

        switch (action){
            /**
             * 网络状态改变的情况下启动服务 ；
             */
            case ConnectivityManager.CONNECTIVITY_ACTION :
                mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = mConnectivityManager.getActiveNetworkInfo();
                if(netInfo != null && netInfo.isAvailable()) {
                    /////////////网络连接
                    if(netInfo.getType()==ConnectivityManager.TYPE_WIFI){
                        /////WiFi网络的情况下进行数据的上传 ;
                        if(SharedPrefsUtil.getValue(context,"ishasnet",false)){
                            if(!isServiceRunning) {
                                context.startService(new Intent(context, WorkService.class));
                                SharedPrefsUtil.putValue(context, "ishasnet", true);
                            }
                        }

                    }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET){
                        /////有线网络
                        SharedPrefsUtil.putValue(context,"ishasnet",false);

                    }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){
                        /////////3g网络
                        SharedPrefsUtil.putValue(context,"ishasnet",false);
                    }
                } else {
                    ////////网络断开
                    SharedPrefsUtil.putValue(context,"ishasnet",false);
                }
                break;

            /**
             * 一个每隔一分钟执行一次的任务，用此来重启服务 ；
             */
            case  Intent.ACTION_TIME_TICK :
                  if(!isServiceRunning){
                   context.startService(new Intent(context, WorkService.class));
                  }
                break;
        }
    }
}
