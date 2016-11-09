package com.pcjh.assistant.entity;

/**
 * Created by szhua on 2016/10/19.
 */
public class UserInfo {
    public String province;
    public String city ;
    public String provinceCn ;
    public String cityCn  ;
    public String wxId ;
    public String phone ;
    public String nickName ;
    public String headerIcon ;
    public String wxNumber ;
    public String password ;

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getWxNumber() {
        return wxNumber;
    }


    public void setWxNumber(String wxNumber) {
        this.wxNumber = wxNumber;
    }

    public void setHeaderIcon(String headerIcon) {
        this.headerIcon = headerIcon;
    }

    public String getHeaderIcon() {
        return headerIcon;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvinceCn() {
        return provinceCn;
    }

    public void setProvinceCn(String provinceCn) {
        this.provinceCn = provinceCn;
    }

    public String getCityCn() {
        return cityCn;
    }

    public void setCityCn(String cityCn) {
        this.cityCn = cityCn;
    }

    public String getWxId() {
        return wxId;
    }

    public void setWxId(String wxId) {
        this.wxId = wxId;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", provinceCn='" + provinceCn + '\'' +
                ", cityCn='" + cityCn + '\'' +
                ", wxId='" + wxId + '\'' +
                ", phone='" + phone + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headerIcon='" + headerIcon + '\'' +
                '}';
    }
}
