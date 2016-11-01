package com.pcjh.assistant.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.pcjh.assistant.R;
import com.pcjh.assistant.adapter.TagAllAdapterForGrid;
import com.pcjh.assistant.adapter.TagSelectedAdapter;
import com.pcjh.assistant.adapter.TagSelectedAdapterForGrid;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.util.FullyGridLayoutManager;
import com.pcjh.assistant.util.NoScrollGridView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddTagActivity extends BaseActivity {

    @InjectView(R.id.select_tag_view)
    NoScrollGridView selectTagView;
    @InjectView(R.id.tag_selected_container)
    LinearLayout tagSelectedContainer;
    @InjectView(R.id.all_tag_recycleView)
    NoScrollGridView allTagRecycleView;
    @InjectView(R.id.tag_all_container)
    LinearLayout tagAllContainer;

    private FullyGridLayoutManager gridLayoutManager  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.inject(this);

        gridLayoutManager =new FullyGridLayoutManager(this,4) ;
     //   selectTagView.setLayoutManager(gridLayoutManager);
        selectTagView.setAdapter(new TagSelectedAdapterForGrid());
        allTagRecycleView.setAdapter(new TagAllAdapterForGrid());

       

    }
}
