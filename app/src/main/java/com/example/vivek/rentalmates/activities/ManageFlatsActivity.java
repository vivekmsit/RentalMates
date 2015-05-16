package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.FlatListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalFlatInfo;
import com.example.vivek.rentalmates.tasks.GetFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.viewholders.FlatListItem;

import java.util.ArrayList;
import java.util.List;

public class ManageFlatsActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ManageFlats_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlatListViewAdapter flatListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_flats);

        appData = AppData.getInstance();
        context = getApplicationContext();

        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.listFlats);
        flatListViewAdapter = new FlatListViewAdapter(this, getData(), getSupportFragmentManager());
        recyclerView.setAdapter(flatListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeListFlats);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);
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
}
