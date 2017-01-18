package com.pcjh.assistant.Tasks;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.RequestParams;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 0:03;
 * https://github.com/szhua
 * 负责单一上传任务;
 * @author sz.hua
 */
public class UploadTask{

   private  String key ;
   private  RequestParams params;
   private  UploadTask uploadTask ;
    private UploadTaskDao uploadTaskDao ;
    private  int retryTime  ;
    private  static final int nMaxRetryTime =4 ;


   public UploadTask (String key,RequestParams requestParams ){
       if(!TextUtils.isEmpty(key)&&requestParams!=null){
           uploadTask =this ;
           this.key =key ;
           this.params =requestParams ;
       }
   }

    public RequestParams getParams() {
        return params;
    }

    public void setParams(RequestParams params) {
        this.params = params;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void addRetryTimes(){
        retryTime++ ;
    }
    public boolean isEnable(){
        if(retryTime>nMaxRetryTime){
            return  false ;
        }
        return  true ;
    }

    public  synchronized void uploadData(Context context ,UploadTaskResult uploadTaskResult){
        uploadTaskDao =new UploadTaskDao(context,uploadTaskResult,uploadTask);
        uploadTaskDao.request(params);
    }

}
