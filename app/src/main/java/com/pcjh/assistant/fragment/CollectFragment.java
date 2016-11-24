package com.pcjh.assistant.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.pcjh.assistant.adapter.HomeListAdapter;
import com.pcjh.assistant.base.BaseLoadMoreListFragment;
import com.pcjh.assistant.db.DbManager;
import com.pcjh.assistant.entity.Matrial;

import java.util.ArrayList;

/**
 * Created by 单志华 on 2016/10/29.
 */
public class CollectFragment extends BaseLoadMoreListFragment   {

    private ArrayList<Matrial> matrialArrayList =new ArrayList<>() ;
    private String date ;
    private HomeListAdapter homeListAdapter ;
    private DbManager  dbManager;

    public void setDate(String date) {
        this.date = date;
        matrialArrayList.clear();
        if(dbManager!=null){
        matrialArrayList= (ArrayList<Matrial>) dbManager.queryCollectMatrials(date);
            Log.i("szhua",matrialArrayList.toString());
        if(homeListAdapter!=null)
          homeListAdapter.setMatrialArrayList(matrialArrayList);
        }else{
            Log.i("szhua","null");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeListAdapter =new HomeListAdapter(getContext());
         dbManager =new DbManager(getContext()) ;
        matrialArrayList = (ArrayList<Matrial>) dbManager.queryCollectMatrials();
        Log.i("szhua",matrialArrayList.toString());
        homeListAdapter.setMatrialArrayList(matrialArrayList);
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
        matrialArrayList.clear();
        DbManager dbManager =new DbManager(getContext()) ;
        if(!TextUtils.isEmpty(date)){
        matrialArrayList= (ArrayList<Matrial>) dbManager.queryCollectMatrials(date);}else{
            matrialArrayList= (ArrayList<Matrial>) dbManager.queryCollectMatrials();
        }
        if(homeListAdapter!=null)
            homeListAdapter.setMatrialArrayList(matrialArrayList);
        swipeRefreshLayout.setRefreshing(false);
    }
    @Override
    public void onItemClick(int position) {

    }
    /**
     * when the date is changed:
     * we refresh the all datas ;
     * @param date
     */
    public void dateChanged(String date) {
        Log.i("szhua",date);
        setDate(date);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();
    }
}
