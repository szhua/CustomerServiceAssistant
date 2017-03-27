package com.pcjh.assistant;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.BaseService;
import com.pcjh.assistant.dao.AppendFansDao;
import com.pcjh.assistant.dao.UploadChatFileDao;
import com.pcjh.assistant.dao.UploadChatLogsDao;
import com.pcjh.assistant.dao.UploadChatLogsForFileDao;
import com.pcjh.assistant.db.DBCipherManager;
import com.pcjh.assistant.entity.ContactForJson;
import com.pcjh.assistant.entity.ContactForJsonBase;
import com.pcjh.assistant.entity.FileReturnEntity;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.MessageForJson;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.ServiceUserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.EncryptUtil;
import com.pcjh.assistant.util.XmlPaser;
import net.sqlcipher.database.SQLiteDatabase;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/*This Service is Persistent Service. Do some what you want to do here.<br/>*/
public class WorkService extends BaseService implements INetResult{
    /*使用static weakrefrence 防止内存泄漏*/
    static class WorkHandler extends Handler {
        WeakReference<WorkService> workService;
        WorkHandler(WorkService workService) {
            this.workService = new WeakReference<>(workService);
        }
        @Override
        public void handleMessage(Message msg) {
            if(workService!=null){
                WorkService work = workService.get();
                if(work!=null){
                work.startWork();
                }
            }
        }
    }

     private WorkHandler workHandler =new WorkHandler(this) ;


     public static Subscription sSubscription;

     /*需要上传的json*/
     private String json;

    private  static  Subscription  sSubscription2;
    private  static String token;
    private  static String  uin;
    private  static String  wx;


    private CustomBinder customBinder ;
    private CustomConnection customConnection ;






    private int onStart(Intent intent) {

//        //启动前台服务而不显示通知的漏洞已在 API Level 25 修复，大快人心！
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
//            //利用漏洞在 API Level 17 及以下的 Android 系统中，启动前台服务而不显示通知 ;
//            startForeground(sHashCode, new Notification());
//            //利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
//                startService(new Intent(getApplication(), WorkNotificationService.class));
//        }

        //启动守护服务，运行在:watch子进程中
        startService(new Intent(getApplication(), WatchDogService.class));
      //   若还没有取消订阅，说明任务仍在运行，为防止重复启动，直接返回START_STICKY
        Log.i("szhua","startService") ;

        if(workHandler==null){
           workHandler =new WorkHandler(this);
        }

        workHandler.sendEmptyMessage(0);

        if (sSubscription != null && !sSubscription.isUnsubscribed()&&sSubscription2!=null&&!sSubscription2.isUnsubscribed()) return START_STICKY;

        sSubscription = Observable
                .interval(5, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted(){
                    }
                    @Override
                    public void onError(Throwable e){
                    }
                    @Override
                    public void onNext(Long count) {
                        Log.i("testIsRunning","isRunning!"+Thread.currentThread().getName());
                    }
                });



        sSubscription2 =  Observable
                .interval(60, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted(){
                    }
                    @Override
                    public void onError(Throwable e){
                    }
                    @Override
                    public void onNext(Long count) {
                        Log.i("testIsRunning","isRunning2!"+Thread.currentThread().getName());
                        workHandler.sendEmptyMessage(0) ;
                    }
                });

        /**
         * 设置WatchDogService为保活的状态
         */
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(getPackageName(), WatchDogService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        return START_STICKY;
    }
//
//=======================================================================================
//


    private  static Users  users ;
    /**
     * 从本地数据库中获得的联系人；(缓存联系人)
     */
    private ArrayMap<String,RConact> conacts =new ArrayMap<>() ;

    /**
     * 从微信中获得联系人
     */
    private ArrayMap<String, RConact>  conactsFromWx =new ArrayMap<>();
    /***
     * 第一次请求的情况联系人 ；
     */
    boolean isFirstGetConacts =true ;
    /**
     * 增加的联系人 ；
      */
    private ArrayMap<String, RConact>  rcAdded = new ArrayMap<>();
    /**
     * 改变的联系人
     */
    private ArrayMap<String, RConact>  rcChanged =  new ArrayMap<>();


    private AppendFansDao appendFansDao =new AppendFansDao(this,this) ;
    private UploadChatLogsDao uploadChatLogsDao =new UploadChatLogsDao(this,this) ;
    private UploadChatFileDao uploadChatFileDao =new UploadChatFileDao(this,this) ;
    private UploadChatLogsForFileDao uploadChatLogsForFilesDao =new UploadChatLogsForFileDao(this,this) ;
    /**
     * Key:LabelId;
     * Value:Lable;
     */
    private HashMap<String,Label> labels =new HashMap<>();
    private ArrayList<WMessage>  wmsgsText =new ArrayList<>();
    private ArrayList<WMessage>  wmsgsFile =new ArrayList<>();


    private boolean isUploadingTextMessge =false;
    private boolean isUploadingFileMessge =false;
    /**
     * 当前正在上传的File Msg
     */
    private WMessage currentUploadFileMsg = null;
    /**
     * 一次上传的最大message数量 ；
     */
    private static final  int nMaxMessage = 100;
    /**
     * 一次上传的最大联系人数量；
     */
    private static final  int nMaxContact = 400;
    /**
     * 最多的重试次数;
     */
    private static final int  nMaxRetryCount =3 ;


    private  static final int  APPENDTYPE =0 ;
    private static final int  CHANGETYPE =1 ;

    /* 上传连天记录的方式*/
    private static final int UPLOADFILELOGTYPE = 0 ;
    private static final int UPLOAPTEXTLOGTYPE = 1 ;

    /* 任务开始的时候的时间*/
    private long startTime  ;
    private ArrayMap<String, Label>  labelsFromWx =new ArrayMap<>() ;
    @Override
    public void onCreate() {
        super.onCreate();
        if(customBinder==null){
            customBinder =new CustomBinder() ;
        }
        customConnection =new CustomConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*初始化sqlChpiher*/
        SQLiteDatabase.loadLibs(getBaseContext());

        WorkService.this.bindService(new Intent(WorkService.this,WatchDogService.class),customConnection, Context.BIND_IMPORTANT);

        Notification.Builder builder =new Notification.Builder(this);
        PendingIntent pendingIntent =PendingIntent.getService(this,0,intent,0);
        builder.setSmallIcon(R.drawable.header_icon)
                .setContentIntent(pendingIntent)
                .setTicker("启动服务中")
                .setAutoCancel(false)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("客服助手");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            builder.setContentInfo("客服助手");
        }
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){
            startForeground(startId,builder.build());
        }else{
            startForeground(startId,builder.getNotification());
        }

        return onStart(intent);
    }

    /*任务开始*/
    public void startWork(){

        ServiceUserInfo serviceUserInfo = null;
        try {
            serviceUserInfo = DBCipherManager.getInstance(this).getInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(serviceUserInfo!=null){

             Log.i("szhua",serviceUserInfo.toString());

             wx =serviceUserInfo.getUsername() ;
             uin =serviceUserInfo.getUin() ;
             token =serviceUserInfo.getToken();
             users =new Users();
             users.setDbPath(serviceUserInfo.getDbPath());

             //防止0的占位；
             String pass  ;
             if(serviceUserInfo.getPass().length()!=7){
              int requiredComplement =7-serviceUserInfo.getPass().length() ;
              String   requiredComplementString ="";
              for (int i =0 ;i<requiredComplement;i++){
                  requiredComplementString +="0" ;
              }
              pass =requiredComplementString+serviceUserInfo.getPass() ;
             }else{
              pass =serviceUserInfo.getPass() ;
             }
             users.setPassword(pass);
             users.setUin(serviceUserInfo.getUin());

         }else{
            Log.i("szhua","serviceInfoNull");
            if(users==null){
                return;
            }
        }


        if(!isUploadingFileMessge&&!isUploadingTextMessge){
            startTime =System.currentTimeMillis();
            try {
                sendMessageToSever();
            }catch (Exception e){
                e.printStackTrace();
                isUploadingFileMessge =false ;
                isUploadingTextMessge =false ;
                Log.e("szhua","has erro but the msg is unKnowed!");
            }
        }else{
            long endTime =System.currentTimeMillis() ;
            //超过四分钟的话，就进行重新开始任务；
            if(((endTime-startTime)/1000)>240){
                startTime =System.currentTimeMillis() ;
                try {
                    sendMessageToSever();
                }catch (Exception e){
                    isUploadingFileMessge =false ;
                    isUploadingTextMessge =false ;
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 主任务
     */
    public void sendMessageToSever() {
        /**
         * 刚开始便执行正在上传任务了 ；
         */
        isUploadingFileMessge =true ;
        isUploadingTextMessge =true ;
        currentUploadFileMsg = null;
        rcAdded.clear();
        rcChanged.clear();
        wmsgsText.clear();
        wmsgsFile.clear();
        labelsFromWx.clear();
        conactsFromWx.clear();


        /*检测当前的微信账号*/
        try {
            String uin = XmlPaser.getUidFromFile();
            //当前的账号和原来的不一样的情况下;或者登陆出去了 ;
            //重新获取文件；
            if(!getUinString().equals(uin)&&uin.equals("0")){
               resetUsesrs();
            }

        }catch (Exception e){
            e.printStackTrace();
             isUploadingFileMessge =false ;
             isUploadingTextMessge =false ;
        }
        /**
         * 从微信中获取标签 ;
         */
        try {
             Log.i("szhua","pass"+users.getPassword())  ;
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
        ArrayMap<String, RConact> conactsCache = new ArrayMap<>();
        /**
         * 将没用的联系人过滤掉 ；
         */

        try {
            for(String key :conactsFromWx.keySet()){
                RConact rc =conactsFromWx.get(key);
                //username不是以下的属性就行；
                if (!rc.getUsername().equals("weixin") && !rc.getUsername().substring(0, 3).equals("gh_")
                        && !rc.getUsername().equals("filehelper")&&!rc.getUsername().contains("@chatroom")&&!rc.getUsername().contains("fake_")) {
                    conactsCache.put(key,rc);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            isUploadingFileMessge=false ;
            isUploadingTextMessge =false ;
        }

        conactsFromWx.clear();
        conactsFromWx.putAll((SimpleArrayMap<? extends String, ? extends RConact>) conactsCache);

        Log.i("szhua","contactsSizeWx"+conactsFromWx.size());
        /**
         * 若是第一次进入该service的时候查询数据库 ;
         * 若是contacts为空那么可能缓存的数据出现了错误，进行重新从本地数据库中获得；
         */
        if (isFirstGetConacts||conacts==null||conacts.isEmpty()) {
            try {
             conacts = DBCipherManager.getInstance(getBaseContext()).quryForRconacts();
            }catch (Exception e){
                isUploadingFileMessge =false;
                isUploadingTextMessge =false;
                conacts =new ArrayMap<>();
            }
        }
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

        try {
            if (!rcAdded.isEmpty()) {

                Log.i("szhua","addedSize"+rcAdded.size());
                for (String key :rcAdded.keySet()){
                    rcAdded.get(key).setLableChageed(true);
                }
                addContactsToSever(rcAdded,labelsFromWx,APPENDTYPE);
            }

            if(!rcChanged.isEmpty()){
                addContactsToSever(rcChanged,labelsFromWx,CHANGETYPE);
            }

        }catch (Exception e){
            isUploadingTextMessge =false ;
            isUploadingFileMessge =false  ;
        }


        /*
        更新缓存的数据
         */
        labels.clear();
        labels.putAll(labelsFromWx);

        conacts.clear();

        conacts.putAll((SimpleArrayMap<? extends String, ? extends RConact>) conactsFromWx);
        /*上传聊天记录;*/
        uploadMessage();
    }


    /* 上传聊天记录 ;*/
    public void  uploadMessage(){

        ArrayList<WMessage> wmsgs = new ArrayList<>();
      try{
        wmsgs = (ArrayList<WMessage>) queryMessage(users,getMsgId(),nMaxMessage);
      }catch (Exception e){
          isUploadingFileMessge =false ;
          isUploadingTextMessge =false ;
      }
        /* 抽取有用的信息  */
        for (WMessage wmsg : wmsgs) {
            if (conacts.containsKey(wmsg.getTalker())) {
                wmsg.setDisplayName(conacts.get(wmsg.getTalker()).getAlias());
                //对信息进行分类；
                classifyMessage(wmsg);
            }
        }
        Log.i("szhua","wmsgsize"+wmsgs.size());
        /**检查是否又重复上传的情况； 若是有的情况下，停止上传 ；（对变量不作处理 */
        Log.i("szhua","msgId:"+getMsgId());


        /**检查后上传*/
    if(!checkMessageIsReSent()){
           sendAllMessageToSever(wmsgs);
    }else{
         isUploadingFileMessge =false ;
         isUploadingTextMessge =false ;
     }

    if(wmsgsText.isEmpty()&&wmsgsFile.isEmpty()&&!wmsgs.isEmpty()){
        setMsgId(wmsgs.get(wmsgs.size()-1).getMsgId());
    }
        //释放内存；
        wmsgs.clear();
    }



    /**将所有的聊天记录上传到服务器 ；*/

    public void sendAllMessageToSever(ArrayList<WMessage> wmsgs ){
        /*存储这一次上传前的msgId; */
        setMessageIdPreSend();

        //将文件的信息传递到服务器；
        if (!wmsgsFile.isEmpty()){
            Log.i("jsonSize","wtextFile"+wmsgsFile.size());
            sendFileMessageToServer();
        }else{
            isUploadingFileMessge =false ;
        }
        if(wmsgsText.isEmpty()){
            isUploadingTextMessge =false ;
        }else {
            Log.i("jsonSize","wtextSize"+wmsgsText.size());
            //将文本的信息传递到服务器；
            sendTextMessageToSever(wmsgsText,UPLOAPTEXTLOGTYPE);
        }
        /* 两个都为空的情况下 ；可能获得的信息不包含有用的 */
        if(wmsgsFile.isEmpty()&&wmsgsText.isEmpty()&&wmsgs!=null&&wmsgs.size()>0){
            setMsgId(wmsgs.get(wmsgs.size()-1).getMsgId());
        }
    }


    /*上传服务器成功以后为Message设置上传的路径； */
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


    /*上传文件聊天到服务器*/
    public void sendFileMessageToServer(){
        //若是没有上传文件的路径的话，就进行上传;
        this.currentUploadFileMsg = null;
        for (WMessage wMessage : wmsgsFile) {
            if (TextUtils.isEmpty(wMessage.getFilePath())) {
                //  若是达到最大的上传次数的话；
                if (wMessage.getUploadToServerCount() > nMaxRetryCount) {
                    wMessage.setFilePath("replaceFilePath");
                }else{
                    currentUploadFileMsg = wMessage;
                    break;
                }
            }
        }


           if(currentUploadFileMsg==null) {
                json = changeMessageToJson(wmsgsFile, UPLOADFILELOGTYPE);
           }else{
               json ="";
           }
           changeThreadToMain(new Handle() {
               @Override
               void handler() {
                   if(currentUploadFileMsg == null){
                       //无可上传文件，则上传文件结束，开始上传聊天信息
                       uploadChatLogsForFilesDao.uploadChatLogsForFile(getWx(),getToken(),json);
                   }else{
                       //上传文件
                       currentUploadFileMsg.addUploadCount();
                       uploadChatFileDao.uploadChatFile(getWx(),getToken(),currentUploadFileMsg.getFile(),currentUploadFileMsg.getMsgId());
                   }
               }
           });
    }
    /* 上传文本聊天到服务器；*/
    public  void sendTextMessageToSever(final ArrayList<WMessage> wmsgsText, final int type){
        if ( wmsgsText.size() > 0) {
               final String json  =changeMessageToJson(wmsgsText,UPLOAPTEXTLOGTYPE) ;
               changeThreadToMain(new Handle() {
                  @Override
                  void handler() {
                     try {
                         if(type==UPLOADFILELOGTYPE){
                             uploadChatLogsForFilesDao.uploadChatLogsForFile(getWx(),getToken(),json);
                             uploadChatLogsForFilesDao.setMsgId(wmsgsText.get(wmsgsText.size()-1).getMsgId());
                         }else{
                             uploadChatLogsDao.uploadChatLogs(getWx(),getToken(),json);
                             uploadChatLogsDao.setMsgId(wmsgsText.get(wmsgsText.size()-1).getMsgId());
                         }
                     }catch (Exception e){
                         e.printStackTrace();
                     }
                  }
              });
        }
    }


    /*将联系人转换成需要上传的格式;(toJson and Gzip ) */
     public String changeContactsToJson(ArrayList<ContactForJsonBase> contactForJsonBases){
         String json ="" ;
         try {
             json = JsonUtil.pojo2json(contactForJsonBases);
             Log.i("jsonSize","fansJson:"+json.length()) ;
             json =EncryptUtil.encryptGZIP(json);
             Log.i("jsonSize","fansJsonZip:"+json.length()) ;
         } catch (IOException e) {
             e.printStackTrace();
             isFirstGetConacts =false;
         }
         return  json ;
     }

    /*将message转换成服务器需要的json格式 ;(toJson and Gzip ) */
    public String  changeMessageToJson(ArrayList<WMessage> wMessages ,int type)   {

        ArrayList<MessageForJson> messageForJsons =new ArrayList<>() ;
       try {
           for (WMessage wMessage : wMessages) {
               MessageForJson mfj =new MessageForJson() ;
               mfj.setAdd_time(wMessage.getCreateTime());
               mfj.setFans_wx(wMessage.getDisplayName());
               mfj.setContent(wMessage.getContent());
               mfj.setType(""+wMessage.getSendType());
               if(TextUtils.isEmpty(wMessage.getFilesize())){mfj.setFilesize(wMessage.getFilesize());}else{mfj.setFilesize("");}
               if(wMessage.getIsSend().equals("1")) {mfj.setDirect("0");} else{mfj.setDirect("1");}
               if(!TextUtils.isEmpty(wMessage.getServerPath())){mfj.setServer(wMessage.getServerPath());}else{mfj.setServer("");}
               messageForJsons.add(mfj);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
        String json ="" ;
        try {
            json = JsonUtil.pojo2json(messageForJsons);
//            if(type==UPLOADFILELOGTYPE){
//            Log.i("jsonSize","jsonMessageFile:"+json.length()) ;
//            }else{
//               Log.i("jsonSize","jsonMessage:"+json.length()) ;
//            }
            json = EncryptUtil.encryptGZIP(json) ;


            messageForJsons.clear();

//            if(type==UPLOADFILELOGTYPE){
//                 Log.i("jsonSize","jsonMessageZipFile:"+json.length()) ;
//            }else{
//                Log.i("jsonSize","jsonMessageZip:"+json.length()) ;
//            }
        } catch (IOException e) {
            //解析出错的情况下 ;
            switch (type){
                case UPLOADFILELOGTYPE:
                    isUploadingFileMessge =false ;
                    break;
                case UPLOAPTEXTLOGTYPE:
                    isUploadingTextMessge =false;
                    break;
            }
            e.printStackTrace();
        }
        return  json ;
    }



    /*对信息进行分类并添加在文件和文本集合中*/
    public void classifyMessage(WMessage wmsg ){
        /*是音频的情况下*/
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




    /**比较两个联系人是否相同*/
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

       /*这里我们将tags 改变的用户进行特殊的处理 */
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

    /*上传新增联系人/或是更改联系人*/
    public void addContactsToSever(ArrayMap<String ,RConact> rcAdded , ArrayMap<String,Label> labelsFromWx, final int type){
        final ArrayList<ContactForJsonBase> contactsForJsons = new ArrayList<>();
        for (String key : rcAdded.keySet()) {
            RConact rConact = rcAdded.get(key);
            ContactForJsonBase contactToAdd ;


            /*若是标签改变的情况下*/
            if (rConact.isLableChageed) {
                contactToAdd = new ContactForJson();
                ArrayList<String> tags = new ArrayList<>();
                if (!TextUtils.isEmpty(rConact.getContactLabelIds())) {
                    String[] a = rConact.getContactLabelIds().split(",");
                    for (String s : a) {
                        if (labels.get(s) != null) {
                            tags.add(labels.get(s).getLabelName());
                        } else if (labelsFromWx.get(s) != null) {
                            tags.add(labelsFromWx.get(s).getLabelName());
                        }
                    }
                }
         ((ContactForJson) contactToAdd).setTagname(tags);
          } else {
                contactToAdd = new ContactForJsonBase();
 }
           contactToAdd.setFans_wx(rConact.getAlias());
            if (!TextUtils.isEmpty(rConact.getNickname())) {
                contactToAdd.setFans_nickname(rConact.getNickname());
            } else {
                contactToAdd.setFans_nickname("");
            }
            if (!TextUtils.isEmpty(rConact.getConRemark())) {
                contactToAdd.setRemark(rConact.getConRemark());
            } else {
                contactToAdd.setRemark("");
            }
            contactToAdd.setModify_time("" + System.currentTimeMillis() / 1000);
            contactToAdd.setModify_type(rConact.getType());
            contactsForJsons.add(contactToAdd);

            if (contactsForJsons.size() >= nMaxContact) {

                final String json =changeContactsToJson(contactsForJsons) ;
                contactsForJsons.clear();
                changeThreadToMain(new Handle() {
                    @Override
                    void handler() {
                     if (type == APPENDTYPE) {
                            appendFansDao.apppendFans(getWx(), getToken(), json);
                 } else {
                            appendFansDao.changeFans(getWx(),getToken(),json);
                     }
                    }
                });
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        /*处于循环外*/
        if (contactsForJsons.size() > 0) {
            final String json =changeContactsToJson(contactsForJsons) ;
            contactsForJsons.clear();
           changeThreadToMain(new Handle() {
               @Override
               void handler() {
                   if(type==APPENDTYPE){
                       appendFansDao.apppendFans(getWx(),getToken(),json);}
                   else{
                       appendFansDao.changeFans(getWx(),getToken(),json);
                   }
            }
           });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return customBinder;
    }

    private void onEnd() {

        System.out.println("保存数据到磁盘。");
        startService(new Intent(getBaseContext(),this.getClass()));

         try {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setComponent(new ComponentName("com.pcjh.assistant","com.pcjh.assistant.StartActivity"));
            startActivity(intent);

          } catch (Exception e) {
             Toast.makeText(this, "启动异常", Toast.LENGTH_SHORT).show();
          }

        startService(new Intent(getApplication(), WorkService.class));
        startService(new Intent(getApplication(), WatchDogService.class));
    }

    /**
     * 最近任务列表中划掉卡片时回调
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        onEnd();
    }

    /**
     *请求成功时候的处理 ；
     * @param requestCode 网络请求顺序号，第一个请求，NetRequestOrderNum=0,处理第一条请求的结果。如果等于1,
     */
    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);

        try {
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
            else if(requestCode==RequestCode.UPLOADTEXT){
                isUploadingTextMessge =false;
                setMessaageIdInCache();
            }

            /**
             * 传递完文件信息的话 ；
             */
            else  if(requestCode==RequestCode.UPLOADTEXTFORFILE){
                isUploadingFileMessge =false;
                setMessaageIdInCache();
            }

            else if(requestCode==RequestCode.APPENDFANS){
                if(!rcAdded.isEmpty()){
                    try {

                        DBCipherManager.getInstance(getBaseContext()).updateContacts(rcAdded);
                        isFirstGetConacts =false;

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
                        isFirstGetConacts =false ;
                    }catch (Exception e){
                        isUploadingTextMessge =false ;
                        isUploadingFileMessge =false ;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            isUploadingFileMessge =false ;
            isUploadingTextMessge =false ;
        }
    }





    @Override
    public void onRequestError(int requestCode, String errorInfo, int error_code) {

         try {
             if(requestCode==RequestCode.UPLOADFILE){
                 sendFileMessageToServer();
             }else
             /**
              * 传递完文本信息的话 ；
              * 就停止，下次传递 ；不更新msgId ;
              */
                 if(requestCode==RequestCode.UPLOADTEXT){
                     isUploadingTextMessge =false;
                 }else
                 /**
                  * 传递完文件信息的话；
                  * 就停止，下次传递 ；不更新msgId ;
                  */
                     if(requestCode==RequestCode.UPLOADTEXTFORFILE){
                         isUploadingFileMessge =false;
                     }

                     //出错以后，删除数据重新上传；
                     else if(requestCode==RequestCode.APPENDFANS){
                         isFirstGetConacts =true;
                         DBCipherManager.getInstance(getBaseContext()).deleteAllContacts();
                         isUploadingTextMessge =false ;
                         isUploadingFileMessge =false ;
                     }
                     else if(requestCode==RequestCode.CHANGEFANS){
                         isFirstGetConacts =true;
                         DBCipherManager.getInstance(getBaseContext()).deleteAllContacts();
                         isUploadingTextMessge =false ;
                         isUploadingFileMessge =false ;
                     }

             Log.i("szhua","requestErro");
         }catch (Exception e){
             e.printStackTrace();
             isUploadingFileMessge =false ;
             isUploadingTextMessge =false ;
         }

    }

    /* 在这里我们的json解析出错也会在这个回调里面 ;*/
    @Override
    public void onRequestFaild(int requestCode, String errorNo, String errorMessage) {
        Log.i("szhua","requestFailed") ;

         try {
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
             }
             /**
              * 传递完文件信息的话；
              * 就停止，下次传递 ；不更新msgId ;
              */
             if(requestCode==RequestCode.UPLOADTEXTFORFILE){
                 isUploadingFileMessge =false;
             }
         }catch (Exception e){
             e.printStackTrace();

             isUploadingFileMessge =false ;
             isUploadingTextMessge =false ;
         }

    }
    @Override
    public void onNoConnect() {
        isUploadingTextMessge=false ;
        isUploadingFileMessge=false ;
    }

    public String getWx (){

        if(TextUtils.isEmpty(wx)){

            try {
                return  DBCipherManager.getInstance(this).getInfo().getUsername();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wx ;
    }
    public String getToken (){
        if(TextUtils.isEmpty(token)){
            try {
                return  DBCipherManager.getInstance(this).getInfo().getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  token ;
    }
    public String getMsgId(){
        String msgid =DBCipherManager.getInstance(getBaseContext()).getMsgId(getUinString()) ;
        if(TextUtils.isEmpty(msgid)){
            msgid = "0";
        }
        return  msgid ;
    }
    //todo  checkIsRight?；（切换账号的情况下） ；
    public  void setMsgId(String msgId){
        /** 加一层安全设置 ; 若是现在的msgId 比原来设置的小的话，那么不进行处理;*/
        if(Integer.parseInt(msgId)>=Integer.parseInt(getMsgId())){
            DBCipherManager.getInstance(getBaseContext()).updateMsgId(getUinString(), msgId);
        }
    }

    @Override
    public void onDestroy() {
        onEnd();
        super.onDestroy();
    }
    /* 判断聊天记录是否在重复的上传 ;*/
    public boolean checkMessageIsReSent(){
        int textId =0 ;
        int fileId =0 ;
        int msgIdPreSend =getMessageIdPreSend() ;
        if(!wmsgsText.isEmpty()){
            textId = Integer.parseInt(wmsgsText.get(wmsgsText.size()-1).getMsgId()) ;
        }
        if(!wmsgsFile.isEmpty()){
            fileId =Integer.parseInt(wmsgsFile.get(wmsgsFile.size()-1).getMsgId()) ;
        }
        /* 若是没有成功的情况的话，msgId不会增加，再次从数据库取数据的话msgIdPreSend会和Math.max(textId,fileId) 相等，这种情况不计算在内 ；*/
        /* 若是上次传递时候的msgId 大于现在的msgId,那么现在的聊天记录肯定是获取的重复的，不要再上传 ；*/

          if(msgIdPreSend>Math.max(textId,fileId)){
              return  true;
          }
        /*若是xml存储的msgId 和现在的msgId 相等的话也是重复了 ;请求还没完成又重新启了服务的情况  ;()todo  */
         return  Integer.parseInt(getMsgId())==Math.max(textId,fileId) ;

    }
    public int getMessageIdPreSend(){
        String msgid = DBCipherManager.getInstance(getBaseContext()).getMsgIdPreSend(getUinString());
        if(TextUtils.isEmpty(msgid)){
            return  0 ;
        }
        return  Integer.parseInt(msgid);
    }
    /**
     * 设置上传聊天记录时候（请求之前的msgId） ；
     */
    public void setMessageIdPreSend(){
        int textId =0 ;
        int fileId =0 ;
        if(!wmsgsText.isEmpty()){
            textId = Integer.parseInt(wmsgsText.get(wmsgsText.size()-1).getMsgId()) ;
        }
        if(!wmsgsFile.isEmpty()){
            fileId =Integer.parseInt(wmsgsFile.get(wmsgsFile.size()-1).getMsgId()) ;
        }
     if(textId!=fileId)
     DBCipherManager.getInstance(getBaseContext()).updateMsgIdPreSend(getUinString(),""+Math.max(textId,fileId));
    }
    /* 在xml中存储messageId  ;(用于下次请求使用) ：   */
    public void setMessaageIdInCache (){

        int textId =0;
        int fileId =0;

        if(!wmsgsText.isEmpty()){
         textId =Integer.parseInt(wmsgsText.get(wmsgsText.size()-1).getMsgId()) ;
        }
        if(!wmsgsFile.isEmpty()){
            fileId =Integer.parseInt(wmsgsFile.get(wmsgsFile.size()-1).getMsgId()) ;
        }
        if(textId!=fileId)
        setMsgId(""+Math.max(textId,fileId));
    }

    public String getUinString(){
        if(TextUtils.isEmpty(uin)){
            try {
                return DBCipherManager.getInstance(this).getInfo().getUin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  uin ;
    }

//    /**
//     * 前台服务的启动 ;
//     */
//    public static class WorkNotificationService extends Service {
//        /**
//         * 利用漏洞在 API Level 18 及以上的 Android 系统中，启动前台服务而不显示通知
//         */
//        @Override
//        public int onStartCommand(Intent intent, int flags, int startId) {
//            startForeground(WorkService.sHashCode, new Notification());
//            stopSelf();
//            return START_STICKY;
//        }
//        @Override
//        public IBinder onBind(Intent intent) {
//            return null;
//        }
//    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private  class  MyService extends NotificationListenerService {
//        @Override
//        public void onNotificationPosted(StatusBarNotification sbn) {
//        }
//        @Override
//        public void onNotificationRemoved(StatusBarNotification sbn) {
//        }
//    }


    /*实现类*/
    private abstract  class  Handle{
      abstract void handler ();
    }
    /**
     * 换线程(用于asyncHttp的请求)
     * @param handle
     */
    public void changeThreadToMain(final Handle handle){
    Handler mainHandler =new Handler(Looper.getMainLooper()) ;
    mainHandler.post(new Runnable() {
        @Override
        public void run() {
           handle.handler();
        }
    }) ;
    }
    private class CustomBinder extends  CustomAidlInterface.Stub{
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }

    private class CustomConnection implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            WorkService.this.startService(new Intent(WorkService.this,WatchDogService.class));
            WorkService.this.bindService(new Intent(WorkService.this,WatchDogService.class),customConnection,Context.BIND_IMPORTANT) ;
        }
    }

}
