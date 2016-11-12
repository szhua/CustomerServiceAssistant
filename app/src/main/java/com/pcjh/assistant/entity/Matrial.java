package com.pcjh.assistant.entity;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/11/11.
 */
public class Matrial {

    /**
     * {"id":"4","material_id":"6","tag_id":"1","images":[{"id":"6","material_id":"4","path":"\/data\/wx\/1478669785523.jpeg","server":"test.baiduor.com","size":"141166","width":"720","height":"1280"}]
     */
    String id ;
    String material_id ;
    String tag_id ;
    String content ;
    String add_time ;

    public String getAdd_time() {
        return add_time;
    }
    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }
    ArrayList<Image> images ;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }


    @Override
    public String toString() {
        return "Matrial{" +
                "id='" + id + '\'' +
                ", material_id='" + material_id + '\'' +
                ", tag_id='" + tag_id + '\'' +
                ", images=" + images +
                ", content" + content +
                '}';
    }
}
