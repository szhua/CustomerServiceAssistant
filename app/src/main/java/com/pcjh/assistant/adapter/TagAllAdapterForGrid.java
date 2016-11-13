package com.pcjh.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pcjh.assistant.R;
import com.pcjh.assistant.dao.GetMaterialTagsDao;
import com.pcjh.assistant.entity.Image;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.interfer.TagAddListener;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/10/29.
 */
public class TagAllAdapterForGrid extends BaseAdapter {


   private TagAddListener tagAddListener ;

    public void setTagAddListener(TagAddListener tagAddListener) {
        this.tagAddListener = tagAddListener;
    }

    private ArrayList<Tag> tags =new ArrayList<>() ;
    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return tags.size();
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
        final Tag tag =tags.get(position) ;
        if(convertView==null){
            convertView =View.inflate(parent.getContext(), R.layout.item_tab_all,null) ;
            holder =new ViewHolder() ;
            holder.add_icon = (ImageView) convertView.findViewById(R.id.add_icon);
            holder.name= (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(tag.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagAddListener.onAddListener(position,tag);
            }
        });
        return convertView;
    }

    class ViewHolder {
          ImageView add_icon  ;
        TextView  name  ;
    }
}
