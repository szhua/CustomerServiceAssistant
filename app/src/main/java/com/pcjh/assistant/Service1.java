package com.pcjh.assistant;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mengma.asynchttp.Http;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseService;
import com.pcjh.assistant.dao.AppendFansDao;
import com.pcjh.assistant.dao.RemoveFansDao;
import com.pcjh.assistant.dao.RemoveFansTagDao;
import com.pcjh.assistant.dao.SetFansTagDao;
import com.pcjh.assistant.dao.UploadChatFileDao;
import com.pcjh.assistant.dao.UploadChatFileDaoFirst;
import com.pcjh.assistant.dao.UploadChatLogsDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.FileReturnEntity;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.LabelConact;
import com.pcjh.assistant.entity.LabelGroup;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.liabrary.utils.UiUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 */
public class Service1 extends BaseService implements INetResult{

    private UserInfo  userInfo;
    private Users  users ;
    private ArrayList<WMessage> messgaeList = new ArrayList<WMessage>();
    private ArrayList<RConact> RConacts = new ArrayList<RConact>();
    private Subscription subscriptions;
    private AppendFansDao appendFansDao =new AppendFansDao(this,this) ;
    private UploadChatLogsDao uploadChatLogsDao =new UploadChatLogsDao(this,this) ;
    private SetFansTagDao setFansTagDao =new SetFansTagDao(this,this) ;
    private RemoveFansDao removeFansDao =new RemoveFansDao(this,this) ;
    private RemoveFansTagDao removeFansTagDao =new RemoveFansTagDao(this,this) ;

    /**
     * 周期获得聊天数据的message ；
     */
    private ArrayList<WMessage>  data2 =new ArrayList<WMessage>();

    /**
     * 带有标签的人；
     */
    private ArrayList<LabelGroup> labelGroups =new ArrayList<LabelGroup>();
    private ArrayList<Label> labels =new ArrayList<Label>() ;
    private ArrayList<RConact> labelRconacts =new ArrayList<RConact>() ;

    /**
     * 减少的联系人
     */
    private ArrayList<RConact>  less;
    /**
     * 增加的联系人;
     */
    private ArrayList<RConact>  added ;
    /**
     * 现在的联系人 ；
     */
    private ArrayList<RConact>  currentData;

    private DbManager  dbManager;
    /**
     * 数据库中含有标签的联系人
     */
    private ArrayList<LabelConact> labelConacts =new ArrayList<LabelConact>() ;
    /**
     * 现在的含有标签的联系人 ;
     */
    private ArrayList<LabelConact>  labelConactsNow;

    /**
     * 增加的带有标签的联系人
     */
    private ArrayList<LabelGroup>  lgAdded;
    /**
     * 减少的带有标签的联系人 ;
     */
    private ArrayList<LabelGroup>  lgLess;



    /**
     * 用于第一次此加载数据完成后通知进行周期的获取数据 ;
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(subscriptions!=null){
                subscriptions.unsubscribe();
            }
            getMessageTimer();
        }
    };


    /**
     * 实现轮循上传文件的机制 ；
     */
    /**
     * 第一请求的时候带有文件的Message ;
     */
    private ArrayList<WMessage>  wmsgsFile;
    private Handler handlerUploadFile =new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(wmsgsFile.get(0)!=null){
                wMessage =wmsgsFile.get(0) ;
                wmsgsFile.remove(wMessage);
                uploadChatFileDaoFirst.uploadChatFile("shuweineng888",AppHolder.getInstance().getToken(),wMessage.getFile(),wMessage.getMsgId());
            }
        }
    } ;
    private UploadChatFileDaoFirst uploadChatFileDaoFirst =new UploadChatFileDaoFirst(this,this) ;
    private WMessage  wMessage;

    /**
     * 实现轮循上传文件的机制 ；
     */
    private ArrayList<WMessage> wmsgsFileTimer ;
    private WMessage  wMessageTimer;
    private Handler handlerUploadFileTimer =new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if(wmsgsFileTimer.get(0)!=null){
                wMessageTimer =wmsgsFileTimer.get(0) ;
                wmsgsFileTimer.remove(wMessageTimer);
                uploadChatFileDaoTimer.uploadChatFile("shuweineng888",AppHolder.getInstance().getToken(),wMessageTimer.getFile(),wMessageTimer.getMsgId());
            }
        }
    } ;
    private UploadChatFileDao uploadChatFileDaoTimer =new UploadChatFileDao(this,this) ;


    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

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
        /**
         * 用户第一次进入这个界面的时候把所有的数据进行上传；
         */

        if(!TextUtils.isEmpty(users.getPassword())) {
            if (SharedPrefsUtil.getValue(this, "isFirstGetData", true)) {
                uploadMessageToServerFirst();
            } else {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                }, 0, 60000);
            }
        }
    }

    public void uploadMessageToServerFirst(){
        subscription =  Observable
                .zip(getConnactLabelIds(users), getRconacts(users), getMessages(users),getRconactsForLabel(users), new Func4<List<Label>, List<RConact>, List<WMessage>,List<RConact> ,Object>() {
                    @Override
                    public Object call(List<Label> labels, List<RConact> conacts, List<WMessage> wMessages ,List<RConact> rConact) {
                        /**
                         * 获得符合需求的联系人;(向数据库中添加联系人)
                         */
                        RConacts = (ArrayList<RConact>) conacts;
                        ArrayList<RConact> data =new ArrayList<RConact>();
                        for (int i = 0; i < RConacts.size(); i++) {
                            RConact rc =RConacts.get(i);
                            if(rc.getTalker().equals("weixin")||  rc.getTalker().substring(0,3).equals("gh_")||rc.getTalker().equals("filehelper")||Integer.parseInt(rc.getType())%2==0||AppHolder.getInstance().getUser().getWxId().equals(rc.getTalker())){
                                data.add(rc);
                            }
                        }
                        RConacts.removeAll(data);
                        DbManager dbManager =new DbManager(getBaseContext()) ;
                        dbManager.addRconact(RConacts);
                        /**
                         * 向数据库中添加标签；
                         */
                        Service1.this.labels = (ArrayList<Label>) labels;
                        dbManager.addLabel(Service1.this.labels);

                        /**
                         * (有效联系人的聊天记录) ；
                         */
                        messgaeList = (ArrayList<WMessage>) wMessages;
                        ArrayList<RConact> data1 = (ArrayList<RConact>) RConacts;
                        data2 =new ArrayList<WMessage>() ;
                        for (WMessage wMessage : messgaeList) {
                            for (RConact conact : data1) {
                                if(wMessage.getTalker().equals(conact.getTalker())){
                                    if(!TextUtils.isEmpty(conact.getAlias())){
                                        wMessage.setDisplayName(conact.getAlias());
                                    }else{
                                        wMessage.setDisplayName(conact.getTalker());
                                    }
                                    data2.add(wMessage);
                                }
                            }
                        }


                        /**
                         * 获得带有标签的联系人，转换成上传的实体;
                         * 获得带有标签的联系人，并且转换为一对一的数据；
                         */
                        labelRconacts = (ArrayList<RConact>) rConact;
                        ArrayList<LabelConact> labelConacts =new ArrayList<LabelConact>() ;
                        for (Label label : labels) {
                            ArrayList<RConact> rc =new ArrayList<RConact>() ;
                            LabelGroup lg =new LabelGroup(label,rc) ;
                            for (RConact labelRconact : labelRconacts) {
                                if(labelRconact.getContactLabelIds().contains(label.getLabelID())){
                                    rc.add(labelRconact);
                                    //向带有标签的联系人中添加；
                                    LabelConact labelConact =new LabelConact() ;
                                    labelConact.setLabel(label);
                                    labelConact.setRconact(labelRconact);
                                    labelConacts.add(labelConact);
                                }
                            }
                            labelGroups.add(lg) ;
                        }
                        /**
                         * 向本地的数据库添加带有标签的联系人；
                         */
                        dbManager.addLabelConact(labelConacts);
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object> () {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("szhua","erroFirstStep:"+e.toString());
                    }
                    @Override
                    public void onNext(Object o) {


                        Log.i("szhua",AppHolder.getInstance().getToken());
                        for (LabelGroup labelGroup : labelGroups) {
                            setFansTagDao.setFansTag("shuweineng888",AppHolder.getInstance().getToken(),labelGroup);
                        }
                        if(RConacts.size()>0) {
                            appendFansDao.apppendFans("shuweineng888", AppHolder.getInstance().getToken(), RConacts);
                        }

                        /**
                         * 第一次上传所有的数据 ；
                         */
                        if(data2!=null&&data2.size()>0){
                            ArrayList<WMessage> wmsgsText =new ArrayList<WMessage>() ;
                            wmsgsFile =new ArrayList<WMessage>();
                            for (WMessage wMessage :  data2) {
                                if (wMessage.getImgPath()!= null) {
                                    /**
                                     * 是音频的情况下；
                                     */
                                    if(wMessage.isVoice){
                                        //音频的路径 ；
                                        String voicePath =Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + users.getUserId()+wMessage.getImgPath();
                                        Log.i("szhua","voicePaht"+voicePath);
                                        File file = new File(voicePath);
                                        if(file.exists()){
                                            wMessage.setFile(file);
                                            wMessage.setSendType(1);
                                            wmsgsFile.add(wMessage);
                                        }

                                    }else{
                                        //tupian的路径; 图片的上传
                                        String orign= wMessage.getImgPath() ;
                                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + users.getUserId() + "/image2/" + orign + ".jpg";
                                        Log.i("szhua","path"+path) ;
                                        File file = new File(path);
                                        if(file.exists()){
                                            wMessage.setFile(file);
                                            wMessage.setSendType(2);
                                            wmsgsFile.add(wMessage);
                                        }
                                    }
                                } else {
                                    //纯文本的shangchuan;
                                    wMessage.setSendType(0);
                                    wmsgsText.add(wMessage) ;
                                }
                            }

                            if(wmsgsFile!=null&&wmsgsFile.size()>0){
                                if(wmsgsFile.get(0)!=null){
                                    wMessage =wmsgsFile.get(0) ;
                                    wmsgsFile.remove(wMessage);
                                    uploadChatFileDaoFirst.uploadChatFile("shuweineng888",AppHolder.getInstance().getToken(),wMessage.getFile(),wMessage.getMsgId());
                                }
                            }

                            if(wmsgsText!=null&&wmsgsText.size()>0){
                                uploadChatLogsDao.uploadChatLogs("shuweineng888",AppHolder.getInstance().getToken(),wmsgsText,"","");
                            }
                        }
                        /**
                         *设置下次访问的聊天时间 ；
                         */
                        SharedPrefsUtil.putValue(getBaseContext(),"lastCreateTime",Long.parseLong(data2.get(data2.size()-1).getCreateTime()));
                        SharedPrefsUtil.putValue(getBaseContext(),"isFirstGetData",false);
                        /**
                         * 每60秒进行请求一次;
                         * 第一次请求延迟一些 ；
                         */
                        Timer timer =new Timer() ;
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(0) ;
                            }
                        },30000,60000);
                    }
                }) ;
    }

    public void  getMessageTimer(){
        subscriptions =    Observable
                .zip(getConnactLabelIds(users), getRconacts(users),getRconactsForLabel(users), new Func3<List<Label>, List<RConact>,List<RConact>, Object>() {
                    @Override
                    public Object call(List<Label> labelss, List<RConact> conacts,List<RConact> labelRc) {
                        /**
                         * 现在的未进行过滤的联系人;
                         */
                        currentData = (ArrayList<RConact>) conacts;
                        ArrayList<RConact> data =new ArrayList<RConact>();
                        for (int i = 0; i < currentData.size(); i++) {
                            RConact rc =currentData.get(i);
                            if(rc.getTalker().equals("weixin") ||  rc.getTalker().substring(0,3).equals("gh_")||rc.getTalker().equals("filehelper")||Integer.parseInt(rc.getType())%2==0||userInfo.getWxId().equals(rc.getTalker())){
                                data.add(rc);
                            }
                        }


                        /**
                         * 得到需要的联系人；
                         */
                        currentData.removeAll(data);
                        /**
                         * 原来的联系人;
                         */
                        dbManager =new DbManager(getBaseContext()) ;
                        ArrayList<RConact> Dbdata = (ArrayList<RConact>) dbManager.quryForRconacts();
                        less =new ArrayList<RConact>();
                        added =new ArrayList<RConact>() ;
                        for (RConact rConact : currentData) {
                            boolean has =false ;
                            for (RConact conact : Dbdata) {
                                if(conact.getTalker().equals(rConact.getTalker())){
                                    has =true ;
                                }
                            }
                            if (!has){
                                added.add(rConact) ;
                            }
                        }
                        for (RConact rConact : Dbdata) {
                            boolean has =false ;
                            for (RConact conact : currentData) {
                                if(conact.getTalker().equals(rConact.getTalker())){
                                    has =true ;
                                }
                            }
                            if(!has){
                                less.add(rConact) ;
                            }
                        }

                        /**
                         * 原来的带有标签的联系人 ；
                         */
                        labelConacts =(ArrayList<LabelConact>)dbManager.queryLabelConact();

                        /**
                         * 现在的带有标签的联系人  ;
                         */
                        labelConactsNow =new ArrayList<LabelConact>() ;
                        labels = (ArrayList<Label>) labelss;


                        for (Label label : labels) {
                            for (RConact rConact : currentData) {
                                if(!TextUtils.isEmpty(rConact.getContactLabelIds())) {
                                    if (rConact.getContactLabelIds().contains(label.getLabelID())) {
                                        LabelConact la = new LabelConact();
                                        la.setRconact(rConact);
                                        la.setLabel(label);
                                        labelConactsNow.add(la);
                                    }
                                }
                            }
                        }
                        /**
                         * 两者进行比较；
                         */
                        //减少的
                        ArrayList<LabelConact> laLess =new ArrayList<LabelConact>() ;
                        //增加的
                        ArrayList<LabelConact> laAdded =new ArrayList<LabelConact>() ;

                        //现在的没有原来的话就是减少的；
                        for (LabelConact labelConact : labelConacts) {
                            boolean ishas =false ;
                            for (LabelConact conact : labelConactsNow) {
                                if (conact.getRconact().getUsername().equals(labelConact.getRconact().getUsername())&&conact.getLabel().getLabelID().equals(labelConact.getLabel().getLabelID())){
                                    ishas =true ;
                                }
                            }
                            if(!ishas){
                                laLess.add(labelConact) ;
                            }
                        }

                        //原来的没有现在的话就是增加了；
                        for (LabelConact labelConact : labelConactsNow) {
                            boolean ishas =false ;
                            for (LabelConact conact : labelConacts) {
                                if (conact.getRconact().getUsername().equals(labelConact.getRconact().getUsername())&&conact.getLabel().getLabelID().equals(labelConact.getLabel().getLabelID())){
                                    ishas =true ;
                                }
                            }
                            if(!ishas){
                                laAdded.add(labelConact) ;
                            }
                        }
                        DbManager dbManager =new DbManager(getBaseContext()) ;

                        if(laAdded.size()>0)
                            dbManager.addLabelConact(laAdded);
                        if(laLess.size()>0)
                            dbManager.deleteLabelConacts(laLess);

                        /**
                         * 转化成上传到服务器的数组；
                         */
                        lgAdded =new ArrayList<LabelGroup>() ;
                        lgLess =new ArrayList<LabelGroup>() ;
                        ArrayList<Label> labelslast = (ArrayList<Label>) dbManager.queryLabels();
                        ArrayList<Label> lableAddeed =new ArrayList<Label>() ;
                        ArrayList<Label> lableLess =new ArrayList<Label>() ;

                        for (Label label : labels) {
                            boolean ishas =false ;
                            for (Label label1 : labelslast) {
                                if (label1.getLabelID().equals(label.getLabelID())){
                                    ishas =true ;
                                }
                            }
                            if(!ishas){
                                lableAddeed.add(label);
                            }
                        }

//                Log.i("szhua",labelslast.toString()) ;
//                Log.i("szhua",labels.toString());
                        for (Label label : labelslast) {
                            boolean ishas =false ;
                            for (Label label1 : labels) {
                                if (label1.getLabelID().equals(label.getLabelID())){
                                    ishas =true ;
                                }
                            }
                            if(!ishas){
                                lableLess.add(label);
                            }
                        }


                        if(lableAddeed!=null&&laAdded.size()>0){
                            dbManager.addLabel(lableAddeed);
                        }
                        if(lableLess!=null&&lableLess.size()>0){
                            dbManager.deleteLabels(lableLess);
                        }


                        labels = (ArrayList<Label>) dbManager.queryLabels();
                        /**
                         * 删除整个标签的时候；
                         */
                        lgAdded =new ArrayList<LabelGroup>() ;
                        lgLess =new ArrayList<LabelGroup>() ;

                        if(lableLess.size()>0){
                            for (Label label : lableLess) {
                                ArrayList<RConact> rc = new ArrayList<RConact>();
                                LabelGroup la = new LabelGroup(label, rc);
                                for (LabelConact labelConact : laLess){
                                    if (labelConact.getLabel().getLabelID().equals(label.getLabelID())) {
                                        rc.add(labelConact.getRconact());
                                    }
                                }
                                if(rc.size()>0) {
                                    lgLess.add(la);
                                }
                            }
                        }

                        if(laAdded.size()>0){
                            for (Label label : labels) {
                                ArrayList<RConact> rc =new ArrayList<RConact>() ;
                                LabelGroup la =new LabelGroup(label,rc) ;
                                for (LabelConact labelConact : laAdded) {
                                    if (labelConact.getLabel().getLabelID().equals(label.getLabelID())){
                                        rc.add(labelConact.getRconact());
                                    }
                                }
                                if(rc.size()>0) {
                                    lgAdded.add(la);
                                }
                            }
                        }
                        if(laLess.size()>0) {
                            for (Label label : labels) {
                                ArrayList<RConact> rc = new ArrayList<RConact>();
                                LabelGroup la = new LabelGroup(label, rc);
                                for (LabelConact labelConact : laLess) {
                                    if (labelConact.getLabel().getLabelID().equals(label.getLabelID())) {
                                        rc.add(labelConact.getRconact());
                                    }
                                }
                                if(rc.size()>0) {
                                    lgLess.add(la);
                                }
                            }
                        }

                        if(lgAdded.size()>0){
                            Log.i("szhua","added"+laAdded.toString()) ;
                            for (LabelGroup labelGroup : lgAdded) {
                                setFansTagDao.setFansTag("shuweineng888",AppHolder.getInstance().getToken(),labelGroup);
                            }
                        }
                        if(lgLess.size()>0){
                            Log.i("szhua","less"+lgLess.toString());
                            for (LabelGroup lgLes : lgLess) {
                                removeFansTagDao.removeFansTags("shuweineng888",AppHolder.getInstance().getToken(),lgLes);
                            }
                        }
                        if(added!=null&&added.size()>0) {
                            appendFansDao.apppendFans("shuweineng888", AppHolder.getInstance().getToken(), added);
                            dbManager.addRconact(added);
                        }

                        if(less!=null&&less.size()>0) {
                            removeFansDao.removeFans("shuweineng888", AppHolder.getInstance().getToken(), less);
                            dbManager.deleteConnacts(less);
                        }
                        return null;
                    }
                })
                .flatMap(new Func1<Object, Observable<List<WMessage>>>() {
                    @Override
                    public Observable<List<WMessage>> call(Object o) {
                        return getMessages(users, "" + SharedPrefsUtil.getValue(getBaseContext(),"lastCreateTime",0L));
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<WMessage>>() {
                    @Override
                    public void onCompleted() {;
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("szhua","erro"+e.toString()) ;

                    }
                    @Override
                    public void onNext(List<WMessage> wMessages) {

                        if(dbManager!=null){
                            dbManager.closeDB();
                        }
                        /**
                         * 对信息进行分类，并且进行上传 ;
                         */
                        if (wMessages == null || wMessages.size() == 0) {
                            Log.i("leilei", "未有新的信息");
                        } else {
                            ArrayList<WMessage> wmsgsText =new ArrayList<WMessage>() ;
                            wmsgsFileTimer =new ArrayList<WMessage>();
                            for (WMessage wMessage :  wMessages) {
                                if (wMessage.getImgPath()!= null) {
                                    /**
                                     * 是音频的情况下；
                                     */
                                    if(wMessage.isVoice){
                                        //音频的路径 ；
                                        String voicePath =Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + users.getUserId()+wMessage.getImgPath();
                                        File file = new File(voicePath);
                                        if(file.exists()){
                                            wMessage.setFile(file);
                                            wMessage.setSendType(1);
                                            wmsgsFileTimer.add(wMessage);
                                        }
                                    }else{
                                        //tupian的路径; 图片的上传
                                        String orign= wMessage.getImgPath() ;
                                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + users.getUserId() + "/image2/" + orign + ".jpg";
                                        File file = new File(path);
                                        if(file.exists()){
                                            wMessage.setFile(file);
                                            wMessage.setSendType(2);
                                            wmsgsFileTimer.add(wMessage);
                                        }
                                    }
                                } else {
                                    //纯文本的shangchuan;
                                    wMessage.setSendType(0);
                                    wmsgsText.add(wMessage) ;
                                    //简单的输出一下;
                                    for (WMessage message : wmsgsText) {
                                        Log.i("leilei",message.getContent()) ;
                                    }
                                }
                            }

                            if(wmsgsFileTimer!=null&&wmsgsFileTimer.size()>0){
                                if(wmsgsFileTimer.get(0)!=null){
                                    wMessageTimer =wmsgsFileTimer.get(0) ;
                                    wmsgsFileTimer.remove(wMessageTimer);
                                    uploadChatFileDaoTimer.uploadChatFile("shuweineng888",AppHolder.getInstance().getToken(),wMessageTimer.getFile(),wMessageTimer.getMsgId());
                                }
                            }

                            if(wmsgsText!=null&&wmsgsText.size()>0){
                                uploadChatLogsDao.uploadChatLogs("shuweineng888",AppHolder.getInstance().getToken(),wmsgsText,"","");
                            }
                            /**
                             * 上传文件；
                             */
                            long lastCreateTime = Long.parseLong(wMessages.get(wMessages.size() - 1).getCreateTime());
                            SharedPrefsUtil.putValue(getBaseContext(),"lastCreateTime",lastCreateTime);

                        }
                    }
                }) ;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onRequestSuccess(int requestCode) {
        if(requestCode== RequestCode.UPLOADFILE1){
            FileReturnEntity fileRe =uploadChatFileDaoFirst.getFileReturnEntity() ;
            ArrayList<WMessage> ws = new ArrayList<>();
            wMessage.setContent(fileRe.getFilepath());
            ws.add(wMessage);
            uploadChatLogsDao.uploadChatLogs("shuweineng888", AppHolder.getInstance().getToken(), ws, fileRe.getFilesize(), fileRe.getServer());
            if(wmsgsFile.size()>0){
                handlerUploadFile.sendEmptyMessage(0);
            }
        }
        if(requestCode== RequestCode.UPLOADFILE){
            FileReturnEntity fileRe =uploadChatFileDaoTimer.getFileReturnEntity() ;
            ArrayList<WMessage> ws = new ArrayList<>();
            wMessageTimer.setContent(fileRe.getFilepath());
            ws.add(wMessageTimer);
            uploadChatLogsDao.uploadChatLogs("shuweineng888", AppHolder.getInstance().getToken(), ws, fileRe.getFilesize(), fileRe.getServer());
            if(wmsgsFileTimer.size()>0){
                handlerUploadFileTimer.sendEmptyMessage(0);
            }
        }
    }
    @Override
    public void onRequestError(int requestCode, String errorInfo, int error_code) {

    }
    @Override
    public void onRequestFaild(String errorNo, String errorMessage) {

    }
    @Override
    public void onNoConnect() {
        /**
         * 没有网络连接 ；
         */
    }
}
