package com.pcjh.assistant.base;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mengma.asynchttp.Http;
import com.mengma.asynchttp.dialog.ProgressHUD;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.ImgFlag;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.liabrary.utils.UiUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created Szhua 2016/10/28
 */
public class BaseActivity extends AppCompatActivity implements INetResult {
    ProgressHUD mProgressHUD;
    private ImageView searchBt ;
    private ImageView collectBt ;
    private ImageView backBt  ;
    private ImageView doneBt ;

    protected Subscription subscription ;

    @Override
    protected void onStart() {
        super.onStart();

        searchBt = (ImageView) findViewById(R.id.search_bt);
        collectBt = (ImageView) findViewById(R.id.collect_btt);
        backBt = (ImageView) findViewById(R.id.back_bt);
        doneBt = (ImageView) findViewById(R.id.done_bt);
        if(backBt!=null){
            backBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setSearchBtListener(View.OnClickListener listener){
        if(searchBt!=null){
            searchBt.setOnClickListener(listener);
        }
    }
    public void setCollectBtListener(View.OnClickListener listener){
        if(collectBt!=null){
            collectBt.setOnClickListener(listener);
        }
    }
    public void setBackBtListener(View.OnClickListener listener){
        if(backBt!=null){
            backBt.setOnClickListener(listener);
        }
    }

    public void setDoneBtListener(View.OnClickListener listener){
        if(doneBt!=null){
            doneBt.setOnClickListener(listener);
        }
    }



    @Override
    public void onRequestSuccess(int requestCode) {
        showProgress(false);
    }

    @Override
    public void onRequestError(int requestCode, String errorInfo, int erro_code) {
        showProgress(false);
    }


    @Override
    public void onRequestFaild(String errorNo, String errorMessage) {
        UiUtil.showLongToast(this, errorMessage);
        showProgress(false);
    }

    @Override
    public void onNoConnect() {
        showProgress(false);
        UiUtil.showLongToast(this, "无网络连接");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Http.getInstance().cancelRequests(this);
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
    /**
     * 显示加载进度条
     *
     * @param show
     */
    public void showProgress(boolean show) {
        showProgressWithText(show, "加载中...");
    }

    /**
     * 显示加载进度条
     *
     * @param show
     * @param message
     */
    public void showProgressWithText(boolean show, String message) {
        if (show) {
            mProgressHUD = ProgressHUD.show(this, message, true, true, null);
        } else {
            if (mProgressHUD != null) {
                mProgressHUD.dismiss();
            }
        }
    }



    //==================================================================================================

    /**
     * 初始化数据库 ；进行解密 ；
     *
     * @param users
     * @return 数据库
     */
    public SQLiteDatabase initPSWdb(Users users) {
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            public void preKey(SQLiteDatabase database) {
            }

            public void postKey(SQLiteDatabase database) {
                //执行这样的sql语句进行对数据库的解密；
                database.rawExecSQL("PRAGMA cipher_migrate;");
            }
        };
        SQLiteDatabase db = SQLiteDatabase.openDatabase(users.getDbPath(), users.getPassword(), null, SQLiteDatabase.OPEN_READWRITE, hook);
        return db;
    }

    public List<WMessage> _getWMessage(Users users , String createTimeP){
        SQLiteDatabase db =initPSWdb(users) ;
        /**
         * where type = 1 设置type=1是为了过滤掉一些微信好的信息 ；
         */
        Cursor c =db.rawQuery("select * from message INNER JOIN  rcontact on message.talker = rcontact.username  where  message.type in ( 1,3 ,34 ) and message.createTime >"+createTimeP ,null) ;
        ArrayList<WMessage> wMessages =new ArrayList<WMessage>();
        while (c.moveToNext()) {
            String content = c.getString(c.getColumnIndex("content"));
            String talker = c.getString(c.getColumnIndex("talker"));
            String createTime = c.getString(c.getColumnIndex("createTime"));
            String msgId = c.getString(c.getColumnIndex("msgId"));
            String type = c.getString(c.getColumnIndex("type"));
            String isSend = c.getString(c.getColumnIndex("isSend"));
            String imgPaht =c.getString(c.getColumnIndex("imgPath")) ;
            String dispalayname =c.getString(c.getColumnIndex("alias"));
            WMessage wmessage = new WMessage();
            wmessage.setContent(content);
            wmessage.setTalker(talker);
            wmessage.setMsgId(msgId);
            wmessage.setIsSend(isSend);
            wmessage.setType(type);
            wmessage.setCreateTime(createTime);
            if(!TextUtils.isEmpty(dispalayname)){
                wmessage.setDisplayName(dispalayname);
            }else {
                wmessage.setDisplayName(talker);
            }
            if(!TextUtils.isEmpty(imgPaht)){
                wmessage.setImgPath(imgPaht);
            }
            wMessages.add(wmessage) ;
        }
        c.close();
        db.close();
        return  wMessages ;
    }


    public Observable<List<WMessage>> getMessages(Users users, final String id){

        Observable<List<WMessage>> WMessgaObser= Observable.just(users).map(new Func1<Users, List<WMessage>>() {
            @Override
            public List<WMessage> call(Users users) {
                return _getWMessage(users,id);
            }
        }) ;
        return  WMessgaObser ;
    }
    /**
     * 获得信息；All
     */
    public List<WMessage> _getWMessage(Users users ){
        SQLiteDatabase db =initPSWdb(users) ;
        /**
         * where type = 1 设置type=1是为了过滤掉一些微信好的信息 ；
         */
        Cursor c =db.rawQuery("select * from message left JOIN  rcontact on message.talker = rcontact.username where  message.type in ( 1,3 ,34) ",null) ;
        ArrayList<WMessage> wMessages =new ArrayList<WMessage>();
        while (c.moveToNext()) {
            String content = c.getString(c.getColumnIndex("content"));
            String talker = c.getString(c.getColumnIndex("talker"));
            String createTime = c.getString(c.getColumnIndex("createTime"));
            String msgId = c.getString(c.getColumnIndex("msgId"));
            String type = c.getString(c.getColumnIndex("type"));
            String isSend = c.getString(c.getColumnIndex("isSend"));
            String imgPaht =c.getString(c.getColumnIndex("imgPath")) ;
            String dispalayname =c.getString(c.getColumnIndex("alias"));
            WMessage wmessage = new WMessage();
            wmessage.setContent(content);
            wmessage.setTalker(talker);
            wmessage.setMsgId(msgId);
            wmessage.setIsSend(isSend);
            wmessage.setType(type);
            wmessage.setCreateTime(createTime);
            if(!TextUtils.isEmpty(dispalayname)){
                wmessage.setDisplayName(dispalayname);
            }else {
                wmessage.setDisplayName(talker);
            }
            if(!TextUtils.isEmpty(imgPaht)){
                wmessage.setImgPath(imgPaht);
            }
            wMessages.add(wmessage) ;
        }
        c.close();
        db.close();
        return  wMessages ;
    }

    public Observable<List<WMessage>> getMessages(Users users){
        Observable<List<WMessage>> WMessgaObser= Observable.just(users).map(new Func1<Users, List<WMessage>>() {
            @Override
            public List<WMessage> call(Users users) {
                return _getWMessage(users);
            }
        }) ;
        return  WMessgaObser ;
    }
    /**
     * 获得contactLabels ；
     * @param users
     * @return contactLabels
     */
    public List<Label> _getLabels(Users users) {
        SQLiteDatabase db = initPSWdb(users);
        Cursor c = db.query("ContactLabel", null, null, null, null, null, null);
        ArrayList<Label> labels = new ArrayList<Label>();
        while (c.moveToNext()) {
            String labelID = c.getString(c.getColumnIndex("labelID"));
            String labelName = c.getString(c.getColumnIndex("labelName"));
            String createTime = c.getString(c.getColumnIndex("createTime"));
            Label label = new Label();
            label.setCreateTime(createTime);
            label.setLabelID(labelID);
            label.setLabelName(labelName);
            labels.add(label);
        }
        c.close();
        db.close();
        return labels;
    }

    public Observable<List<Label>> getConnactLabelIds(Users users) {
        Observable<List<Label>> labelObser = null;
        labelObser = Observable.just(users)
                .map(new Func1<Users, List<Label>>() {
                    @Override
                    public List<Label> call(Users users) {
                        return _getLabels(users);
                    }
                });
        return labelObser;
    }


    public  List<RConact> _GetContact(String where,Users users){

        SQLiteDatabase db = initPSWdb(users);
        Cursor c =db.rawQuery(where,null);
        //  Cursor c = db.query("rcontact", null, null, null, null, null, null);
        ArrayList<RConact> RConacts = new ArrayList<RConact>();
        while (c.moveToNext()) {
            String talker = c.getString(c.getColumnIndex("username"));
            String nickname = c.getString(c.getColumnIndex("nickname"));
            String alias = c.getString(c.getColumnIndex("alias"));
            String type = c.getString(c.getColumnIndex("type"));
            String contactLabelIds = c.getString(c.getColumnIndex("contactLabelIds"));
            RConact RConact = new RConact();
            RConact.setTalker(talker);
            RConact.setUsername(talker);
            RConact.setNickname(nickname);
            if(!TextUtils.isEmpty(alias)){
            RConact.setAlias(alias);}else{
                RConact.setAlias(talker);
            }
            RConact.setType(type);
            if (!TextUtils.isEmpty(contactLabelIds))
                RConact.setContactLabelIds(contactLabelIds);
            RConacts.add(RConact);
        }
        c.close();
        db.close();
        return RConacts;

    }



  public Observable<List<RConact>> getRconactsForLabel(Users users){

      Observable<List<RConact>> rconactObser = null;
      rconactObser = Observable.just(users)
              .map(new Func1<Users, List<RConact>>() {
                  @Override
                  public List<RConact> call(Users users) {
                      return _GetContact("select * from rcontact where  contactLabelIds  is not  ''  ;",users);
                  }
              });
      return rconactObser;
  }



    public Observable<List<RConact>> getRconacts(Users users) {
        Observable<List<RConact>> rconactObser = null;
        rconactObser = Observable.just(users)
                .map(new Func1<Users, List<RConact>>() {
                    @Override
                    public List<RConact> call(Users users) {
                        return _GetContact("select * from rcontact where  type not in (0,2,4,33)",users);
                    }
                });
        return rconactObser;
    }




    public Users  getUserFromDatabase(String uin){
        Log.i("leilei","thread"+Thread.currentThread());
        ArrayList<Users > users = null ;
        DbManager dbManager = new DbManager(BaseActivity.this);
        users = (ArrayList<Users>) dbManager.query();
        if (users != null && users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUin().equals(uin)) {
                    Users user = users.get(i);
                    return user;
                }
            }
        }
        return  null ;
    }

    public void getMessageFromWxinFirst(){
        getMessages(AppHolder.getInstance().getUsers()).subscribe(new Subscriber<List<WMessage>>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(List<WMessage> wMessages) {
                Log.i("leilei",Thread.currentThread().getName());
                long  lastCreateTime = Long.parseLong(wMessages.get(wMessages.size() - 1).getCreateTime());
                SharedPrefsUtil.putValue(BaseActivity.this,"lastCreateTime",lastCreateTime);
                if (wMessages != null && !TextUtils.isEmpty(wMessages.get(wMessages.size() - 1).getImgPath())) {
                    Log.i("leilei", "messageSzie" + wMessages.get(wMessages.size() - 1).getImgPath());
                    String orign = wMessages.get(wMessages.size() - 1).getImgPath();
                    String imgpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/" + AppHolder.getInstance().getUsers().getUserId() + "/image2/" + orign + ".jpg";
                    File file =new File(imgpath) ;
                    Log.i("leilei","existed"+file.exists());
                    Log.i("leilei", "imgpath" + imgpath);
                    // /storage/emulated/0/tencent/MicroMsg/a155cad2529e20357ada9b6b7e558c69/image2/d1/27/th_d1a127c840a997c4c36a678aaa8eec11.jpg
                    // Picasso.with(TimerActivity.this).load(file).placeholder(R.drawable.szhua).into(iv);
                }
            }
        });
    }





}
