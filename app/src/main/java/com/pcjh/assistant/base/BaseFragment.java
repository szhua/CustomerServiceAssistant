package com.pcjh.assistant.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mengma.asynchttp.dialog.ProgressHUD;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.liabrary.utils.UiUtil;


/**
 * Created 2016/3/24
 */
public class BaseFragment extends Fragment implements INetResult {



    ProgressHUD mProgressHUD;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        UiUtil.showLongToast(getContext(), errorMessage);
        showProgress(false);
    }

    @Override
    public void onNoConnect() {
        showProgress(false);
        UiUtil.showLongToast(getContext(), "无网络连接");
    }

    public void showProgress(boolean show) {
        showProgressWithText(show, "加载中...");
    }

    public void showProgressWithText(boolean show, String message) {
        if (show) {
            mProgressHUD = ProgressHUD.show(getActivity(), message, true, true, null);
        } else {
            if (mProgressHUD != null) {
                mProgressHUD.dismiss();
            }
        }
    }

    public String getWx(){
        String wx = SharedPrefsUtil.getValue(getContext(),"wx","") ;
        return   wx  ;
    }


}
