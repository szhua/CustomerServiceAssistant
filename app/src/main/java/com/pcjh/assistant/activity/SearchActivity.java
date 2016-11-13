package com.pcjh.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.mengma.asynchttp.JsonUtil;
import com.mengma.asynchttp.RequestCode;
import com.pcjh.assistant.R;
import com.pcjh.assistant.adapter.HomePagerAdapter;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.dao.GetMaterialTagsDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.fragment.HomeFragment;
import com.pcjh.assistant.util.SharedPrefsUtil;
import com.pcjh.liabrary.tablayout.SlidingTabLayout;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends BaseActivity {

    @InjectView(R.id.back_bt)
    ImageView backBt;
    @InjectView(R.id.search_et)
    EditText searchEt;
    @InjectView(R.id.slidingtablayout)
    SlidingTabLayout slidingtablayout;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;

    private ArrayList<HomeFragment> homeFragments = new ArrayList<>();

    private HomePagerAdapter mAdapter;

    private GetMaterialTagsDao getMaterialTagsDao =new GetMaterialTagsDao(this,this) ;
    private ArrayList<Tag> tags =new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        getMaterialTagsDao.getMatrialTag("shuweineng888", AppHolder.getInstance().getToken());
    }
    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);
        if(requestCode== RequestCode.CODE_0){
            tags = (ArrayList<Tag>) getMaterialTagsDao.getTags();
            ArrayList<String> mTitles =new ArrayList<String>() ;
            homeFragments.clear();
            for (Tag tag : tags) {
                homeFragments.add(HomeFragment.getInstance(tag.getName(),tag.getType()));
                mTitles.add(tag.getName());
            }
            mAdapter = new HomePagerAdapter(getSupportFragmentManager());
            mAdapter.setmTitles(mTitles);
            mAdapter.setHomeFragments(homeFragments);
            viewpager.setAdapter(mAdapter);
            slidingtablayout.setViewPager(viewpager);
        }
    }
}
