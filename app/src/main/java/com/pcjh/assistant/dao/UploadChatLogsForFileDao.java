package com.pcjh.assistant.dao;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.MessageForJson;
import com.pcjh.assistant.entity.WMessage;
import com.pcjh.assistant.util.EncryptUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by szhua on 2016/11/8.
 */
public class UploadChatLogsForFileDao extends IDao {

    /**
     * 用于比较是否传递完数据 ；
     */
    String msgId ;
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public String getMsgId() {
        return msgId;
    }
    public UploadChatLogsForFileDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }




    public void uploadChatLogsForFile(String wx , String token , String json  ){
        RequestParams requestParams =new RequestParams() ;
        requestParams.add("wx",wx);
        requestParams.add("token",token);
        requestParams.add("chat_logs",json);
        postRequest(Constant.BASE_URL+Constant.UPLOAD_CHAT_LOGS,requestParams, RequestCode.UPLOADTEXTFORFILE);
    }




    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {
    }
}
