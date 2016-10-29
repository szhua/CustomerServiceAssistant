package com.pcjh.assistant.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcjh.assistant.R;

/**
 * Created by szhua on 2016/10/29.
 */
public class TagSelectedAdapter extends RecyclerView.Adapter {
    private Context context ;
    private LayoutInflater inflater ;

    public  TagSelectedAdapter(Context context) {
        this.context =context ;
        inflater =LayoutInflater.from(context) ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =inflater.inflate(R.layout.item_tab_selected,parent,false) ;
        RecyclerView.ViewHolder holder =new RecyclerView.ViewHolder(view) {
            @Override
            public String toString() {
                return super.toString();
            }
        } ;

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
