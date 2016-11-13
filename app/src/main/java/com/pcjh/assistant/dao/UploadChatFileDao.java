package com.pcjh.assistant.dao;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.FileReturnEntity;
import com.pcjh.assistant.entity.WMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 单志华 on 2016/11/8.
 */
public class UploadChatFileDao extends IDao {

    FileReturnEntity fileReturnEntity  ;

    public FileReturnEntity getFileReturnEntity() {
        return fileReturnEntity;
    }

    private WMessage wMessage  ;

    public void setwMessage(WMessage wMessage) {
        this.wMessage = wMessage;
    }

    public WMessage getwMessage() {
        return wMessage;
    }

    public UploadChatFileDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void uploadChatFile (String wx , String token , File file ,String flag){
        RequestParams requestParams =new RequestParams();
        requestParams.put("wx" ,wx);
        requestParams.put("token",token);
        try {
            requestParams.put("file",file) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        requestParams.add("flag",flag);
        postRequest(Constant.BASE_URL+Constant.UPLOAD_CHAT_FILE, requestParams,RequestCode.UPLOADFILE);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
        if(requestCode==RequestCode.UPLOADFILE){
         fileReturnEntity =JsonUtil.node2pojo(result,FileReturnEntity.class);
        }
    }
}
