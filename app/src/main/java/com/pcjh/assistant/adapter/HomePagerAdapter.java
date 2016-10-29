package com.pcjh.assistant.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pcjh.assistant.fragment.HomeFragment;

import java.util.ArrayList;

/**
 * Created by szhua on 2016/10/28.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    private ArrayList<HomeFragment> homeFragments ;
    private String [] mTitles  ;

    public void setHomeFragments(ArrayList<HomeFragment> homeFragments) {
        this.homeFragments = homeFragments;
    }
    public void setmTitles(String[] mTitles) {
        this.mTitles = mTitles;
    }

     public HomePagerAdapter(FragmentManager fm ,String [] mTitles){
         super(fm);
         this.mTitles =mTitles ;
     }

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return homeFragments==null?0:homeFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return homeFragments.get(position);
    }

}