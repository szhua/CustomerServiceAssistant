package com.pcjh.assistant.dao;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;

import java.io.IOException;

/**
 * Created by szhua on 2016/11/11.
 */
public class GetMaterialListDao extends IDao {

    public GetMaterialListDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void getMaterialList(String wx ,String token ,String tag_id){

        RequestParams requestParams =new RequestParams() ;
        requestParams.put("wx",wx);
        requestParams.put("token",token);
        requestParams.put("tag_id",tag_id);

        postRequest(Constant.BASE_URL+Constant.GET_MATERIAL_LIST,requestParams, RequestCode.CODE_0);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {


    }
}
