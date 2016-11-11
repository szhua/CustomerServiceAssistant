package com.pcjh.assistant.entity;

/**
 * Created by 单志华 on 2016/11/10.
 */
public class Tag {
    String type ;
    String name ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
