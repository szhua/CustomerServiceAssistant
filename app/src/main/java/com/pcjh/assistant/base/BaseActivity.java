package com.pcjh.assistant.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mengma.asynchttp.Http;
import com.mengma.asynchttp.dialog.ProgressHUD;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.liabrary.utils.UiUtil;

/**
 * Created Szhua 2016/10/28
 */
public class BaseActivity extends AppCompatActivity implements INetResult {
    ProgressHUD mProgressHUD;
    private ImageView searchBt ;
    private ImageView collectBt ;
    private ImageView backBt  ;
    private ImageView doneBt ;

    @Override
    protected void onStart() {
        super.onStart();

        searchBt = (ImageView) findViewById(R.id.search_bt);
        collectBt = (ImageView) findViewById(R.id.collect_bt);
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
}
