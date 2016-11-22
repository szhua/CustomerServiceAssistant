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
 * Created by 单志华 on 2016/11/21.
 */
public class AddMaterialRepostCountDao extends IDao {

    /**
     * 转发的数量 ;
     */
    private int repost_count ;

    /*
     转发的消息的位置 ;
     */
    private int position ;

    public int getRepost_count() {
        return repost_count;
    }

    public int getPosition() {
        return position;
    }

    public AddMaterialRepostCountDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }


    public void addMaterialRepostCount(String wx ,String token ,String material_id,int position){
        RequestParams requestParams =new RequestParams() ;
        requestParams.put("wx",wx);
        requestParams.put("token",token);
        requestParams.put("material_id",material_id);
        postRequest(Constant.BASE_URL+Constant.ADD_MATERIAL_REPOST_COUNT,requestParams, RequestCode.ADDMETRIALTRANCOUNT);
        this.position =position ;
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
        if(requestCode==RequestCode.ADDMETRIALTRANCOUNT){
            repost_count =result.findValue("repost_count").asInt() ;
        }
    }
}

