package com.pcjh.assistant.activity;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ArrayRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.dao.AppendFansDao;
import com.pcjh.assistant.dao.InitDao;
import com.pcjh.assistant.dao.RemoveFansDao;
import com.pcjh.assistant.dao.RemoveFansTagDao;
import com.pcjh.assistant.dao.SetFansTagDao;
import com.pcjh.assistant.dao.UploadChatLogsDao;
import com.pcjh.assistant.db.DatabaseHelper;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.ImgFlag;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.LabelGroup;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.schedulers.Schedulers;

public class TimerActivity extends BaseActivity implements INetResult {


    /**
     * 初步实现对信息的截取;
     */
    private ArrayList<WMessage> messgaeList = new ArrayList<WMessage>();
    //用于解密数据库的密码;
    private ArrayList<RConact> RConacts = new ArrayList<RConact>();

    private Subscription subscriptions;

    private AppendFansDao appendFansDao =new AppendFansDao(this,this) ;
    private UploadChatLogsDao uploadChatLogsDao =new UploadChatLogsDao(this,this) ;
    private SetFansTagDao setFansTagDao =new SetFansTagDao(this,this) ;
    private RemoveFansDao removeFansDao =new RemoveFansDao(this,this) ;
    private RemoveFansTagDao removeFansTagDao =new RemoveFansTagDao(this,this) ;



    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(subscriptions!=null){
                subscriptions.unsubscribe();
            }
            getMessageTimer();
        }
    };
    private Users  users  ;
    private UserInfo  userInfo;
    private ArrayList<WMessage>  data2 =new ArrayList<WMessage>();

    private ArrayList<LabelGroup> labelGroups =new ArrayList<LabelGroup>();
    private ArrayList<Label> labels =new ArrayList<Label>() ;
    private ArrayList<RConact> labelRconacts =new ArrayList<RConact>() ;
    private ArrayList<RConact>  less;
    private ArrayList<RConact>  added ;
    private ArrayList<RConact>  currentData;
    private DbManager  dbManager;
    private ArrayList<LabelGroup>  labelGroupAdded;
    private ArrayList<LabelGroup>  labelGroupDeleted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        users =AppHolder.getInstance().getUsers();
        userInfo =AppHolder.getInstance().getUser() ;
        /**
         * 用户第一次进入这个界面的时候把所有的数据进行上传；
         */
        if(SharedPrefsUtil.getValue(this,"isFirstGetData",true)) {
          uploadMessageToServerFirst();
        }else{
          Timer timer =new Timer() ;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.i("szhua","start") ;
                   handler.sendEmptyMessage(0) ;
                }
            },0,10000);
        }
    }
    public void uploadMessageToServerFirst(){
    subscription =  Observable
              .zip(getConnactLabelIds(users), getRconacts(users), getMessages(users),getRconactsForLabel(users), new Func4<List<Label>, List<RConact>, List<WMessage>,List<RConact> ,Object>() {
            @Override
            public Object call(List<Label> labels, List<RConact> conacts, List<WMessage> wMessages ,List<RConact> rConact) {
                /**
                 * 上传联系人
                 */
                messgaeList = (ArrayList<WMessage>) wMessages;

                RConacts = (ArrayList<RConact>) conacts;
                ArrayList<RConact> data =new ArrayList<RConact>();
                for (int i = 0; i < RConacts.size(); i++) {
                    RConact rc =RConacts.get(i);
                    if(rc.getTalker().equals("weixin") ||  rc.getTalker().substring(0,3).equals("gh_")||rc.getTalker().equals("filehelper")||Integer.parseInt(rc.getType())%2==0||AppHolder.getInstance().getUser().getWxId().equals(rc.getTalker())){
                      data.add(rc);
                    }
                }
                RConacts.removeAll(data);

                DbManager dbManager =new DbManager(TimerActivity.this) ;
                dbManager.addRconact(RConacts);



                /**
                 * 上传标签分组;
                 */
                TimerActivity.this.labels = (ArrayList<Label>) labels;
                labelRconacts = (ArrayList<RConact>) rConact;

                dbManager.addLabel(TimerActivity.this.labels);
                /**
                 * 上传聊天记录;
                 */
                ArrayList<RConact> data1 = (ArrayList<RConact>) RConacts;
                data2 =new ArrayList<WMessage>() ;
             //   Log.i("szhua","size"+messgaeList.size());
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

                for (Label label : labels) {
                    ArrayList<RConact> rc =new ArrayList<RConact>() ;
                    LabelGroup lg =new LabelGroup(label,rc) ;
                    for (RConact labelRconact : labelRconacts) {
                        if(labelRconact.getContactLabelIds().contains(label.getLabelID())){
                             rc.add(labelRconact) ;
                        }
                    }
                    labelGroups.add(lg) ;
                }
                for (LabelGroup labelGroup : labelGroups) {
                    setFansTagDao.setFansTag("shuweineng888",AppHolder.getInstance().getToken(),labelGroup);
                }
            appendFansDao.apppendFans("shuweineng888",AppHolder.getInstance().getToken(),RConacts);
                Log.i("szhua","data2"+data2.size());
                uploadChatLogsDao.uploadChatLogs("shuweineng888",AppHolder.getInstance().getToken(),data2);

                if(data2!=null&&data2.size()>0)
                SharedPrefsUtil.putValue(TimerActivity.this,"lastCreateTime",Long.parseLong(data2.get(data2.size()-1).getCreateTime()));
                SharedPrefsUtil.putValue(TimerActivity.this,"isFirstGetData",false);
                Timer timer =new Timer() ;
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i("szhua","start") ;
                        handler.sendEmptyMessage(0) ;
                    }
                },0,10000);
            }
        }) ;
    }


    public void  getMessageTimer(){
      subscriptions =    Observable
                .zip(getConnactLabelIds(users), getRconacts(users),getRconactsForLabel(users), new Func3<List<Label>, List<RConact>,List<RConact>, Object>() {
            @Override
            public Object call(List<Label> labelss, List<RConact> conacts,List<RConact> labelRc) {
                currentData = (ArrayList<RConact>) conacts;
                ArrayList<RConact> data =new ArrayList<RConact>();
                for (int i = 0; i < currentData.size(); i++) {
                    RConact rc =currentData.get(i);
                    if(rc.getTalker().equals("weixin") ||  rc.getTalker().substring(0,3).equals("gh_")||rc.getTalker().equals("filehelper")||Integer.parseInt(rc.getType())%2==0||AppHolder.getInstance().getUser().getWxId().equals(rc.getTalker())){
                        data.add(rc);
                    }
                }
                currentData.removeAll(data);
                dbManager =new DbManager(TimerActivity.this) ;
                ArrayList<RConact> Dbdata = (ArrayList<RConact>) dbManager.quryForRconacts();
                less =new ArrayList<RConact>();
                added =new ArrayList<RConact>()  ;



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
                 * 原来的标签分组 ；
                  */
                ArrayList<Label> lastLabels =new ArrayList<Label>() ;
                lastLabels= (ArrayList<Label>) dbManager.queryLabels();

                labels= (ArrayList<Label>) labelss;
                labelGroupAdded =new ArrayList<LabelGroup>() ;
                 labelGroupDeleted =new ArrayList<LabelGroup>() ;
                ArrayList<LabelGroup> lableGroupLast =new ArrayList<LabelGroup>() ;
                for (Label lastLabel : lastLabels) {
                    ArrayList<RConact> rc =new ArrayList<RConact>() ;
                    LabelGroup lg =new LabelGroup(lastLabel,rc) ;
                    for (RConact rConact : Dbdata) {
                        if(!TextUtils.isEmpty(rConact.getContactLabelIds())){
                        if(rConact.getContactLabelIds().contains(lastLabel.getLabelID())){
                            rc.add(rConact) ;
                        }
                        }
                    }
                    lableGroupLast.add(lg);
                }

                /**
                 * 现在的标签分组 ;
                 */
                labelRconacts = (ArrayList<RConact>) labelRc;
                labelGroups.clear();
                for (Label label : labels) {
                    ArrayList<RConact> rc =new ArrayList<RConact>() ;
                    LabelGroup lg =new LabelGroup(label,rc) ;
                    for (RConact labelRconact : labelRconacts) {
                        if(labelRconact.getContactLabelIds().contains(label.getLabelID())){
                            rc.add(labelRconact) ;
                        }
                    }
                    labelGroups.add(lg) ;
                }

                for (LabelGroup labelGroup : lableGroupLast) {
                     boolean ishas =false ;
                    for (LabelGroup group : labelGroups) {
                        if (group.getLabel().getLabelID().equals(labelGroup.getLabel().getLabelID())){
                            ishas =true  ;
                        }
                    }
                    if(!ishas){
                        labelGroupDeleted.add(labelGroup) ;
                    }
                }

                for (LabelGroup labelGroup : labelGroups) {
                    boolean ishas =false ;
                    for (LabelGroup group : lableGroupLast) {
                        if (group.getLabel().getLabelID().equals(labelGroup.getLabel().getLabelID())){
                            ishas =true  ;
                        }
                    }
                    if(!ishas){
                        labelGroupAdded.add(labelGroup) ;
                    }
                }

                return null;
            }
        })
              .flatMap(new Func1<Object, Observable<List<WMessage>>>() {
            @Override
            public Observable<List<WMessage>> call(Object o) {
                return getMessages(AppHolder.getInstance().getUsers(), "" + SharedPrefsUtil.getValue(TimerActivity.this,"lastCreateTime",0L));
            }
        })
                .subscribeOn(Schedulers.io())
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

                if(labelGroupAdded!=null&&labelGroupAdded.size()>0){
                    for (LabelGroup labelGroup : labelGroupAdded) {
                        setFansTagDao.setFansTag("shuweineng888",AppHolder.getInstance().getToken(),labelGroup);
                    }
                    ArrayList<Label> labels =new ArrayList<Label>() ;
                    for (LabelGroup labelGroup : labelGroupAdded) {
                        labels.add(labelGroup.getLabel()) ;
                    }
                    dbManager.addLabel(labels);
                }
                if(labelGroupDeleted!=null&&labelGroupDeleted.size()>0){
                    for (LabelGroup labelGroup : labelGroupDeleted) {
                        removeFansTagDao.removeFansTags("shuweineng888",AppHolder.getInstance().getToken(),labelGroup);
                    }
                    ArrayList<Label> labels =new ArrayList<Label>() ;
                    for (LabelGroup labelGroup : labelGroupDeleted) {
                        labels.add(labelGroup.getLabel()) ;
                    }
                    dbManager.deleteLabels(labels);
                }

                if(added!=null&&added.size()>0) {
                   // Log.i("szhua",added.toString()) ;
                    appendFansDao.apppendFans("shuweineng888", AppHolder.getInstance().getToken(), added);
                    dbManager.addRconact(added);
                }

                if(less!=null&&less.size()>0) {
                    removeFansDao.removeFans("shuweineng888", AppHolder.getInstance().getToken(), less);
                    dbManager.deleteConnacts(less);
                }

                /**
                 * todo;relove ;
                 */
           //     Log.i("szhua",Thread.currentThread().getName());
                if (wMessages == null || wMessages.size() == 0) {
                    Log.i("leilei", "未有新的信息");
                } else {
                    ArrayList<WMessage> wmsgsText =new ArrayList<WMessage>() ;
                    ArrayList<File> wmsgsFile =new ArrayList<File>() ;
                    for (WMessage wMessage :  wMessages) {
                        if (wMessage.getImgPath()!= null) {
                            WMessage wmessage =wMessages.get(wMessages.size()-1) ;
                            /**
                             * 是音频的情况下；
                             */
                            if(wmessage.isVoice){
                                //音频的路径 ；
                                String voicePath =Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + AppHolder.getInstance().getUsers().getUserId()+wmessage.getImgPath();
                                Log.i("szhua","voicePaht"+voicePath);
                                File file = new File(voicePath);
                                if(file.exists()){
                                    wmsgsFile.add(file) ;
                                }

                            }else{
                                //tupian的路径; 图片的上传
                                Log.i("leilei", "imgpaht" + wMessages.get(wMessages.size() - 1).getImgPath());
                                String orign= wmessage.getImgPath() ;
                                String path =   Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + AppHolder.getInstance().getUsers().getUserId() + "/image2/" + orign + ".jpg";
                                Log.i("szhua","path"+path) ;
                                File file = new File(path);
                                if(file.exists()){
                                    wmsgsFile.add(file) ;
                                }
                            }
                        } else {
                            //纯文本的shangchuan;
                            wmsgsText.add(wMessage) ;
                            Log.i("leilei", "onnnext" + wMessages.get(wMessages.size() - 1).getContent());
                        }
                    }
                    uploadChatLogsDao.uploadChatLogs("shuweineng888",AppHolder.getInstance().getToken(),wmsgsText);
                    /**
                     * 上传文件 ；
                     */


                    long lastCreateTime = Long.parseLong(wMessages.get(wMessages.size() - 1).getCreateTime());
                    SharedPrefsUtil.putValue(TimerActivity.this,"lastCreateTime",lastCreateTime);
                }
            }
        }) ;
    }

    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);
    }

    @Override
    public void onRequestError(int requestCode, String errorInfo, int erro_code) {
        super.onRequestError(requestCode, errorInfo, erro_code);
    }
}
