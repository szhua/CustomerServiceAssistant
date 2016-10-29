package com.mengma.asynchttp;

/**
 * Create by szhua 2016/3/14
 */
public class HttpClient {
    public final static String TAG = "async";
    /**
     * 私有化的构造方法，保证外部的类不能通过构造器来实例化。
     */
    private HttpClient() {

    }
    /**
     * 内部类，用于实现lzay机制
     */
    private static class SingletonHolder {
        /**
         * 单例变量
         */
        private static HttpClient instance = new HttpClient();
    }


}
