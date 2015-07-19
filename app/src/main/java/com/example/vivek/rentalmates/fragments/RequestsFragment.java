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
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.RequestListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnRequestListReceiver;
import com.example.vivek.rentalmates.tasks.GetRequestListAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ManageFlats_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView requestTextView;
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
        requestListViewAdapter = new RequestListViewAdapter(context, getFragmentManager());
        recyclerView.setAdapter(requestListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListRequests);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);

        requestTextView = (TextView) layout.findViewById(R.id.requestsText);
        updateView();
        return layout;
    }

    void updateView() {
        if (appData.getRequests().size() == 0) {
            requestTextView.setVisibility(View.VISIBLE);
        } else {
            requestTextView.setVisibility(View.GONE);
        }
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
                if (requests == null) {
                    appData.storeRequestList(context, new ArrayList<Request>());
                } else {
                    appData.storeRequestList(context, requests);
                }
                requestListViewAdapter.updateRequestData();
                requestListViewAdapter.notifyDataSetChanged();
                updateView();
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
