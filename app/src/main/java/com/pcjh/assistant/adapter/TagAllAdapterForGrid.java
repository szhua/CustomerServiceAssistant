package com.pcjh.assistant.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pcjh.assistant.R;

/**
 * Created by 单志华 on 2016/10/29.
 */
public class TagAllAdapterForGrid extends BaseAdapter {
    @Override
    public int getCount() {
        return 40;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder =null ;
        if(convertView==null){
            convertView =View.inflate(parent.getContext(), R.layout.item_tab_all,null) ;
            holder =new ViewHolder() ;
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    class ViewHolder {

    }
}
