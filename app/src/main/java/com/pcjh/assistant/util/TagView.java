package com.pcjh.assistant.util;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pcjh.assistant.R;
import com.pcjh.assistant.adapter.TagAllAdapterForGrid;
import com.pcjh.assistant.adapter.TagSelectedAdapterForGrid;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.interfer.TagAddListener;
import com.pcjh.assistant.interfer.TagDeleteListener;

import java.util.ArrayList;

import butterknife.InjectView;

/**
 * Created by szhua on 2016/11/12.
 * 自定义组合控件 ；
 */
public class TagView extends LinearLayout {

    private final NoScrollGridView  selectTagView;
    private final NoScrollGridView  allTagRecycleView;
    private LayoutInflater layoutInflater;

    ArrayList<Tag> tagsSelected  =new ArrayList<>() ;
    ArrayList<Tag> tagsAll =new ArrayList<>();

    private TagAllAdapterForGrid tagAllAdapterForGrid ;
    private TagSelectedAdapterForGrid tagSelectedAdapterForGrid ;

    private TagAddListener tagAddListener  ;
    private ArrayList<Tag> tagsAdded  =new ArrayList<>();
    private ArrayList<Tag> tagsDelete =new ArrayList<>();
    private ArrayList<Tag> orignData =new ArrayList<>() ;


    public ArrayList<Tag> getTagsAdded() {
        return tagsAdded;
    }

    public ArrayList<Tag> getTagsDelete() {
        return tagsDelete;
    }

    public void setTagAddListener(TagAddListener tagAddListener) {
        this.tagAddListener = tagAddListener;
        tagAllAdapterForGrid.setTagAddListener(tagAddListener);
    }

    public void setTagsAll(ArrayList<Tag> tagsAll) {
        this.tagsAll = tagsAll;
        tagAllAdapterForGrid.setTags(tagsAll);
    }

    public void setTagsSelected(ArrayList<Tag> tagsSelected) {
        this.tagsSelected = tagsSelected;
        orignData.addAll(tagsSelected);
        tagSelectedAdapterForGrid.setTagArrayList(tagsSelected);
    }

    public ArrayList<Tag> getTagsAll() {
        return tagsAll;
    }

    public ArrayList<Tag> getTagsSelected() {
        return tagsSelected;
    }
    public TagView(Context context) {
        this(context, null);
    }


    public TagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
        layoutInflater = (LayoutInflater)context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.tag_view_layout, this, true);
        selectTagView = (NoScrollGridView) findViewById(R.id.select_tag_view);

        allTagRecycleView= (NoScrollGridView) findViewById(R.id.all_tag_recycleView);

        tagAllAdapterForGrid =new TagAllAdapterForGrid() ;
        tagSelectedAdapterForGrid =new TagSelectedAdapterForGrid() ;

        selectTagView.setAdapter(tagSelectedAdapterForGrid);
        allTagRecycleView.setAdapter(tagAllAdapterForGrid);


        tagAllAdapterForGrid.setTags(tagsAll);
        tagSelectedAdapterForGrid.setTagArrayList(tagsSelected);
        /**
         * 实现联动 ；
         */
         //按动底部的添加按钮的时候；
            tagAllAdapterForGrid.setTagAddListener(new TagAddListener() {
                @Override
                public void onAddListener(int positon, Tag tag) {
                    boolean isSame =false ;
                    //看看上不是否含有这个标签 ；
                    for (Tag tag1 : tagsSelected) {
                        if(tag1.getType().equals(tag.getType())){
                            isSame =true;
                        }
                    }
                    //若不含有的话；
                    if(!isSame){
                        tagsSelected.add(tag);
                        tagSelectedAdapterForGrid.setTagArrayList(tagsSelected);
                    }
                }
            });
       //按动上部的删除按钮的时候
        tagSelectedAdapterForGrid.setTagDeleteListener(new TagDeleteListener() {
            @Override
            public void onDelete(int positon, Tag tag) {

                if(tagsSelected.size()<=2){
                    Toast.makeText(getContext(),"至少包含两个标签",Toast.LENGTH_SHORT).show();
                    return;
                }
                tagsSelected.remove(positon);
                tagsDelete.add(tag);

                tagSelectedAdapterForGrid.setTagArrayList(tagsSelected);

                if(!tagsDelete.contains(tag)){
                    tagsDelete.add(tag) ;
                }
                if(tagsAdded.contains(tag)){
                    tagsAdded.remove(tag) ;
                }
            }
        });
    }


    public ArrayList<Tag> getDelete(){
        tagsDelete.clear();

        /**
         * 原来的有现在的没有那么就是删除了;
         */
        for (Tag tag : orignData) {
            boolean ishas =false ;
            for (Tag tag1 : tagsSelected) {
                if(tag1.getType().equals(tag.getType())){
                   ishas =true ;
                }
            }
            if(!ishas){
                tagsDelete.add(tag);
            }
        }
        return  tagsDelete ;
    }

    public ArrayList<Tag> getAdded(){
        /**
         * 现在的有原来的没有就是增加了；
         */
        for (Tag tag : tagsSelected) {
            boolean ishas =false ;
            for (Tag tag1 : orignData) {
                if(tag1.getType().equals(tag.getType())){
                    ishas =true ;
                }
            }
            if(!ishas){
                tagsAdded.add(tag);
            }
        }
        return  tagsAdded ;
    }


}
