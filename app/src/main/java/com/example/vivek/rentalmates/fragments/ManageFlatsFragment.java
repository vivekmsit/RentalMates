package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.FlatListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.tasks.GetFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.viewholders.FlatListItem;

import java.util.ArrayList;
import java.util.List;

public class ManageFlatsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ManageFlats_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlatListViewAdapter flatListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_manage_flats, container, false);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listFlats);
        flatListViewAdapter = new FlatListViewAdapter(context, getData());
        recyclerView.setAdapter(flatListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListFlats);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);
        return layout;
    }


    public List<FlatListItem> getData() {
        Log.d(TAG, "inside getData");
        List<FlatListItem> mItems = new ArrayList<>();
        if (appData.getFlats() == null) {
            LocalFlatInfo data = new LocalFlatInfo();
            data.setFlatName("ss");
            mItems.add(new FlatListItem(data));
        } else {
            for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
                mItems.add(new FlatListItem(localFlatInfo));
            }
        }
        return mItems;
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetFlatInfoListAsyncTask task = new GetFlatInfoListAsyncTask(context);
        task.setOnFlatInfoListReceiver(new OnFlatInfoListReceiver() {
            @Override
            public void onFlatInfoListLoadSuccessful(List<FlatInfo> flats) {
                Log.d(TAG, "inside onFlatInfoListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                flatListViewAdapter.setData(getData());
                flatListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFlatInfoListLoadFailed() {
                Log.d(TAG, "inside onFlatInfoListLoadFailed");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
