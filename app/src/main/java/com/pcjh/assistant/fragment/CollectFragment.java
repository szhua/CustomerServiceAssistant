package com.pcjh.assistant.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.pcjh.assistant.adapter.HomeListAdapter;
import com.pcjh.assistant.base.BaseLoadMoreListFragment;
import com.pcjh.assistant.entity.HomeEntity;
import com.pcjh.assistant.interfer.DateChangedListener;
import com.pcjh.liabrary.utils.UiUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 单志华 on 2016/10/29.
 */
public class CollectFragment extends BaseLoadMoreListFragment implements DateChangedListener {

    private ArrayList<HomeEntity > homeEntities  =new ArrayList<>() ;
    private String date ;
    private String[] IMG_URL_LIST = {
            "http://ac-QYgvX1CC.clouddn.com/36f0523ee1888a57.jpg",
            "http://ac-QYgvX1CC.clouddn.com/07915a0154ac4a64.jpg",
            "http://ac-QYgvX1CC.clouddn.com/9ec4bc44bfaf07ed.jpg",
            "http://ac-QYgvX1CC.clouddn.com/fa85037f97e8191f.jpg",
            "http://ac-QYgvX1CC.clouddn.com/de13315600ba1cff.jpg",
            "http://ac-QYgvX1CC.clouddn.com/15c5c50e941ba6b0.jpg",
            "http://ac-QYgvX1CC.clouddn.com/10762c593798466a.jpg",
            "http://ac-QYgvX1CC.clouddn.com/eaf1c9d55c5f9afd.jpg",
            "http://ac-QYgvX1CC.clouddn.com/ad99de83e1e3f7d4.jpg",
            "http://ac-QYgvX1CC.clouddn.com/233a5f70512befcc.jpg",
    };

    private HomeListAdapter homeListAdapter ;
    public static CollectFragment getInstance(String title){
        CollectFragment collectFragment =new CollectFragment() ;
        collectFragment.date =title;
        return collectFragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeListAdapter =new HomeListAdapter(getContext());
        for (int i = 0; i <20; i++) {
            List<String> imgUrls = new ArrayList<>();
            imgUrls.addAll(Arrays.asList(IMG_URL_LIST).subList(0, i % 9));
            HomeEntity post = new HomeEntity(imgUrls);
            homeEntities.add(post);
        }
        homeListAdapter.setHomeEntities(homeEntities);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return homeListAdapter;
    }

    @Override
    public boolean haveMore() {
        return false;
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void onItemClick(int position) {

    }

    /**
     * when the date is changed:
     * we refresh the all datas ;
     * @param date
     */
    @Override
    public void dateChanged(String date) {
        UiUtil.showLongToast(getContext(),date);
    }
}
