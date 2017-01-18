package com.pcjh.assistant.Tasks;

import android.content.Context;

import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.RequestCode;
import com.pcjh.assistant.base.Constant;

import java.io.IOException;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 0:08;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public class UploadTaskDao extends UploadTaskBaseDao{


    public UploadTaskDao(Context context, UploadTaskResult uploadTaskResult, UploadTask uploadTask) {
        super(context, uploadTaskResult, uploadTask);

    }

    public void request(RequestParams params){
        postRequest(Constant.BASE_URL+Constant.APPEND_FANS,params, RequestCode.APPENDFANS);
    };


    @Override
    public void onRequestSuccess(JsonNode result, int requestCode, UploadTask uploadTask) throws IOException {
        // TODO: 2016/12/16
    }
}
