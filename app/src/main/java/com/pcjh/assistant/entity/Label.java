package com.pcjh.assistant.entity;

import java.io.Serializable;

/**
 * Created by szhua on 2016/11/1.
 */
public class Label implements Serializable {

     public String labelID ;
     public String labelName ;
     public String createTime ;

     public String getLabelID() {
        return labelID;
    }
    public void setLabelID(String labelID) {
        this.labelID = labelID;
    }
    public String getLabelName() {
        return labelName;
    }
    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    @Override
    public String toString() {
        return "Label{" +
                "labelID='" + labelID + '\'' +
                ", labelName='" + labelName + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
