package com.pcjh.assistant.dao;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.Http;
import com.mengma.asynchttp.HttpClient;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.ContactForJsonBase;
import com.pcjh.assistant.entity.RConact;
import com.pcjh.assistant.util.EncryptUtil;


import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by szhua on 2016/11/8.
 */
public class AppendFansDao extends IDao {
    public AppendFansDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void apppendFans(String wx , String token , String json){
        RequestParams params =new RequestParams();
        params.add("wx" ,wx);
        params.add("token",token);
        params.put("fans_wx_tags",json);
        postRequest(Constant.BASE_URL+Constant.APPEND_FANS,params, RequestCode.APPENDFANS);
    }


    public void changeFans(String wx , String token , String json){
        RequestParams params =new RequestParams();
        params.add("wx" ,wx) ;
        params.add("token",token);
        params.put("fans_wx_tags",json);
        postRequest(Constant.BASE_URL+Constant.APPEND_FANS ,params, RequestCode.CHANGEFANS);
    }
    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
//      if(requestCode==RequestCode.CODE_0){
//      }
    }
}
