package com.pcjh.assistant.Tasks;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mengma.asynchttp.Http;
import com.mengma.asynchttp.ResultUtil;
import com.mengma.asynchttp.interf.INetResult;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 0:13;
 * https://github.com/szhua
 *
 * @author sz.hua
 *
 * 负责网络请求；
 */
public abstract class UploadTaskBaseDao {

    public final static String TAG = "uploadTaskLog";
    protected UploadTaskResult uploadTaskResult;
    protected Context mContext;
    protected UploadTask uploadTask ;

    public UploadTaskBaseDao(Context context, UploadTaskResult uploadTaskResult,UploadTask uploadTask) {
        mContext = context;
        this.uploadTaskResult = uploadTaskResult;
        this.uploadTask =uploadTask;
    }


    /**
     * 得到结果后，对结果处理逻辑
     *
     * @param result      网络请求返回的结果
     * @param requestCode 区别请求号码
     * @throws IOException
     */
    public abstract void onRequestSuccess(JsonNode result, int requestCode ,UploadTask uploadTask ) throws IOException;


    /**
     * POST 请求 MainInUse===Post
     *
     * @param requestCode
     */
    public void postRequest(String url, RequestParams params, final int requestCode) {

        //Log.i(TAG, "POST: " + AsyncHttpClient.getUrlWithQueryString(true, url, params));

        Http.getInstance().post(mContext, url, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Log.d(TAG, responseBody);
                String errorInfo = "";
                int error_code = 0;
                try {
                    JsonNode node = ResultUtil.handleResult(responseBody);
                    int responseCode = node.findValue("status").asInt();
                    //  String responseCode = node.findValue("succeed").asText();
                    if (node.findValue("msg") != null) {
                        errorInfo = node.findValue("msg").asText();
                    }
                    if (responseCode==0) {
                        onRequestSuccess(node, requestCode ,uploadTask);
                        uploadTaskResult.onRequestSuccess(requestCode,uploadTask);
                    } else {
                        uploadTaskResult.onRequestError(requestCode, errorInfo, error_code,uploadTask);
                    }
                } catch (JsonProcessingException e1) {
                    e1.printStackTrace();
                    //添加 parse==》erro也是请求的erro ;
                    uploadTaskResult.onRequestFaild(requestCode,e1.toString(),e1.toString(),uploadTask);
                } catch (IOException e) {
                    e.printStackTrace();
                    //添加 parse==》erro也是请求的erro ;
                    uploadTaskResult.onRequestFaild(requestCode,e.toString(),e.toString(),uploadTask);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                if (statusCode == 0)
                    uploadTaskResult.onNoConnect(uploadTask);
                else
                    uploadTaskResult.onRequestFaild(requestCode,"" + statusCode, responseBody,uploadTask);
            }
        });

    }


}




