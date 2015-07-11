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
import com.example.vivek.rentalmates.adapters.RequestListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnRequestListReceiver;
import com.example.vivek.rentalmates.tasks.GetRequestListAsyncTask;
import com.example.vivek.rentalmates.viewholders.RequestListItem;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ManageFlats_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestListViewAdapter requestListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_requests, container, false);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listRequests);
        requestListViewAdapter = new RequestListViewAdapter(context, getData());
        recyclerView.setAdapter(requestListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListRequests);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);
        return layout;
    }


    public List<RequestListItem> getData() {
        Log.d(TAG, "inside getData");
        List<RequestListItem> mItems = new ArrayList<>();
        if (appData.getRequests() == null) {
            Request data = new Request();
            data.setRequestedEntityName("ss");
            mItems.add(new RequestListItem(data));
        } else {
            for (Request request : appData.getRequests()) {
                mItems.add(new RequestListItem(request));
            }
        }
        return mItems;
    }


    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetRequestListAsyncTask task = new GetRequestListAsyncTask(context);
        task.setOnRequestListReceiver(new OnRequestListReceiver() {
            @Override
            public void onRequestListLoadSuccessful(List<Request> requests) {
                Log.d(TAG, "inside onFlatInfoListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                appData.storeRequestList(context, requests);
                requestListViewAdapter.setData(getData());
                requestListViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRequestListLoadFailed() {
                Log.d(TAG, "inside onFlatInfoListLoadFailed");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
