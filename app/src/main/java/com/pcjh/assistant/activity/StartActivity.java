package com.pcjh.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.WorkService;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.dao.InitDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.UserInfo;
import com.pcjh.assistant.entity.Users;
import com.pcjh.assistant.util.InitializeWx;
import com.pcjh.assistant.util.Md5;
import com.pcjh.assistant.util.Root;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.assistant.util.XmlPaser;
import com.pcjh.liabrary.utils.UiUtil;

import java.util.ArrayList;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class StartActivity extends BaseActivity implements INetResult{


    private InitDao initDao =new InitDao(this,this);
    private DbManager dbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 设置不显示title;
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE) ;
        /**
         * 设置显示全屏 ;
         */
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_start);

        ButterKnife.inject(this);

        dbManager =new DbManager(this) ;

        Root.getInstance().getRoot(new Root.IGotRootListener() {
            @Override
            public void onGotRootResult(boolean hasRoot) {

                if (hasRoot) {
                    getUserInfo();
                } else {
                    UiUtil.showLongToast(StartActivity.this, "未获得root权限");
                }
            }
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
        if(subscription!=null){
            subscription.unsubscribe();
        }
    }


    /**
     * 从微信获取用户信息；
     */
    public void getUserInfo(){
        String Imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
                .getDeviceId();
        String uin = XmlPaser.getUidFromFile();
        //若是从xml中获取的uin为0或者为空，当前没有登陆的微信号；
        if (TextUtils.isEmpty(uin)||uin.equals("0")) {
            UiUtil.showLongToast(StartActivity.this, "请登录当前的微信号");
        } else {
            SharedPrefsUtil.putValue(StartActivity.this,"uin",uin);
            SharedPrefsUtil.putValue(StartActivity.this,"Imei",Imei);
            getUserInfo(uin,Imei);
        }
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
     *
     *
     *  链式结构从数据库中获得信息；
     */
    public void getUserInfo(final String uin , final String Imei) {
        subscription =  Observable.just(uin)
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
                                return InitializeWx.getInstance().readDatabaseFromOldInfo(users, StartActivity.this, uin, dbManager);
                            } else {
                                /**
                                 * 获得解密数据库(EnMicroMsg.db)的密码 ;
                                 */
                                String password = Md5.getMd5Value(Imei + uin).substring(0, 7);

                                //简单的进行打印（便于跟踪信息）；
                                Log.i("szhua", "psw" + password);
                                Log.i("szhua","uin"+uin) ;

                                AppHolder.getInstance().getUser().setPassword(password);
                                return InitializeWx.getInstance().readWeChatDatabase(password, StartActivity.this, uin, dbManager);

                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<UserInfo>() {
                        @Override
                        public void onCompleted() {
                            showProgress(false);
                        }
                        @Override
                        public void onError(Throwable e) {
                            showProgress(false);
                            Log.i("leilei", "this is why you can't get userInfo :"+e.toString());
                            Toast.makeText(StartActivity.this, "解析时出现错误", Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onNext(UserInfo userInfo) {
                            Log.i("leilei", "NickName:" + userInfo.getNickName());
                            if (TextUtils.isEmpty(userInfo.getWxNumber())) {
                                userInfo.setWxNumber(userInfo.getWxId());
                            }
                            AppHolder.getInstance().setUser(userInfo);
                            /**
                             * 储存当前登录账户的微信号 ;
                             */
                            SharedPrefsUtil.putValue(StartActivity.this, "wx", AppHolder.getInstance().getUser().getWxNumber());
                            initDao.get_token(userInfo.getWxNumber());
                        }
                    });
        }


}
