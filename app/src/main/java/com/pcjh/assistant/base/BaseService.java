package com.pcjh.assistant.base;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.mengma.asynchttp.Http;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.SharedPrefsUtil;

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
 * Created by szhua on 2016/11/9.
 */
public class BaseService extends Service {
    protected Subscription subscription ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }
    //==================================================================================================

    /**
     * 初始化数据库 ；进行解密 ；
     * @param users
     * @return 数据库
     */
    public SQLiteDatabase initPSWdb(Users users) {
        SQLiteDatabase.loadLibs(getBaseContext());
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

}
