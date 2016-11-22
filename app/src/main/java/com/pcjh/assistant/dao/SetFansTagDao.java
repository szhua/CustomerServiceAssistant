package com.pcjh.assistant.dao;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.Label;
import com.pcjh.assistant.entity.LabelGroup;
import com.pcjh.assistant.entity.RConact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by 单志华 on 2016/11/8.
 */
public class SetFansTagDao extends IDao
{
    public SetFansTagDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void setFansTag(String wx , String token, String tag_name ,ArrayList<String> wxs){
        RequestParams requestParams =new RequestParams() ;
        requestParams.add("wx",wx);
        requestParams.add("token",token);
        for (int i =0 ;i<wxs.size();i++) {
            String rc =wxs.get(i) ;
            requestParams.add("fans_wx[]"+i,rc);
        }
        requestParams.add("tag_name",tag_name);
        postRequest(Constant.BASE_URL+Constant.SET_FANS_TAG,requestParams, RequestCode.CODE_4);
    }
    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {

    }
}
