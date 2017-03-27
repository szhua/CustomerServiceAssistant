package com.pcjh.assistant.entity;

/**
 * Created by szhua on 2016/11/24.
 * 转换为json的联系人实体类 《base》；
 */

public class ContactForJsonBase {


    String fans_nickname ;
    String fans_wx ;
    String modify_time ;
    String modify_type ;
    String remark ;


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFans_nickname() {
        return fans_nickname;
    }

    public void setFans_nickname(String fans_nickname) {
        this.fans_nickname = fans_nickname;
    }

    public String getFans_wx() {
        return fans_wx;
    }

    public void setFans_wx(String fans_wx) {
        this.fans_wx = fans_wx;
    }

    public String getModify_time() {
        return modify_time;
    }

    @Override
    public String toString() {
        return "ContactForJsonBase {" +
                ", fans_nickname='" + fans_nickname + '\'' +
                ", fans_wx='" + fans_wx + '\'' +
                ", modify_time='" + modify_time + '\'' +
                ", modify_type='" + modify_type + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getModify_type() {
        return modify_type;
    }

    public void setModify_type(String modify_type) {
        this.modify_type = modify_type;
    }
}
