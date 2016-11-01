package com.pcjh.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.pcjh.assistant.R;
import com.pcjh.assistant.WX.WxUtil;
import com.pcjh.assistant.adapter.HomePagerAdapter;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.fragment.HomeFragment;
import com.pcjh.liabrary.tablayout.SlidingTabLayout;
import com.tencent.mm.sdk.modelmsg.WXTextObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomeActivity extends BaseActivity {
    @InjectView(R.id.slidingtablayout)
    SlidingTabLayout slidingtablayout;
    @InjectView(R.id.viewpager)
    ViewPager viewpager;
    @InjectView(R.id.add_icon)
    ImageView addIcon;
    private ArrayList<HomeFragment> homeFragments = new ArrayList<>();
    private final String[] mTitles = {
            "肠胃", "皮炎", "肠炎"
            , "便秘", "糖尿病"
    };
    private HomePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WxUtil.regWX(this);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);
        for (String title : mTitles) {
            homeFragments.add(HomeFragment.getInstance(title));
        }
        mAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mAdapter.setmTitles(mTitles);
        mAdapter.setHomeFragments(homeFragments);
        viewpager.setAdapter(mAdapter);
        slidingtablayout.setViewPager(viewpager);
        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
  WxUtil.shareToWxCircle("jifsdjfidsfjdljsdlkfjsdfjsdjfsdfjsdfjsd");
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        setSearchBtListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        setCollectBtListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CollectActivity.class);
                startActivity(intent);
            }
        });
    }
}
