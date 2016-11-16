package com.pcjh.assistant.base;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.mengma.asynchttp.Http;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.activity.HomeActivity;
import com.pcjh.assistant.activity.StartActivity;
import com.pcjh.assistant.dao.InitDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.ChmodUtil;
import com.pcjh.assistant.util.Md5;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.assistant.util.XmlPaser;
import com.pcjh.liabrary.utils.UiUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by szhua on 2016/11/9.
 */
public abstract class BaseService extends Service implements INetResult {
    protected Subscription subscription ;
    private InitDao  initDao;
    private DataOutputStream os;
    private DataInputStream  is;
    private java.lang.Process process;
    private String  Imei;
    private String   uin;
    private String  password;

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
            String imgPaht =c.getString(c.getColumnIndex("imgPath"));
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



    public Observable<String> getUin(){

        Observable<String> observable =Observable.just("").map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return XmlPaser.getUidFromFile();
            }
        }) ;
        return  observable ;
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


    /**
     * 重新获得用户的信息；
     */
    public void resetUsesrs(){
        Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        uin = XmlPaser.getUidFromFile();

        SharedPrefsUtil.putValue(getBaseContext(),"uin",uin);
       getUserInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.i("leilei", e.toString());
                    }
                    @Override
                    public void onNext(UserInfo userInfo) {
                        Log.i("leilei", "nickname" +userInfo.getNickName());
                        AppHolder.getInstance().setUser(userInfo);
                        initDao =new InitDao(getBaseContext(),BaseService.this) ;
                        initDao.get_token("shuweineng888");
                    }
                });
    }

    protected abstract void reGetMessage();
    @Override
    public void onRequestSuccess(int requestCode) {
        if(requestCode== RequestCode.INITSUCESS){
            String token = initDao.getToken();
            AppHolder.getInstance().setToken(token);
            Users users = AppHolder.getInstance().getUsers();
            SharedPrefsUtil.putValue(getBaseContext(), "token", token);

            /**
             * 储存一些信息;
             */
            SharedPrefsUtil.putValue(getBaseContext(), "uin", users.getUin());
            SharedPrefsUtil.putValue(getBaseContext(), "password", users.getPassword());
            SharedPrefsUtil.putValue(getBaseContext(), "dbPath", users.getDbPath());
            SharedPrefsUtil.putValue(getBaseContext(), "token", token);
            SharedPrefsUtil.putValue(getBaseContext(), "wxid", AppHolder.getInstance().getUser().getWxId());

            reGetMessage();
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

    }

    /**
     * @return UserInfo
     */
    public Observable<UserInfo> getUserInfo() {
        Observable<UserInfo> userInfoOb = null;
        if (!TextUtils.isEmpty(this.uin) && Integer.parseInt(this.uin) != 0) {
            userInfoOb = Observable.just(this.uin)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String s){
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Func1<String, ArrayList<Users>>() {
                        @Override
                        public ArrayList<Users> call(String s) {
                            DbManager dbManager = new DbManager(getBaseContext());
                            return (ArrayList<Users>) dbManager.query();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<ArrayList<Users>>() {
                        @Override
                        public void call(ArrayList<Users> userses) {
                            if (userses == null || userses.size() == 0) {
                                Log.i("szhua","数据暂无此微信号！");
                            }
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Func1<ArrayList<Users>, Users>() {
                        @Override
                        public Users call(ArrayList<Users> userses) {
                            return userses == null ? null : checkUserExisted(userses);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Users>() {
                        @Override
                        public void call(Users users) {
                            if (users != null) {
                                AppHolder.getInstance().setUsers(users);;
                                AppHolder.getInstance().getUser().setPassword(users.getPassword());
                                SharedPrefsUtil.putValue(getBaseContext(), "isFirstGetData", false);
                            } else {
                                SharedPrefsUtil.putValue(getBaseContext(), "isFirstGetData", true);
                            }
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Func1<Users, UserInfo>() {
                        @Override
                        public UserInfo call(Users users) {
                            if (users != null) {
                                return readDatabaseFromOldInfo(users);
                            } else {
                                String result = Imei + BaseService.this.uin;
                                String md5 = Md5.getMd5Value(result);
                                password = md5.substring(0, 7);
                                Log.i("szhua","psw"+password);
                                AppHolder.getInstance().getUser().setPassword(password);
                                return readWeChatDatabase();
                            }
                        }
                    });
            return userInfoOb;
        } else {
            return Observable.error(new Throwable("erro "));
        }}


    /**
     * 检验当前微信账号的数据是否存在；
     *
     * @param data
     * @return
     */
    public Users checkUserExisted(ArrayList<Users> data) {
        if (data != null && data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getUin().equals(uin)) {
                    Users users = data.get(i);
                    return users;
                }
            }
        }
        return null;
    }

    //获得root的权限；
    public void getRoot(){

        try {
            process = Runtime.getRuntime().exec("/system/xbin/su"); /*这里可能需要修改su
的源代码 （注掉  if (myuid != AID_ROOT && myuid != AID_SHELL) {*/
            os = new DataOutputStream(process.getOutputStream());
            is = new DataInputStream(process.getInputStream());
            os.writeBytes("/system/bin/ls" + " \n"); //这里可以执行具有root 权限的程序了
            os.writeBytes(" exit \n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            Log.e("szhua", "Unexpected error - Here is what I know:" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }// get the root privileges
    }


    //已知密码和文件路径的情况下；
    public UserInfo readDatabaseFromOldInfo(Users users) {
        SQLiteDatabase.loadLibs(this);
        File testFile = new File("/data/data/com.tencent.mm/MicroMsg/");
        UserInfo userinfo = null;
        if (!testFile.canRead()) {
            ChmodUtil.setFileCanRead(testFile);
        }
        if (users != null) {
            File dbFile = new File(users.getDbPath());
            if (!dbFile.canRead()) {
                ChmodUtil.setFileCanRead(dbFile);
            }
            userinfo = getDataWithSqlcipher(dbFile, users.getPassword(), true);
        }
        return userinfo;
    }


    public UserInfo getDataWithSqlcipher(File dbFile, String pass, boolean isFromOld) {
        //获得最终的db文件并进行读取其中的数据；
        UserInfo userInfo = null;
        if (dbFile != null && dbFile.getName().contains("db")) {
            SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
                public void preKey(SQLiteDatabase database) {
                }
                public void postKey(SQLiteDatabase database) {
                    //执行这样的sql语句进行对数据库的解密；
                    database.rawExecSQL("PRAGMA cipher_migrate;");
                }
            };
            try {
                //以这样的方式去读取数据库中的文件，确保文件的完整性：
                //@WeChat ====》微信客户端户对本地的数据库进行判断，发现文件被破坏的话就会执行重新登录操作，并且会对文件中的数据进行清除：
                //这样的体验对用户来说肯定是不行的。 故放弃官方的打开方式 使用下面的方法。
                SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), pass, null, SQLiteDatabase.OPEN_READWRITE, hook);
                Cursor c = db.query("userinfo", null, null, null, null, null, null);
                UserInfo userinfo = new UserInfo();
                while (c.moveToNext()) {
                    String id = c.getString(c.getColumnIndex("id"));
                    String value = c.getString(c.getColumnIndex("value"));
                    if (id.equals("12325")) {
                        userinfo.setProvince(value);
                    } else if (id.equals("12326")) {
                        userinfo.setCity(value);
                    } else if (id.equals("4")) {
                        userinfo.setNickName(value);
                    } else if (id.equals("12293")) {
                        userinfo.setProvinceCn(value);
                    } else if (id.equals("12292")) {
                        userinfo.setCityCn(value);
                    } else if (id.equals("2")) {
                        userinfo.setWxId(value);
                    } else if (id.equals("6")) {
                        userinfo.setPhone(value);
                    } else if (id.equals("42")) {
                        userinfo.setWxNumber(value);
                    }
                }
                c.close();
                db.close();
                SQLiteDatabase db1 = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), pass, null, SQLiteDatabase.OPEN_READWRITE, hook);
                Cursor c1 = db1.query("userinfo2", null, null, null, null, null, null);
                while (c1.moveToNext()) {
                    String id = c1.getString(c.getColumnIndex("id"));
                    String value = c1.getString(c.getColumnIndex("value"));
                    if (id.equals("USERINFO_SELFINFO_SMALLIMGURL_STRING")) {
                        userinfo.setHeaderIcon(value);
                    }
                }
                c1.close();
                db1.close();
                if (!isFromOld) {
                    Users users = new Users();
                    users.setDbPath(dbFile.getAbsolutePath());
                    users.setPassword(pass);
                    users.setUin(uin);
                    DbManager dbManager = new DbManager(this);
                    ArrayList<Users> data = new ArrayList<>();
                    data.add(users);
                    dbManager.add(data);
                    AppHolder.getInstance().setUsers(users);
                }
                return userinfo;
            } catch (Exception e) {
                Log.i("szhua", "erro when sql db");
                return null;
            }
        }
        return null;
    }



    //读取微信中的信息 ；
    public UserInfo readWeChatDatabase() {
        getRoot();
        //此处用于读取 当前微信号的uin
        SQLiteDatabase.loadLibs(this);
        File testFile = new File("/data/data/com.tencent.mm/MicroMsg/");
        if (!testFile.canRead()) {
            ChmodUtil.setFileCanRead(testFile);
        }
        ArrayList<File> dbDatas = new ArrayList<File>();
        if (testFile.isDirectory()) {
            for (int i = 0; i < testFile.listFiles().length; i++) {
                //获得MicroMsg文件下的cf792f218920a5d21aa5c121bcbe7f65文件 ；
                File childFile = testFile.listFiles()[i];
                //获得指定的文件夹下的目录；
                if (childFile.getName().length() == "cf792f218920a5d21aa5c121bcbe7f65".length()) {
                    ChmodUtil.setFileCanRead(childFile);
                    if (childFile.isDirectory() && childFile.exists() && childFile.canRead()) {
                        for (int j = 0; j < childFile.listFiles().length; j++) {
                            //获得下一层的数据 ；
                            File child = childFile.listFiles()[j];
                            if (child.getName().equals("EnMicroMsg.db")) {
                                ChmodUtil.setFileCanRead(child);
                                dbDatas.add(child);
                            }
                        }
                    }
                }

            }
        }
        UserInfo userInfo = null;
        if (dbDatas.size() == 0) {
            return null;
        } else {
            for (int i = 0; i < dbDatas.size(); i++) {
                userInfo = getDataWithSqlcipher(dbDatas.get(i), password, false);
                if (userInfo != null) {
                    //使用break调出这个循环；
                    break;
                }
            }
        }
        return userInfo;
    }

}
