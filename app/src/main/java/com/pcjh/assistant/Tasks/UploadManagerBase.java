package com.pcjh.assistant.Tasks;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 1:09;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public abstract class UploadManagerBase implements  UploadTasksMangerImpl ,UploadTaskResult {

    @Override
    public void onRequestSuccess(int requestCode, UploadTask uploadTask) {
      requestSuccess(uploadTask);
    }
    @Override
    public void onRequestError(int requestCode, String errorInfo, int error_code, UploadTask uploadTask) {
        requetFailed(uploadTask);
    }
    @Override
    public void onRequestFaild(int requestCode, String errorNo, String errorMessage, UploadTask uploadTask) {
        requetFailed(uploadTask);
    }
    @Override
    public void onNoConnect(UploadTask uploadTask) {
        requetFailed(uploadTask);
    }

}
