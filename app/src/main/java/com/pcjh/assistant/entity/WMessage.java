package com.pcjh.assistant.entity;

import android.text.TextUtils;

import com.pcjh.assistant.util.Md5;

import java.io.File;

/**
 * Created by szhua on 2016/10/10.
 */
public class WMessage {

    public   String talker ;
    public   String content ;
    public  String createTime ;
    public  String msgId ;
    //1 为标识的时候说明是自己发出的；
    public   String isSend ;
    public    String displayName ;
    public  String imgPath ;
    public boolean isVoice ;
    public boolean isImage ;
    public int sendType ;
    public File  file ;

   public String filesize ;
    public String serverPath ;
    public String filePath ;
    public int uploadToServerCount ;

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public int getUploadToServerCount() {
        return uploadToServerCount;
    }

    public void addUploadCount() {
        this.uploadToServerCount++;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getServerPath() {
        return serverPath;
    }
    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }


    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }

    public int getSendType() {
        return sendType;
    }
    public void setSendType(int sendType) {
        this.sendType = sendType;
    }
    public boolean isVoice() {
        return isVoice;
    }
    public boolean isImage(){return isImage ; }

    public String getImgPath() {

        return imgPath;
    }
    public void setImgPath(String imgPath) {
        if(imgPath.length()=="562220102116f6cbb02a3e5103".length()){
          String md5 =  Md5.getMd5Value(imgPath);
          String path1 =md5.substring(0,2) ;
          String path2 =md5.substring(2,4) ;
          imgPath ="/voice2/"+path1+"/"+path2+"/msg_"+imgPath+".amr";
          this.isVoice =true;
        }
        if(!TextUtils.isEmpty(imgPath)){
            if(imgPath.contains("THUMBNAIL_DIRPATH://th_")){
                int len ="THUMBNAIL_DIRPATH://th_".length() ;
                String path1 =imgPath.substring(len,len+2) ;
                String path2 =imgPath.substring(len+2,len+4) ;
                String orig =imgPath.substring("THUMBNAIL_DIRPATH://th_".length(),imgPath.length()) ;
                imgPath =path1+"/"+path2+"/"+orig;
                this.isImage =true ;
            }
        }

        this.imgPath = imgPath;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTalker() {
        return talker;
    }
    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }


    public String getIsSend() {
        return isSend;
    }
    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    @Override
    public String toString() {
        return "WMessage{" +
                "talker='" + talker + '\'' +
                ", content='" + content + '\'' +
                ", createTime='" + createTime + '\'' +
                ", msgId='" + msgId + '\'' +
                ", isSend='" + isSend + '\'' +
                ", displayName='" + displayName + '\'' +
                ", imgPath='" + imgPath + '\'' +
                ", isVoice=" + isVoice +
                ", isImage=" + isImage +
                ", sendType=" + sendType +
                ", file=" + file +
                ", serverPath='" + serverPath + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
