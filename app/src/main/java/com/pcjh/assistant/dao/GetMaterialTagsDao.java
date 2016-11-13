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
import com.pcjh.assistant.entity.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by szhua on 2016/11/10.
 */
public class GetMaterialTagsDao extends IDao {

    private List<Tag> tags  =new ArrayList<>();


    public List<Tag> getTags() {
        return tags;
    }

    public GetMaterialTagsDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void getMatrialTag(String wx ,String token ){
        RequestParams requestParams =new RequestParams();
        requestParams.put("wx" ,wx);
        requestParams.put("token",token);
        postRequest(Constant.BASE_URL+Constant.GET_MATERIAL_TAGS,requestParams, RequestCode.CODE_0);
    }
    public void getMatrialTagSelected(String wx ,String token ){
        RequestParams requestParams =new RequestParams();
        requestParams.put("wx" ,wx);
        requestParams.put("token",token);
        postRequest(Constant.BASE_URL+Constant.GET_MATERIAL_TAGS,requestParams, RequestCode.CODE_1);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {

        if (requestCode== RequestCode.CODE_0||requestCode==RequestCode.CODE_1) {
            JsonNode node =result.findValue("tags") ;
            try {
                JSONObject jsonObject =new JSONObject(JsonUtil.node2json(node));
                 Iterator<String> keys =jsonObject.keys();
                 while (keys.hasNext()){
                     String key =  keys.next() ;
                     Log.i("szhua",key);
                     String value =jsonObject.getString(key);
                     Log.i("szhua",value);
                     Tag tag =new Tag();
                     tag.setType(key);
                     tag.setName(value);
                     tags.add(tag);
                 }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
