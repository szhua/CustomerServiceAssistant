package com.mengma.asynchttp.interf;

/**
 * Created by szhua 2016/3/14
 */
public interface INetResult {
    /**
     * 访问网络成功后更新UI
     *
     * @param requestCode 网络请求顺序号，第一个请求，NetRequestOrderNum=0,处理第一条请求的结果。如果等于1,
     *                    表示处理此页面的第二条请求
     */
    public void onRequestSuccess(int requestCode);

    public void onRequestError(int requestCode, String errorInfo,int error_code);

    public void onRequestFaild(int requestCode ,String errorNo, String errorMessage);

    public void onNoConnect();
}
