package com.pcjh.assistant.dao;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;

import java.io.IOException;

/**
 * Created by szhua on 2016/11/14.
 */
public class AddMaterialFavoriteCountDao  extends IDao
{

    private int favorite_count ;
    private int postion ;

    public int getFavorite_count() {
        return favorite_count;
    }


    public AddMaterialFavoriteCountDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }
    public void addFavoriateCount(String wx ,String token ,String material_id,int postion){
        RequestParams requestParams =new RequestParams() ;
        requestParams.put("wx",wx);
        requestParams.put("token",token);
        requestParams.put("material_id",material_id);
        postRequest(Constant.BASE_URL+Constant.ADD_MATERIAL_FAVORITE_COUNT,requestParams, RequestCode.CODE_5);
        this.postion =postion ;
    }
    public int getPostion() {
        return postion;
    }
    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
        if(requestCode==RequestCode.CODE_5){
            favorite_count =result.findValue("favorite_count").asInt() ;
        }
    }
}
