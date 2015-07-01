package com.example.vivek.rentalmates.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.AvailableFlatListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnAvailableFlatInfoListReceiver;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.tasks.GetAvailableFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.viewholders.AvailableFlatListItem;

import java.util.ArrayList;
import java.util.List;

public class SearchFlatFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "AFlatList_Debug";

    private AppData appData;
    private RecyclerView recyclerView;
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

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listAvailableFlats);
        availableFlatListViewAdapter = new AvailableFlatListViewAdapter(getActivity(), getData());
        recyclerView.setAdapter(availableFlatListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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
        return layout;
    }

    public List<AvailableFlatListItem> getData() {
        Log.d(TAG, "inside getData");
        List<AvailableFlatListItem> mItems = new ArrayList<>();
        if (appData.getAvailableFlats().size() == 0) {
            LocalFlatInfo data = new LocalFlatInfo();
            data.setFlatName("Flat Name");
            data.setAddress("Unknown Address");
            data.setSecurityAmount(1);
            data.setRentAmount(1);
            mItems.add(new AvailableFlatListItem(data));
        } else {
            for (LocalFlatInfo flatInfo : appData.getAvailableFlats().values()) {
                mItems.add(new AvailableFlatListItem(flatInfo));
            }
        }
        return mItems;
    }

    public void onSwipeRefresh() {
        GetAvailableFlatInfoListAsyncTask task = new GetAvailableFlatInfoListAsyncTask(getActivity());
        task.setOnAvailableFlatInfoListReceiver(new OnAvailableFlatInfoListReceiver() {
            @Override
            public void onAvailableFlatInfoListLoadSuccessful(List<FlatInfo> flats) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (flats != null) {
                    appData.storeAvailableFlatInfoList(getActivity(), flats);
                    availableFlatListViewAdapter.setData(getData());
                    availableFlatListViewAdapter.notifyDataSetChanged();
                }
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
