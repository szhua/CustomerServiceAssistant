package com.pcjh.assistant.entity;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/11/9.
 */
public class LabelGroup {

    //single
    private String labelname  ;

    private ArrayList<String > wxs;

    public String getLabelname() {
        return labelname;
    }

    public void setLabelname(String labelname) {
        this.labelname = labelname;
    }

    public ArrayList<String> getWxs() {
        return wxs;
    }

    public void setWxs(ArrayList<String> wxs) {
        this.wxs = wxs;
    }

    @Override
    public String toString() {
        return "LabelGroup{" +
                "labelname='" + labelname + '\'' +
                ", wxs=" + wxs +
                '}';
    }
}
