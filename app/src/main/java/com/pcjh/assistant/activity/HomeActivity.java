package com.pcjh.assistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mengma.asynchttp.RequestCode;
import com.pcjh.assistant.R;
import com.pcjh.assistant.WX.WxUtil;
import com.pcjh.assistant.adapter.HomePagerAdapter;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseActivity;
import com.pcjh.assistant.dao.GetMaterialTagsDao;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Tag;
import com.pcjh.assistant.fragment.HomeFragment;
import com.pcjh.assistant.util.SharedPrefsUtil;
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

    private HomePagerAdapter mAdapter;

    private GetMaterialTagsDao getMaterialTagsDao =new GetMaterialTagsDao(this,this) ;
    private ArrayList<Tag> tags =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WxUtil.regWX(this);
        setContentView(R.layout.activity_home);
        ButterKnife.inject(this);

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent =new Intent(HomeActivity.this,AddTagActivity.class) ;
              startActivityForResult(intent,101);
            }
        });
        getMaterialTagsDao.getMatrialTag("shuweineng888", SharedPrefsUtil.getValue(this,"token",""));
    }

    @Override
    protected void onStart(){
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

    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);

        if(requestCode== RequestCode.CODE_0){
            tags = (ArrayList<Tag>) getMaterialTagsDao.getTags();
            DbManager dbManager =new DbManager(this) ;
            if(SharedPrefsUtil.getValue(this,"isFirstPutTags",true)){
                dbManager.addTags(tags);
                SharedPrefsUtil.putValue(this,"isFirstPutTags",false);
            }else{
                /**
                 * 两者进行比较，然后设置tab ;
                 * （增加的不作处理）
                 */
             ArrayList<Tag> tagArrayList = (ArrayList<Tag>) dbManager.queryTag();
         //     ArrayList<Tag> tagAdded =new ArrayList<>() ;
             ArrayList<Tag> tagLess =new ArrayList<>() ;
                /**
                 * 现在有，原来没有，增加了；（暂定不作处理；）
                 */
//              for (Tag tag : tags) {
//                    boolean ishas =false;
//                    for (Tag tag1 : tagArrayList) {
//                        if(tag1.getName().equals(tag.getName())){
//                            ishas =true ;
//                        }
//                    }
//                    if(!ishas) {
//                        tagAdded.add(tag);
//                    }
//                }
                /**
                 * 原来有，现在没有，减少了
                 */
                for (Tag tag : tagArrayList) {
                    boolean ishas =false ;
                    for (Tag tag1 : tags) {
                        if(tag1.getName().equals(tag.getName())){
                            ishas =true ;
                        }
                    }
                    if(!ishas) {
                        tagLess.add(tag);
                    }
                }
                if(tagLess.size()>0){
                  dbManager.deleteTags(tagLess);
                }
                tags = (ArrayList<Tag>) dbManager.queryTag();
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101&&resultCode==RESULT_OK){
          getMaterialTagsDao.getMatrialTag("shuweineng888",SharedPrefsUtil.getValue(this,"token",""));
        }
    }
}
