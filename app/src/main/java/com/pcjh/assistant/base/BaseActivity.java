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
    public void onRequestFaild(int requestCode, String errorNo, String errorMessage) {
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

    public String getWx(){
        String wx =SharedPrefsUtil.getValue(this,"wx","") ;
        return   wx  ;
    }
    public String getToken(){
        String token = SharedPrefsUtil.getValue(this,"token","") ;
        return   token  ;
    }

}
