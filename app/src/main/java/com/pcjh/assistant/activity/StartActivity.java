package com.pcjh.assistant.activity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.WorkService;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.dao.InitDao;
import com.pcjh.assistant.dao.TestgZipDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.ContactForJson;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.util.ChmodUtil;
import com.pcjh.assistant.util.Md5;
import com.pcjh.assistant.util.Root;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.assistant.util.XmlPaser;
import com.pcjh.liabrary.utils.UiUtil;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class StartActivity extends BaseActivity implements INetResult{


    private InitDao initDao;
    private String Imei;
    private String uin;
    private String password;
    private DbManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.inject(this);




        dbManager =new DbManager(this) ;
        Root.getInstance().getRoot(new Root.IGotRootListener() {
            @Override
            public void onGotRootResult(boolean hasRoot) {
                if (!hasRoot) {
                    UiUtil.showLongToast(StartActivity.this, "未获得root权限");
                }else{
                    Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                            .getDeviceId();
                    uin = XmlPaser.getUidFromFile();
                    //若是从xml中获取的uin为0或者为空，当前没有登陆的微信号；
                    if (TextUtils.isEmpty(uin)||uin.equals("0")) {
                        UiUtil.showLongToast(StartActivity.this, "请登录当前的微信号");
                    } else {
                        SharedPrefsUtil.putValue(StartActivity.this,"uin",uin);
                        SharedPrefsUtil.putValue(StartActivity.this,"Imei",Imei);
                        subscription= getUserInfo(uin)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<UserInfo>() {
                                    @Override
                                    public void onCompleted() {
                                        showProgress(false);
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        showProgress(false);
                                        Log.i("leilei", e.toString());
                                        Toast.makeText(StartActivity.this,"解析时出现错误",Toast.LENGTH_SHORT).show();
                                    }
                                    @Override
                                    public void onNext(UserInfo userInfo) {
                                        Log.i("leilei", "nickname" +userInfo.getNickName());
                                        if(TextUtils.isEmpty(userInfo.getWxNumber())){
                                            userInfo.setWxNumber(userInfo.getWxId());
                                        }
                                        AppHolder.getInstance().setUser(userInfo);
                                        /**
                                         * 储存当前登录账户的微信号 ;
                                         */
                                        SharedPrefsUtil.putValue(StartActivity.this,"wx",AppHolder.getInstance().getUser().getWxNumber());
                                        initDao =new InitDao(StartActivity.this,StartActivity.this);
                                        initDao.get_token(userInfo.getWxNumber());
                                    }
                                });
                    }
                }}

        });
    }

    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);
        if(requestCode==RequestCode.INITSUCESS) {
            String token = initDao.getToken();
            AppHolder.getInstance().setToken(token);
            Users users = AppHolder.getInstance().getUsers();
            SharedPrefsUtil.putValue(StartActivity.this, "token", token);
            SharedPrefsUtil.putValue(StartActivity.this, "uin", users.getUin());
            SharedPrefsUtil.putValue(StartActivity.this, "password", users.getPassword());
            SharedPrefsUtil.putValue(StartActivity.this, "dbPath", users.getDbPath());
            SharedPrefsUtil.putValue(StartActivity.this, "token", token);
            SharedPrefsUtil.putValue(StartActivity.this, "wxid", AppHolder.getInstance().getUser().getWxId());
            new Thread(){
                @Override
                public void run() {
                    Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    try {
                        sleep(2000);
                        startService(new Intent(StartActivity.this, WorkService.class));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onRequestError(int requestCode, String errorInfo, int erro_code) {
        super.onRequestError(requestCode,errorInfo,erro_code);
        if(requestCode== RequestCode.CODE_0){
            Toast.makeText(this,"此微信号未被授权",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param uin
     * @return UserInfo
     *                                                                                   ||
     *                                                                             若是有
     * 1。从数据库拿出已经存在的用户，若是有用户不存在==》判断是否有当前的账户==》
     *                                                                            若是没有
     *                                                                                   ||
     *                                     1。从数据库拿出已经存在的用户，若是没有户存在==》
     */
    public Observable<UserInfo> getUserInfo(final String uin) {
        Observable<UserInfo> userInfoOb = null;
        if (!TextUtils.isEmpty(uin) && Integer.parseInt(uin) != 0) {
            userInfoOb = Observable.just(uin)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            showProgress(true);
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Func1<String, ArrayList<Users>>() {
                        @Override
                        public ArrayList<Users> call(String s) {
                            return (ArrayList<Users>) dbManager.query();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<ArrayList<Users>>() {
                        @Override
                        public void call(ArrayList<Users> userses) {
                            if (userses == null || userses.isEmpty()) {
                                Log.i("szhua","数据库暂无微信号");
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
                                 Log.i("szhua", "从数据库获得用户成功！");
                            } else {
                                Log.i("szhua", "从数据库获取用户失败！");
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
                                String result = Imei + uin;
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

                /**
                 * 若不是从旧的数据库获取数据的话;
                 */
                if (!isFromOld) {
                    Users users = new Users();
                    /**
                     * 储存用户的微信号的路径 ；
                     */
                    users.setDbPath(dbFile.getAbsolutePath());
                    /**
                     * 储存password
                     */
                    users.setPassword(pass);
                    /**
                     * 储存uin；
                     */
                    users.setUin(uin);
                    Log.i("users", users.toString());
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



    //从微信文件中读取用户的信息 ；
    public UserInfo readWeChatDatabase() {
         getRoot();
        //此处用于读取 当前微信号的uin
        SQLiteDatabase.loadLibs(this);
        File testFile = new File("/data/data/com.tencent.mm/MicroMsg/");
        /**
         * 若是不能够读取的话，设置他为可读取 ;
         */
        if (!testFile.canRead()) {
            ChmodUtil.setFileCanRead(testFile);
        }
        /**
         * 可能是当前微信的EnMicrloMsg的文件 ；
         */
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
        if (dbDatas.isEmpty()) {
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


    // get the root privileges
    public void getRoot(){
        /**
         * for Root ;
         */
         Process process =null;
         DataOutputStream os =null;
         DataInputStream is =null;
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
        }
    }

}
