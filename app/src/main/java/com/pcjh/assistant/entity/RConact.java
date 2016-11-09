package com.pcjh.assistant.entity;

import java.io.Serializable;

/**
 * Created by szhua on 2016/10/19.
 */
public class RConact implements Serializable{

public   String talker ;
   public String nickname ;
    //微信号；
    public  String alias ;
    public String type ;
    public String contactLabelIds ;
    public String username ;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getContactLabelIds() {
        return contactLabelIds;
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



    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    @Override
    public String toString() {
        return "RConact{" +
                "talker='" + talker + '\'' +
                ", nickname='" + nickname + '\'' +
                ", alias='" + alias + '\'' +
                ", type='" + type + '\'' +
                ", contactLabelIds='" + contactLabelIds + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
