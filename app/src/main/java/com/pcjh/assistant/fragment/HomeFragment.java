package com.pcjh.assistant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.adapter.HomeListAdapter;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseLoadMoreListFragment;
import com.pcjh.assistant.dao.GetMaterialListDao;
import com.pcjh.assistant.entity.HomeEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by szhua on 2016/10/28.
 */
public class HomeFragment extends BaseLoadMoreListFragment implements INetResult

{
    private ArrayList<HomeEntity > homeEntities  =new ArrayList<>() ;
    
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

    private String type;
    private String title ;
    private HomeListAdapter homeListAdapter ;
    private GetMaterialListDao getMaterialListDao =new GetMaterialListDao(getContext(),this);


    public static HomeFragment getInstance(String title,String type){
        HomeFragment homeFragment =new HomeFragment() ;
        homeFragment.type ="1" ;
        homeFragment.title =title ;
        return homeFragment ;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMaterialListDao.getMaterialList("shuweineng888", AppHolder.getInstance().getToken(),type);
        homeListAdapter =new HomeListAdapter(getContext());
        for (int i = 0; i <9 ; i++) {
            List<String> imgUrls = new ArrayList<>();
            imgUrls.addAll(Arrays.asList(IMG_URL_LIST).subList(0, i % 9));
            HomeEntity post = new HomeEntity(imgUrls);
            homeEntities.add(post);
        }
        homeListAdapter.setHomeEntities(homeEntities);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
}
