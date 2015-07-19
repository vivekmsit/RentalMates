package com.example.vivek.rentalmates.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.AvailableFlatListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnAvailableFlatInfoListReceiver;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.tasks.GetAvailableFlatInfoListAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SearchFlatFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "AFlatList_Debug";

    private AppData appData;
    private RecyclerView recyclerView;
    private TextView availableFlatsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AvailableFlatListViewAdapter availableFlatListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search_flat, container, false);

        availableFlatsTextView = (TextView) layout.findViewById(R.id.availableFlatsText);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listAvailableFlats);
        availableFlatListViewAdapter = new AvailableFlatListViewAdapter(getActivity());
        recyclerView.setAdapter(availableFlatListViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeAvailableFlats);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onSwipeRefresh();
            }
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.purple);
        swipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        updateView();
        return layout;
    }

    public void updateView() {
        if (appData.getAvailableFlats().size() == 0) {
            availableFlatsTextView.setVisibility(View.VISIBLE);
        } else {
            availableFlatsTextView.setVisibility(View.GONE);
        }
    }

    public void onSwipeRefresh() {
        GetAvailableFlatInfoListAsyncTask task = new GetAvailableFlatInfoListAsyncTask(getActivity());
        task.setOnAvailableFlatInfoListReceiver(new OnAvailableFlatInfoListReceiver() {
            @Override
            public void onAvailableFlatInfoListLoadSuccessful(List<FlatInfo> flats) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (flats == null) {
                    appData.storeAvailableFlatInfoList(getActivity(), new ArrayList<FlatInfo>());
                } else {
                    appData.storeAvailableFlatInfoList(getActivity(), flats);
                }
                availableFlatListViewAdapter.updateAvailableFlatsData();
                availableFlatListViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onAvailableFlatInfoListLoadFailed() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
