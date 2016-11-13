package com.pcjh.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjh.assistant.R;
import com.pcjh.assistant.entity.Image;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.interfer.TagDeleteListener;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/10/29.
 */
public class TagSelectedAdapterForGrid extends BaseAdapter {

    private ArrayList<Tag> tagArrayList  =new ArrayList<>() ;
    private TagDeleteListener tagDeleteListener  ;

    public void setTagDeleteListener(TagDeleteListener tagDeleteListener) {
        this.tagDeleteListener = tagDeleteListener;
    }

    public void setTagArrayList(ArrayList<Tag> tagArrayList) {
        this.tagArrayList = tagArrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return tagArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder =null ;
        final Tag tag =tagArrayList.get(position) ;
        if(convertView==null){
            convertView =View.inflate(parent.getContext(), R.layout.item_tab_selected,null) ;
            holder =new ViewHolder() ;
            holder.deleteIcon = (ImageView) convertView.findViewById(R.id.delete_icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name .setText(tag.getName());
        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagDeleteListener.onDelete(position,tag);
            }
        });
        return convertView;
    }

    class ViewHolder {
          ImageView deleteIcon ;
          TextView name ;
    }
}
