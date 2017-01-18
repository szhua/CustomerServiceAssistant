package com.pcjh.assistant.Tasks;

/**
 * CustomerServiceAssistant
 * Create   2016/12/16 0:03;
 * https://github.com/szhua
 *
 * @author sz.hua
 * 负责任务管理；
 */
public interface UploadTasksMangerImpl {

   void addUploadTask(UploadTask uploadTask);
   void reMoveUploadTask(UploadTask uploadTask);
   void request();
   boolean isEnable();
   void  requestSuccess(UploadTask uploadTask );
   void requetFailed(UploadTask uploadTask) ;

}
