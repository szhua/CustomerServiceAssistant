package com.pcjh.assistant.dao;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.util.EncryptUtil;

import java.io.IOException;

/**
 * Created by 单志华 on 2016/11/24.
 */
public class TestgZipDao extends IDao {
    public TestgZipDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }
    public void post(String json){
        RequestParams requestParams =new RequestParams() ;

        String result = EncryptUtil.encryptGZIP(json) ;
        Log.i("szhuazip","resultSize"+result.length()) ;
        requestParams.add("fans_wx_tags",result);
        postRequest(Constant.BASE_URL+"testgzip",requestParams, RequestCode.CODE_0);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
        Log.i("szhua",result.toString());
    }
}
