package com.pcjh.assistant.util;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by szhua on 2016/10/18.
 * 修改读写权限工具；
 */
public class ChmodUtil {
    /**
     * Set files to the startedMode;
     */
    public static void setFileCanNotRead(File file) {
        try {
            String command = "chmod 600 " + file.getAbsolutePath();
            Log.i("zyl", "command = " + command);
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("su");
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            //  Process proc = runtime.exec(command);
            int status =proc.waitFor();
            if(status==0){
                Log.i("zyl","ok");
            }else{
                Log.i("zyl","failed");
            }

        } catch (IOException e) {
            Log.i("zyl","chmod fail!!!!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("zyl","InterruptedException");
        }
    }



    /**
     * 设置文件的读写权限;一般的情况下使用android 的sdk是不能够改写文件的读写权限的;
     * 使用android系统内部的调用shell的方法设置文件的读写权限；
     */
    public static void setFileCanRead(File file) {
        try {
            String command = "chmod 777 " + file.getAbsolutePath();
            Log.i("zyl", "command = " + command);
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("su");
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            //  Process proc = runtime.exec(command);
            int status =proc.waitFor();
            if(status==0){
                Log.i("zyl","ok");
            }else{
                Log.i("zyl","failed");
            }
        } catch (IOException e) {
            Log.i("zyl","chmod fail!!!!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("zyl","InterruptedException");
        }

    }


    /**
     * Set files to the startedMode; {the second}
     */
    public static void setFileCanNotRead2(File file) {
        try {
            String command = "chmod 700 " + file.getAbsolutePath();
            Log.i("zyl", "command = " + command);
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("su");
            DataOutputStream os = new DataOutputStream(proc.getOutputStream());
            os.writeBytes(command+"\n");
            os.writeBytes("exit\n");
            os.flush();
            //  Process proc = runtime.exec(command);
            int status =proc.waitFor();
            if(status==0){
                Log.i("zyl","ok");
            }else{
                Log.i("zyl","failed");
            }

        } catch (IOException e) {
            Log.i("zyl","chmod fail!!!!");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i("zyl","InterruptedException");
        }
    }

}
