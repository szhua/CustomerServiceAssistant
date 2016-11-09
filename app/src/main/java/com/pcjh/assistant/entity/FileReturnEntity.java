package com.pcjh.assistant.entity;

/**
 * Created by 单志华 on 2016/11/8.
 */
public class FileReturnEntity {
    /**
     * filepath	文件路径
     filesize	文件大小
     filename	文件名
     fileext	文件扩展名
     server	服务器地址
     */
    String filepath  ;
    String filesize  ;
    String filename ;
    String fileext ;
    String server ;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFileext() {
        return fileext;
    }

    public void setFileext(String fileext) {
        this.fileext = fileext;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
