package com.example.vivek.rentalmates.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FlatSearchActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.adapters.AvailableFlatListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.tasks.SearchFlatsForRentAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SearchFlatFragment extends android.support.v4.app.Fragment implements MainTabActivity.ActivityEventReceiver {
    private static final int FLAT_SEARCH_CRITERIA = 1;

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView availableFlatsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AvailableFlatListViewAdapter availableFlatListViewAdapter;
    private LinearLayout searchCriteriaLayout;
    private MainTabActivity mainTabActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        mainTabActivity = (MainTabActivity) getActivity();
        mainTabActivity.registerForActivityEvents("searchFlatsFragment", this);
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
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
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

        //Initialize SearchCriteriaLayout
        searchCriteriaLayout = (LinearLayout) layout.findViewById(R.id.searchCriteriaLayout);
        searchCriteriaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFlats();
            }
        });
        if (!appData.getFlatSearchCriteria().getFilterResetDone()) {
            searchCriteriaLayout.setVisibility(View.INVISIBLE);
        }

        updateView();
        return layout;
    }

    private void searchFlats() {
        Intent intent = new Intent(context, FlatSearchActivity.class);
        startActivityForResult(intent, FLAT_SEARCH_CRITERIA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLAT_SEARCH_CRITERIA) {
            if (resultCode == Activity.RESULT_OK) {
                searchCriteriaLayout.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(true);
                onSwipeRefresh();
            }
        }
    }

    public void updateView() {
        if (appData.getAvailableFlats().size() == 0) {
            availableFlatsTextView.setVisibility(View.VISIBLE);
        } else {
            availableFlatsTextView.setVisibility(View.GONE);
        }
    }

    public void onSwipeRefresh() {
        SearchFlatsForRentAsyncTask task = new SearchFlatsForRentAsyncTask(context, appData.getFlatSearchCriteria());
        task.setOnExecuteTaskReceiver(new SearchFlatsForRentAsyncTask.OnExecuteTaskReceiver() {
            @Override
            public void onTaskCompleted(List<FlatInfo> flats) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (flats == null) {
                    appData.storeAvailableFlatInfoList(getActivity(), new ArrayList<FlatInfo>());
                    Toast.makeText(context, "No Matching Flats Found", Toast.LENGTH_SHORT).show();
                } else {
                    appData.storeAvailableFlatInfoList(getActivity(), flats);
                    Toast.makeText(context, flats.size() + " Flats found", Toast.LENGTH_SHORT).show();
                }
                availableFlatListViewAdapter.updateAvailableFlatsData();
                availableFlatListViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onTaskFailed() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }

    @Override
    public void onEventReceived(String eventType) {
        switch (eventType) {
            case "filterFlatsFABPressed":
                searchFlats();
                break;
            default:
                break;
        }
    }
}
