package com.pcjh.assistant.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mengma.asynchttp.RequestCode;
import com.pcjh.assistant.R;
import com.pcjh.assistant.adapter.TagAllAdapterForGrid;
import com.pcjh.assistant.adapter.TagSelectedAdapterForGrid;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.dao.GetMaterialTagsDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.assistant.util.TagView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddTagActivity extends BaseActivity {


    @InjectView(R.id.back_bt)
    ImageView backBt;
    @InjectView(R.id.header_title)
    TextView headerTitle;
    @InjectView(R.id.done_bt)
    ImageView doneBt;
    @InjectView(R.id.tagview)
    TagView tagview;
    private GetMaterialTagsDao getMaterialTagsDao = new GetMaterialTagsDao(this, this);
    ArrayList<Tag> tags = new ArrayList<>();
    ArrayList<Tag> tagsAll = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.inject(this);


        DbManager dbManager = new DbManager(this);
        tags = (ArrayList<Tag>) dbManager.queryTag();

        tagview.setTagsSelected(tags);
        getMaterialTagsDao.getMatrialTag("shuweineng888", SharedPrefsUtil.getValue(this,"token",""));


        doneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Tag> tagadd =tagview.getAdded() ;
                ArrayList<Tag> tagles =tagview.getDelete() ;
                DbManager db =new DbManager(AddTagActivity.this) ;
                if(tagadd.size()>0){
                    db.addTags(tagadd);
                }
                if(tagles.size()>0){
                    db.deleteTags(tagles);
                }
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);
        if (requestCode == RequestCode.CODE_0) {
            tagsAll = (ArrayList<Tag>) getMaterialTagsDao.getTags();
            tagview.setTagsAll(tagsAll);
        }
    }
}
