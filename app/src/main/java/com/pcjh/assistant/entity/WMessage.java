package com.pcjh.assistant.entity;

import android.text.TextUtils;

import com.pcjh.assistant.util.Md5;

/**
 * Created by szhua on 2016/10/10.
 */
public class WMessage {

    public   String talker ;
    public   String content ;
    public  String createTime ;
    public  String msgId ;
    public  String type ;
    //1 为标识的时候说明是自己发出的；
  public   String isSend ;
  public   String headerIcon ;
 public    String displayName ;
  public  String imgPath ;
 public boolean isVoice ;
public boolean isImage ;
    public boolean isVoice() {
        return isVoice;
    }
    public boolean isImage(){return isImage ; }

    public String getImgPath() {

        return imgPath;
    }
    public void setImgPath(String imgPath) {
        if(imgPath.length()=="562220102116f6cbb02a3e5103".length()){
          this.isVoice =true;
          String md5 =  Md5.getMd5Value(imgPath);
          String path1 =md5.substring(0,2) ;
          String path2 =md5.substring(2,4) ;
          imgPath ="/voice2/"+path1+"/"+path2+"/msg_"+imgPath+".amr";
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

    public String getHeaderIcon() {
        return headerIcon;
    }
    public void setHeaderIcon(String headerIcon) {
        this.headerIcon = headerIcon;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
                ", type='" + type + '\'' +
                ", isSend='" + isSend + '\'' +
                '}';
    }
}
