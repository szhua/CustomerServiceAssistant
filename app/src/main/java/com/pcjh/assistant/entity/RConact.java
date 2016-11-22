package com.pcjh.assistant.entity;

import java.io.Serializable;

/**
 * Created by szhua on 2016/10/19.
 */
public class RConact implements Serializable{

   public String nickname ;
    //微信号；
    public  String alias ;
    public String type ;
    public String contactLabelIds ;
    public String username ;
    public String conRemark ;

    public String getConRemark() {
        return conRemark;
    }

    public void setConRemark(String conRemark) {
        this.conRemark = conRemark;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getContactLabelIds() {
        return contactLabelIds;
    }

    public boolean isLableChageed ;

    public void setLableChageed(boolean lableChageed) {
        isLableChageed = lableChageed;
    }
    public boolean isLableChageed() {
        return isLableChageed;
    }
    public void setContactLabelIds(String contactLabelIds) {
        this.contactLabelIds = contactLabelIds;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }





    @Override
    public String toString() {
        return "RConact{" +
                ", nickname='" + nickname + '\'' +
                ", alias='" + alias + '\'' +
                ", type='" + type + '\'' +
                ", contactLabelIds='" + contactLabelIds + '\'' +
                ", username='" + username + '\'' +
                ", conRemark='" + conRemark + '\'' +
                '}';
    }
}
