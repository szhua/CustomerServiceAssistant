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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 单志华 on 2016/11/8.
 */
public class UploadChatFileDao extends IDao {


    FileReturnEntity fileReturnEntity  ;

    public void setFileReturnEntity(FileReturnEntity fileReturnEntity) {
        this.fileReturnEntity = fileReturnEntity;
    }

    public FileReturnEntity getFileReturnEntity() {
        return fileReturnEntity;
    }

    public UploadChatFileDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }

    public void uploadChatFile (String wx , String token , File file){
        RequestParams requestParams =new RequestParams();
        requestParams.put("wx" ,wx);
        requestParams.put("token",token);
        try {
            requestParams.put("file",file) ;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        postRequest(Constant.BASE_URL+Constant.UPLOAD_CHAT_FILE, requestParams,RequestCode.CODE_2);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
        if(requestCode==RequestCode.CODE_2){
          fileReturnEntity =new FileReturnEntity();




        }
    }
}
