package com.pcjh.assistant.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.RConact;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by szhua on 2016/11/8.
 */
public class AppendFansDao extends IDao {
    public AppendFansDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void apppendFans(String wx , String token , ArrayList<RConact> rConacts){
        RequestParams params =new RequestParams();

        for (int i = 0; i < rConacts.size(); i++) {
        RConact rc =rConacts.get(i) ;
            if(TextUtils.isEmpty(rc.getAlias())){
                params.add("fans_wx[]"+i,rc.getTalker());
            }else{
                params.add("fans_wx[]"+i,rc.getAlias());
            }
            params.add("fans_nickname[]"+i,rc.getNickname());
        }
        params.add("wx" ,wx) ;
        params.add("token",token);
        postRequest(Constant.BASE_URL+Constant.APPEND_FANS,params, RequestCode.CODE_0);
    }
    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
//      if(requestCode==RequestCode.CODE_0){
//
//      }
    }
}
