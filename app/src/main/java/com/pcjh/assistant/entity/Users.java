package com.pcjh.assistant.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by szhua on 2016/10/18.
 * 这个类用于储存本机访问过的数据库的信息；
 * 这样就方便的少去很多工作；
 *
 * 这里先暂时的用uin作为用户的唯一的标识；
 */
public class Users  implements Serializable{

    public int id =-1;
    public String uin ;
    public String password ;
    public String dbPath ;
    public String nickName ;
    public String face ;
    public String contactLabelIds ;
   public String userId ;


    public String getUserId() {
        if(!TextUtils.isEmpty(dbPath)){
// /data/data/com.tencent.mm/MicroMsg/a155cad2529e20357ada9b6b7e558c69/EnMicroMsg.db
            String parent =dbPath.substring("/data/data/com.tencent.mm/MicroMsg/".length(),"/data/data/com.tencent.mm/MicroMsg/a155cad2529e20357ada9b6b7e558c69".length());
            this.userId =parent ;
        }
        return userId;
    }

    public String getContactLabelIds() {
        return contactLabelIds;
    }
    public void setContactLabelIds(String contactLabelIds) {
        this.contactLabelIds = contactLabelIds;
    }

    public String getUin() {
        return uin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getFace() {
        return face;
    }
    public void setFace(String face) {
        this.face = face;
    }
    @Override
    public String toString() {
        return "Users{" +
                "uin='" + uin + '\'' +
                ", password='" + password + '\'' +
                ", dbPath='" + dbPath + '\'' +
                ", nickName='" + nickName + '\'' +
                ", face='" + face + '\'' +
                '}';
    }
}
