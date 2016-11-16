package com.pcjh.assistant.dao;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.IDao;
import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.base.Constant;
import com.pcjh.assistant.entity.WMessage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 单志华 on 2016/11/8.
 */
public class UploadChatLogsDao extends IDao {

    public UploadChatLogsDao(Context context, INetResult iNetResult) {
        super(context, iNetResult);
    }


    public void uploadChatLogs(String wx , String token , ArrayList<WMessage> wxs ,String filesize ,String server ){
        RequestParams requestParams =new RequestParams() ;
        requestParams.add("wx",wx);
        requestParams.add("token",token);
        for (int i =0 ;i<wxs.size();i++) {
            WMessage wMessage =wxs.get(i);
            requestParams.add("fans_wx[]"+i,wMessage.getDisplayName());
            if(wMessage.getIsSend().equals("1"))
            { requestParams.add("direct[]"+i,"0");
            }
            else{
              requestParams.add("direct[]"+i,"1");
            }
            requestParams.put("type[]"+i,wMessage.getSendType());
            requestParams.add("content[]"+i,wMessage.getContent());
            requestParams.add("add_time[]"+i,""+Long.parseLong(wMessage.getCreateTime())/1000);
            requestParams.put("filesize[]"+i,filesize);
            requestParams.put("server[]"+i,server);
        }
        postRequest(Constant.BASE_URL+Constant.UPLOAD_CHAT_LOGS,requestParams, RequestCode.CODE_5);
    }

    @Override
    public void onRequestSuccess(JsonNode result, int requestCode) throws IOException {

    }
}
