package com.pcjh.assistant.entity;

/**
 * CustomerServiceAssistant
 * Create   2017/2/17 18:20;
 * @author sz.hua
 */
public class ServiceUserInfo {

    String uin ;
    String pass ;
    String token ;
    String dbPath ;
    String wxid ;
    String username ;


    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    @Override
    public String toString() {
        return "ServiceUserInfo{" +
                "uin='" + uin + '\'' +
                ", pass='" + pass + '\'' +
                ", token='" + token + '\'' +
                ", dbPath='" + dbPath + '\'' +
                ", wxid='" + wxid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
