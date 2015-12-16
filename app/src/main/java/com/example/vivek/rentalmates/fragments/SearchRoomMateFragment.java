package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.adapters.AvailableRoomMateListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.tasks.SearchRoomMatesAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SearchRoomMateFragment extends android.support.v4.app.Fragment implements MainTabActivity.ActivityEventReceiver {

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView seekersTextView;
    private MainTabActivity mainTabActivity;
    private SharedPreferences prefs;
    AvailableRoomMateListViewAdapter availableRoomMateListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        mainTabActivity = (MainTabActivity) getActivity();
        mainTabActivity.registerForActivityEvents("searchRoomMatesFragment", this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search_room_mate, container, false);

        seekersTextView = (TextView) layout.findViewById(R.id.seekersTextView);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listSeekers);
        availableRoomMateListViewAdapter = new AvailableRoomMateListViewAdapter(getActivity());
        recyclerView.setAdapter(availableRoomMateListViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeSeekers);
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
        if (appData.getRoomMateList().size() == 0) {
            seekersTextView.setVisibility(View.VISIBLE);
        } else {
            seekersTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void searchRoomMates() {
        Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
    }

    private void onSwipeRefresh() {
        SearchRoomMatesAsyncTask task = new SearchRoomMatesAsyncTask(context, prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0));
        task.setOnExecuteTaskReceiver(new SearchRoomMatesAsyncTask.OnExecuteTaskReceiver() {
            @Override
            public void onTaskCompleted(List<FlatSearchCriteria> flatSearchCriteriaList) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (flatSearchCriteriaList == null) {
                    appData.storeRoomMateList(getActivity(), new ArrayList<FlatSearchCriteria>());
                    Toast.makeText(context, "No Matching RoomMates Found", Toast.LENGTH_SHORT).show();
                } else {
                    appData.storeRoomMateList(getActivity(), flatSearchCriteriaList);
                    Toast.makeText(context, flatSearchCriteriaList.size() + " RoomMates found", Toast.LENGTH_SHORT).show();
                }
                availableRoomMateListViewAdapter.updateAvailableFlatsData();
                availableRoomMateListViewAdapter.notifyDataSetChanged();
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
            case "filterRoomMatesFABPressed":
                searchRoomMates();
                break;
            default:
                break;
        }
    }
}
