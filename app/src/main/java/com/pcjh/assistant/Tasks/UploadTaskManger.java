package com.pcjh.assistant.Tasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 0:40;
 * https://github.com/szhua
 * @author sz.hua
 */
public class UploadTaskManger extends UploadManagerBase implements UploadTaskResult {

    //singleTonMode;
    private Context context ;
    private UploadTaskManger(Context context) {this.context =context ;}
    private static class UploadManagerSingleTon{
        private static UploadTaskManger instance(Context context){
            return  new UploadTaskManger(context);
        }
    }
    public  static UploadTaskManger getInstance(Context context){
        return  UploadManagerSingleTon.instance(context);
    }

     /*任务栈允许最大的连接*/
     private static  final int nMaxTasksNum =10;


    private static final  int waitiingTime =100;

    /*任务栈*/
    private HashMap<String ,UploadTask> uploadTasks  ;

    /*用于转换线程*/
    private Handler handler;

    /*是否正在请求、上传*/
    private boolean isRequesting =false;

    private Handler getHandler(){
        if(handler!=null){
            return  handler ;
        }
        handler =new Handler(Looper.getMainLooper());
        return  handler ;
    }

    private UplodTaskFinishListener uplodTaskFinishListener ;

    public void setUplodTaskFinishListener(UplodTaskFinishListener uplodTaskFinishListener) {
        this.uplodTaskFinishListener = uplodTaskFinishListener;
    }

    @Override
    public void addUploadTask(final UploadTask uploadTask) {
        if(uploadTasks==null){
            uploadTasks=new LinkedHashMap<String ,UploadTask>();
        }
        if(isEnable()) {
                //toRequest;
                uploadTasks.put(uploadTask.getKey(), uploadTask);
                request();
            }
    }
    @Override
    public void reMoveUploadTask(UploadTask uploadTask) {
        if(uploadTasks!=null){

        }
    }

    /*请求*/
    @Override
    public void request() {
      if(!isRequesting){
          if(uploadTasks!=null&&!uploadTasks.isEmpty()){
            final UploadTask uploadTask =getHead(uploadTasks).getValue();
            if(uploadTask.isEnable()){
               getHandler().post(new Runnable() {
                   @Override
                   public void run() {
                       uploadTask.uploadData(context,UploadTaskManger.this);
                   }
               });
            }else{
                uploadTasks.remove(uploadTask.getKey());
            }
            isRequesting =true;
          }
          if(uploadTasks.isEmpty()){
              uplodTaskFinishListener.finishedTask();
          }

      }
    }

    /*是否可用*/
    @Override
    public boolean isEnable() {
        if(uploadTasks==null){
            return  false ;
        }

        if(uploadTasks.size()>nMaxTasksNum){
            return  false ;
        }
        return true;
    }

    /*请求失败的情况下*/
    @Override
    public void requetFailed(UploadTask uploadTask) {
        uploadTask.addRetryTimes();
        isRequesting =false;
        /*继续请求*/
        request();
    }




    /*请求成功的情况下*/
    @Override
    public void requestSuccess(UploadTask uploadTask) {
        isRequesting =false;
        /*继续请求*/
        request();
    }

    /*get header data from hashMap*/
    public <K, V> Map.Entry<K, V> getHead(HashMap<K, V> map) {
        return map.entrySet().iterator().next();
    }

    interface   UplodTaskFinishListener {
        void finishedTask() ;
    }
    
}
