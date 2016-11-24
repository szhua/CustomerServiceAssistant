package com.pcjh.assistant.entity;

/**
 * Created by szhua on 2016/11/24.
 */
public class MessageForJson  {

    String fans_wx ;
    String direct ;
    String type ;
    String content ;
    String add_time;
    String filesize;
    String server;


    public String getFans_wx() {
        return fans_wx;
    }

    public void setFans_wx(String fans_wx) {
        this.fans_wx = fans_wx;
    }

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "MessageForJson{" +
                "fans_wx='" + fans_wx + '\'' +
                ", direct='" + direct + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", add_time='" + add_time + '\'' +
                ", filesize='" + filesize + '\'' +
                ", server='" + server + '\'' +
                '}';
    }
}
