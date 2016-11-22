package com.pcjh.assistant.entity;

/**
 * Created by 单志华 on 2016/11/10.
 */
public class LabelConact {

    private String  tag_name  ;
    private String wx ;

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    @Override
    public String toString() {
        return "LabelConact{" +
                "tag_name='" + tag_name + '\'' +
                ", wx='" + wx + '\'' +
                '}';
    }
}
