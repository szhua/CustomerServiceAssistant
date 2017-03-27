package com.pcjh.assistant.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.WorkService;
import com.pcjh.assistant.dao.InitDao;
import com.pcjh.assistant.db.CustomHook;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.ChmodUtil;
import com.pcjh.assistant.util.InitializeWx;
import com.pcjh.assistant.util.Md5;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.assistant.util.XmlPaser;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by szhua on 2016/11/9.
 *
 */
public  class BaseService extends Service implements INetResult {

    private InitDao  initDao =new InitDao(this,this);
    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    //==================================================================================================从微信数据库中拿信息 ；

    /* 初始化数据库 ；进行解密 ；*/
    public SQLiteDatabase initPSWdb(Users users) throws  Exception  {
        SQLiteDatabaseHook hook = new CustomHook();
        File file =new File(users.getDbPath());
        if(file.exists()){
            Log.i("szhua","testPass:"+users.getPassword());
            return  SQLiteDatabase.openDatabase(file.getAbsolutePath(), users.getPassword(), null, SQLiteDatabase.OPEN_READWRITE, hook);
        }
        return  SQLiteDatabase.openDatabase(users.getDbPath(),users.getPassword(), null, SQLiteDatabase.OPEN_READWRITE, hook);
    }

    /* 从微信中获取信息（指定条目的数据*/
    public List<WMessage> queryMessage( Users users ,String msgIdd ,int limitNum) throws  Exception {
        SQLiteDatabase db =initPSWdb(users) ;
        ArrayList<WMessage> wMessages =new ArrayList<>();
        Cursor c =db.rawQuery("select content,talker,createTime,msgId,isSend,imgPath from message where msgId  > "
                +msgIdd +" limit " +limitNum, null);
        while (c.moveToNext()) {
            String content = c.getString(c.getColumnIndex("content"));
            String talker = c.getString(c.getColumnIndex("talker"));
            String createTime = c.getString(c.getColumnIndex("createTime"));
            String msgId = c.getString(c.getColumnIndex("msgId"));
            String isSend = c.getString(c.getColumnIndex("isSend"));
            String imgPaht =c.getString(c.getColumnIndex("imgPath")) ;
            WMessage wmessage = new WMessage();
            wmessage.setContent(content);
            wmessage.setTalker(talker);
            wmessage.setMsgId(msgId);
            wmessage.setIsSend(isSend);
            wmessage.setCreateTime(createTime);

            if(!TextUtils.isEmpty(imgPaht)){
                wmessage.setImgPath(imgPaht);
            }
             wMessages.add(wmessage) ;
        }
        c.close();
        db.close();
        return  wMessages ;
    }


    /*获得contactLabels  */
    public ArrayMap<String,Label> _getLabels(Users users) throws  Exception{
        SQLiteDatabase db =initPSWdb(users);
        Log.i("szhua",users.getPassword());
        Cursor c = db.query("ContactLabel", new String[]{"labelID","labelName","createTime"}, null, null, null, null, null);
        ArrayMap<String,Label> hashMap =new ArrayMap<>() ;
       try{
           while (c.moveToNext()) {
               String labelID = c.getString(c.getColumnIndex("labelID"));
               String labelName = c.getString(c.getColumnIndex("labelName"));
               String createTime = c.getString(c.getColumnIndex("createTime"));
               Label label = new Label();
               label.setCreateTime(createTime);
               label.setLabelID(labelID);
               label.setLabelName(labelName);
               hashMap.put(labelID,label);
           }
       }catch (Exception e){
         e.printStackTrace();
       }
        c.close();
        db.close();
        return hashMap;
    }


    /** 从微信中获得用户*/
    public ArrayMap<String,RConact> _GetContact( Users users ) throws  Exception {
        SQLiteDatabase db =initPSWdb(users) ;
        Cursor c =db.rawQuery("select  username  ,nickname , conRemark ,alias ,type ,contactLabelIds  from rcontact where type not in (4 ,33)",null);
        ArrayMap<String,RConact> RConacts =new ArrayMap<>() ;
        while (c.moveToNext()) {
            String conRemark =c.getString(c.getColumnIndex("conRemark")) ;
            String username = c.getString(c.getColumnIndex("username"));
            String nickname = c.getString(c.getColumnIndex("nickname"));
            String alias = c.getString(c.getColumnIndex("alias"));
            String type = c.getString(c.getColumnIndex("type"));
            String contactLabelIds = c.getString(c.getColumnIndex("contactLabelIds"));
            RConact RConact = new RConact();
            RConact.setUsername(username);
            RConact.setNickname(nickname);
            if(!TextUtils.isEmpty(alias)){
                RConact.setAlias(alias);}else{
                RConact.setAlias(username);
            }
            RConact.setType(type);
            if (!TextUtils.isEmpty(contactLabelIds))
                RConact.setContactLabelIds(contactLabelIds);
            if(!TextUtils.isEmpty(conRemark)){
                RConact.setConRemark(conRemark);
            }
            RConacts.put(RConact.getUsername(),RConact);
        }
        c.close();
        db.close();
        return RConacts;
    }

//=================================================================================================================================================从微信数据库中拿信息 ；

    /**
     * 重新获得用户的信息；
     */
    public void resetUsesrs(){
        getUserInfo();
    }


    /**
     * 从微信获取用户信息；
     */
    public void getUserInfo(){
        String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        String uin = XmlPaser.getUidFromFile();
        //若是从xml中获取的uin为0或者为空，当前没有登陆的微信号；
        if (!TextUtils.isEmpty(uin)&&!uin.equals("0")) {
            SharedPrefsUtil.putValue(getBaseContext(),"uin",uin);
            SharedPrefsUtil.putValue(getBaseContext(),"Imei",Imei);
            getUserInfo(uin,Imei);
        }
    }


    /**
     *                                                                             若是有
     * 1。从数据库拿出已经存在的用户，若是有用户不存在==》判断是否有当前的账户==》
     *                                                                            若是没有
     *                                                                                   ||
     *                                     1。从数据库拿出已经存在的用户，若是没有户存在==》
     *  链式结构从数据库中获得信息；
     */
    public void getUserInfo(final String uin , final String Imei) {
        final DbManager dbManager =new DbManager(getBaseContext()) ;
            Observable.just(uin)
                    .observeOn(Schedulers.io())
                    .map(new Func1<String, ArrayList<Users>>() {
                        @Override
                        public ArrayList<Users> call(String s) {
                            return (ArrayList<Users>) dbManager.query();
                        }
                    })
                    .map(new Func1<ArrayList<Users>, Users>() {
                        @Override
                        public Users call(ArrayList<Users> userses) {
                            return userses.isEmpty() ? null : InitializeWx.getInstance().checkUserExisted(userses, uin);
                        }
                    })
                    .map(new Func1<Users, UserInfo>() {
                        @Override
                        public UserInfo call(Users users) {
                            if (users != null) {
                                Log.i("szhua", "从数据库获取用户成功！");
                                return InitializeWx.getInstance().readDatabaseFromOldInfo(users,getBaseContext(), uin, dbManager);
                            } else {
                                /* 获得解密数据库(EnMicroMsg.db)的密码 */
                                String password = Md5.getMd5Value(Imei + uin).substring(0, 7);
                                //简单的进行打印（便于跟踪信息）；
                                Log.i("szhua", "psw:" + password);
                                Log.i("szhua","uin:"+uin) ;

                                AppHolder.getInstance().getUser().setPassword(password);
                                return InitializeWx.getInstance().readWeChatDatabase(password, getBaseContext(), uin, dbManager);

                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserInfo>() {
                        @Override
                        public void onCompleted() {
                          Log.i("szhua","切换账号操做完成") ;
                          dbManager.closeDB();
                        }
                        @Override
                        public void onError(Throwable e) {
                            Log.i("leilei", "this is why you can't get userInfo :"+e.toString());
                            Log.i("szhua","解析时出现错误") ;
                            dbManager.closeDB();
                        }
                        @Override
                        public void onNext(UserInfo userInfo) {
                            Log.i("leilei", "NickNameReGet:" + userInfo.getNickName());
                            if (TextUtils.isEmpty(userInfo.getWxNumber())) {
                                userInfo.setWxNumber(userInfo.getWxId());
                            }
                            AppHolder.getInstance().setUser(userInfo);
                            /**
                             * 储存当前登录账户的微信号 ;
                             */
                            SharedPrefsUtil.putValue(getBaseContext(), "wx", AppHolder.getInstance().getUser().getWxNumber());
                            initDao.get_token(userInfo.getWxNumber());
                        }
                    });
       }


    /**
     *
     * @param requestCode 网络请求顺序号，第一个请求，NetRequestOrderNum=0,处理第一条请求的结果。如果等于1,
     */
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
            SharedPrefsUtil.putValue(getBaseContext(),"wx",AppHolder.getInstance().getUser().getWxNumber());

            /**
             * 重新启动服务 ；
             */
            startService(new Intent(getBaseContext(), WorkService.class));
        }

    }
    @Override
    public void onRequestError(int requestCode, String errorInfo, int error_code) {

        /**
         * 设置此微信号没有被授权 ；
         */
        SharedPrefsUtil.putValue(getBaseContext(),"isAuthoritid",false);
        Toast.makeText(getBaseContext(),"此微信号未被授权",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestFaild(int requestCode, String errorNo, String errorMessage) {
    }


    @Override
    public void onNoConnect() {
    }


}
