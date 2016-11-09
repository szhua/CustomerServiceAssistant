package com.pcjh.assistant.dao;

import android.content.Context;
import android.text.TextUtils;

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
 * Created by szhua on 2016/11/8.
 */
public class RemoveFansDao extends IDao {
    public RemoveFansDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void removeFans(String wx , String token , ArrayList<RConact> rConacts){
        RequestParams params =new RequestParams() ;
        params.put("wx" ,wx) ;
        params.put("token",token);
        for (int i = 0; i < rConacts.size(); i++) {
            RConact rc =rConacts.get(i) ;
            if(TextUtils.isEmpty(rc.getAlias())){
                params.add("fans_wx[]",rc.getTalker());
            }else{
                params.add("fans_wx[]",rc.getAlias());
            }
            params.add("fans_nickname[]"+i,rc.getNickname());
        }
        postRequest(Constant.BASE_URL+Constant.REMOVE_FANS,params, RequestCode.CODE_1);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
//      if(requestCode==RequestCode.CODE_0){
//
//      }
    }
}
