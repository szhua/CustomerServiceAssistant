package com.pcjh.assistant.Tasks;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 0:09;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public interface UploadTaskResult {
    /**
     * 访问网络成功后更新UI
     *
     * @param requestCode 网络请求顺序号，第一个请求，NetRequestOrderNum=0,处理第一条请求的结果。如果等于1,
     *                    表示处理此页面的第二条请求
     */
    public void onRequestSuccess(int requestCode ,UploadTask uploadTask);

    public void onRequestError(int requestCode, String errorInfo,int error_code ,UploadTask uploadTask);

    public void onRequestFaild(int requestCode ,String errorNo, String errorMessage ,UploadTask uploadTask);

    public void onNoConnect(UploadTask uploadTask);


}
