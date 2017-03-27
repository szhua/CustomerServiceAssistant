package com.pcjh.assistant.entity;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/11/24.
 * 转换为json的联系人实体类 ；实现类 ；
 */
public class ContactForJson  extends ContactForJsonBase{

    ArrayList<String> tagname ;
    public ArrayList<String> getTagname() {
        return tagname;
    }
    public void setTagname(ArrayList<String> tagname) {
        this.tagname = tagname;
    }

}
