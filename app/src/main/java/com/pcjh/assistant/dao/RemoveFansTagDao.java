package com.pcjh.assistant.dao;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.LabelGroup;
import com.pcjh.assistant.entity.RConact;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 单志华 on 2016/11/9.
 */
public class RemoveFansTagDao extends IDao {

    public RemoveFansTagDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }
    public void removeFansTags(String wx , String token, String tagname,ArrayList<String> wxs){
        RequestParams requestParams =new RequestParams() ;
        requestParams.add("wx",wx);
        requestParams.add("token",token);

        for (int i =0 ;i<wxs.size();i++) {
            String rc =wxs.get(i) ;
            requestParams.add("fans_wx[]"+i,rc);
        }
        requestParams.add("tag_name",tagname);
        postRequest(Constant.BASE_URL+Constant.REMOVE_FANS_TAG,requestParams, RequestCode.CODE_4);
    }
    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {

    }
}
