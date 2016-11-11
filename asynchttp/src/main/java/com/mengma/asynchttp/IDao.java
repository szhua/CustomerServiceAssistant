package com.mengma.asynchttp;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mengma.asynchttp.interf.INetResult;


import java.io.IOException;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * 公用的网络请求辅助类
 * 网路请求的第一层过滤（统一处理错误结果）
 * Create by szhua 2016/3/14
 */
public abstract class IDao {
    public final static String TAG = "httplog";
    protected INetResult mResult;
    protected Context mContext;

    public IDao(Context context, INetResult iNetResult) {
        mContext = context;
        mResult = iNetResult;
    }

    /**
     * 得到结果后，对结果处理逻辑
     *
     * @param result      网络请求返回的结果
     * @param requestCode 区别请求号码
     * @throws IOException
     */
    public abstract void onRequestSuccess(JsonNode result, int requestCode) throws IOException;


    /**
     * get请求网络，本方法提供结果的分发
     *
     * @param params
     */
    protected void getRequest(String url, Map<String, String> params) {
        _getRequest(url, params, 0);
    }

    /**
     * get请求网络，带有请求编号
     *
     * @param params
     */
    protected void getRequestForCode(String url, Map<String, String> params, int requestCode) {
        _getRequest(url, params, requestCode);
    }


    private void _getRequest(String url, Map<String, String> params, final int requestCode) {

        RequestParams ajaxParams = new RequestParams(params);
        Log.d(TAG, AsyncHttpClient.getUrlWithQueryString(true, url, ajaxParams));

        Http.getInstance().get(mContext, url, ajaxParams, new TextHttpResponseHandler() {

            @Override
            public void onCancel() {
                super.onCancel();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Log.d(TAG, responseBody);
                String errorInfo = "";
                int error_code = 0;
                try {
                    JsonNode node = ResultUtil.handleResult(responseBody);
                    boolean responseCode = node.findValue("success").asBoolean();
                    //  String responseCode = node.findValue("succeed").asText();
                    if (node.findValue("msg") != null) {
                        errorInfo = node.findValue("msg").asText();
                    }

                    if (node.findValue("error_code") != null) {
                        error_code = node.findValue("error_code").asInt();
                    }
                    if (responseCode) {
                        onRequestSuccess(node, requestCode);
                        mResult.onRequestSuccess(requestCode);
                    } else {
                        mResult.onRequestError(requestCode, errorInfo, error_code);
                    }
                } catch (JsonProcessingException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    if (Arad.app.config.httpConfig != null) {
//                        JsonNode node = Arad.app.config.httpConfig.handleResult(responseBody);
//                        onRequestSuccess(node, requestCode);
//                    }
//                    mResult.onRequestSuccess(requestCode);
//                } catch (AradException e) {
//                    mResult.onRequestFaild(e.getError_code(), e.getMessage());
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                Log.d(TAG, "statusCode:" + statusCode + " Body:" + responseBody);
                if (statusCode == 0)
                    mResult.onNoConnect();
                else
                    mResult.onRequestFaild("" + statusCode, responseBody);
            }
        });

    }




    /**
     * POST 请求
     *
     * @param requestCode 自定义这是第几个post请求，用于结果的区分
     */
    public void postRequest(String url, RequestParams params, final int requestCode) {

        Log.i(TAG, "POST: " + AsyncHttpClient.getUrlWithQueryString(true, url, params));

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

                    if (node.findValue("error_code") != null) {
                        error_code = node.findValue("error_code").asInt();
                    }
                    if (responseCode==0) {
                        onRequestSuccess(node, requestCode);
                        mResult.onRequestSuccess(requestCode);
                    } else {
                        mResult.onRequestError(requestCode, errorInfo, error_code);
                    }
                } catch (JsonProcessingException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                Log.d(TAG, "statusCode:" + statusCode + " Body:" + responseBody);
                if (statusCode == 0)
                    mResult.onNoConnect();
                else
                    mResult.onRequestFaild("" + statusCode, responseBody);
            }
        });

    }

    /**
     * Post json 请求
     *
     * @param url         接口地址
     * @param jsonParams  请求参数 json字符串
     * @param requestCode 请求编号，区分返回的结果
     */
    public void postRequest(String url, String jsonParams, final int requestCode) {
        Log.d(TAG, "POST: " + url + " JSONParams:" + jsonParams);

        Http.getInstance().post(mContext, url, jsonParams, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable throwable) {
                Log.d(TAG, "statusCode:" + statusCode + " Body:" + responseBody);

                if (statusCode == 0)
                    mResult.onNoConnect();
                else
                    mResult.onRequestFaild("" + statusCode, responseBody);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Log.d(TAG, responseBody);
                String errorInfo = "";
                int error_code = 0;
                try {
                    JsonNode node = ResultUtil.handleResult(responseBody);
                    boolean responseCode = node.findValue("success").asBoolean();
                    //  String responseCode = node.findValue("succeed").asText();
                    if (node.findValue("msg") != null) {
                        errorInfo = node.findValue("msg").asText();
                    }

                    if (node.findValue("error_code") != null) {
                        error_code = node.findValue("error_code").asInt();
                    }
                    if (responseCode) {
                        onRequestSuccess(node, requestCode);
                        mResult.onRequestSuccess(requestCode);
                    } else {
                        mResult.onRequestError(requestCode, errorInfo, error_code);
                    }
                } catch (JsonProcessingException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}