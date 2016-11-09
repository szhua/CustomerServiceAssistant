package com.pcjh.assistant.dao;
import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;

import java.io.IOException;

/**
 * Created by szhua on 2016/11/3.
 */
public class InitDao extends IDao {

    private String json ;
    private String token ;
    public String getJson() {
        return json;
    }

    public String getToken() {
        return token;
    }

    public InitDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }


      public void get_token (String wx) {
          RequestParams params = new RequestParams();
          params.put("wx", wx);
          postRequest(Constant.BASE_URL + Constant.GET_TOKEN, params, RequestCode.CODE_0);
      }


    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
        if(requestCode==RequestCode.CODE_0){
         //   Log.i("szhua", JsonUtil.node2json(result));
            json =JsonUtil.node2json(result);
            token =result.findValue("token").asText() ;
        }
    }
}
