package com.pcjh.assistant.entity;

/**
 * Created by 单志华 on 2016/11/11.
 */
public class Image  {

    /**
     * {"id":"6",
     * "material_id":"4",
     * "path":"\/data\/wx\/1478669785523.jpeg",
     * "server":"test.baiduor.com",
     * "size":"141166","width":"720","height":"1280"}
     */

    String id ;
    String material_id ;
    String path ;
    String server ;


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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }


    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", material_id='" + material_id + '\'' +
                ", path='" + path + '\'' +
                ", server='" + server + '\'' +
                '}';
    }
}
