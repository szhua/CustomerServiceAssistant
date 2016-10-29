package com.mengma.asynchttp;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mengma.asynchttp.interf.IHttp;


import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Create by szhua 2016/3/16
 */
public class Http implements IHttp {
    private static Http instance;
    private static AsyncHttpClient client;

    private Http() {
        client = new AsyncHttpClient();
//        client.setConnectTimeout(1001);
//        client.setMaxRetriesAndTimeout(5,10);
    }

    /**
     * 内部类，用于实现lzay机制
     */
    private static class SingletonHolder {
        /**
         * 单例变量
         */
        private static Http instance = new Http();
    }
    /**
     * 获取单例对象实例
     *
     * @return
     */
    public static Http getInstance() {
        return SingletonHolder.instance;
    }

//    public static Http getInstance() {
//        if (instance == null)
//            instance = new Http();
//        //TODO http拦截器-增加全局错误处理机制
////        ((DefaultHttpClient) client.getHttpClient()).addResponseInterceptor(new HttpResponseInterceptor() {
////            @Override
////            public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
////
////            }
////        });
//        return instance;
//    }

    @Override
    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    @Override
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    @Override
    public void download(String url, BinaryHttpResponseHandler responseHandler) {
        client.get(url, responseHandler);
    }

    @Override
    public void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, url, params, responseHandler);
    }

    @Override
    public void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, url, params, responseHandler);
    }

    @Override
    public void download(Context context, String url, BinaryHttpResponseHandler responseHandler) {
        client.get(context, url, responseHandler);
    }

    @Override
    public void post(Context context, String url, String jsonParams, AsyncHttpResponseHandler responseHandler) {
        try {
            StringEntity entity = new StringEntity(jsonParams);
            entity.setContentType("application/json");
            client.post(context, url, entity, "application/json", responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelRequests(Context context) {
        client.cancelRequests(context, true);
    }

    @Override
    public void cancelAllRequests() {
        client.cancelAllRequests(true);
    }
}
