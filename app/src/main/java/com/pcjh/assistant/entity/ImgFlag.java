package com.pcjh.assistant.entity;

import java.io.Serializable;

/**
 * Created by szhua on 2016/10/19.
 */
public class ImgFlag  implements Serializable{
  public String username ;
  public  String lastupdatetime ;
  public  String reserved1 ;
  public  String reserved2 ;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(String lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    @Override
    public String toString() {
        return "ImgFlag{" +
                "username='" + username + '\'' +
                ", lastupdatetime='" + lastupdatetime + '\'' +
                ", reserved1='" + reserved1 + '\'' +
                ", reserved2='" + reserved2 + '\'' +
                '}';
    }
}
