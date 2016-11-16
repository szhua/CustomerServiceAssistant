package com.pcjh.assistant.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.mengma.asynchttp.RequestCode;
import com.mengma.asynchttp.interf.INetResult;
import com.pcjh.assistant.R;
import com.pcjh.assistant.adapter.HomeListAdapter;
import com.pcjh.assistant.base.AppHolder;
import com.pcjh.assistant.base.BaseLoadMoreListFragment;
import com.pcjh.assistant.dao.AddMaterialFavoriteCountDao;
import com.pcjh.assistant.dao.GetMaterialListDao;
import com.pcjh.assistant.entity.HomeEntity;
import com.pcjh.assistant.entity.Matrial;
import com.pcjh.assistant.util.SharedPrefsUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by szhua on 2016/10/28.
 */
public class HomeFragment extends BaseLoadMoreListFragment implements INetResult

{
    private ArrayList<HomeEntity > homeEntities  =new ArrayList<>() ;
    private String type;
    private String title ;
    private HomeListAdapter homeListAdapter ;
    private GetMaterialListDao getMaterialListDao =new GetMaterialListDao(getContext(),this);
    private ArrayList<Matrial> matrialArrayList =new ArrayList<Matrial>() ;
    private AddMaterialFavoriteCountDao addMaterialFavoriteCountDao =new AddMaterialFavoriteCountDao(getContext(),this) ;
    private String words  ;

    public void setWords(String words) {
        this.words = words;
    }

    public String getWords() {
        return words;
    }
    public void setSearchWords(String words){
        this.words =words ;
        getMaterialListDao.getMaterialList("shuweineng888", SharedPrefsUtil.getValue(getContext(),"token",""),type,words);
    }
    public static HomeFragment getInstance(String title,String type){
        HomeFragment homeFragment =new HomeFragment() ;
        homeFragment.type =type ;
        homeFragment.title =title;
        return homeFragment ;
    }

    public void addMatrialFav(String matrial_id,int postion){
    addMaterialFavoriteCountDao.addFavoriateCount("shuweineng888",SharedPrefsUtil.getValue(getContext(),"token",""),matrial_id,postion);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeListAdapter =new HomeListAdapter(getContext());
        homeListAdapter.setMatrialArrayList(matrialArrayList);
    }





    @Override
    public void onResume() {
        super.onResume();
        getMaterialListDao.getMaterialList("shuweineng888", AppHolder.getInstance().getToken(),type);
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
        getMaterialListDao.getMaterialList("shuweineng888", AppHolder.getInstance().getToken(),type);
    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onRequestSuccess(int requestCode) {
        super.onRequestSuccess(requestCode);
        if(requestCode== RequestCode.CODE_0){
            swipeRefreshLayout.setRefreshing(false);
            matrialArrayList = (ArrayList<Matrial>) getMaterialListDao.getMatrials();
            homeListAdapter.setMatrialArrayList(matrialArrayList);
        }
        if(requestCode==RequestCode.CODE_5){
           matrialArrayList.get(addMaterialFavoriteCountDao.getPostion()).setFavorite_count(""+addMaterialFavoriteCountDao.getFavorite_count());
            homeListAdapter.notifyDataSetChanged();
        }

    }
}
