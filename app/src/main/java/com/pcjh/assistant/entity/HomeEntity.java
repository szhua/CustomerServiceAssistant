package com.pcjh.assistant.entity;

import java.util.List;

/**
 * Created by szhua on 2016/10/28.
 */
public class HomeEntity  {


    public HomeEntity(List<String> mImgUrlList) {
        this.mImgUrlList = mImgUrlList;
    }

    private List<String> mImgUrlList;
    public List<String> getmImgUrlList() {
        return mImgUrlList;
    }
    public void setmImgUrlList(List<String> mImgUrlList) {
        this.mImgUrlList = mImgUrlList;
    }






}
