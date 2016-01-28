package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.adapters.AvailableRoomMateListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.tasks.SearchRoomMatesAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SearchRoomMateFragment extends android.support.v4.app.Fragment implements MainTabActivity.ActivityEventReceiver {

    private AppData appData;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView seekersTextView;
    private RecyclerView recyclerView;
    private MainTabActivity mainTabActivity;
    private Spinner toolbarSpinner;
    private Long currentFlatId;
    private List<LocalFlatInfo> localFlats;
    AvailableRoomMateListViewAdapter availableRoomMateListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localFlats = new ArrayList<>();
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
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

        //Initialize Spinner
        localFlats.clear();
        toolbarSpinner = (Spinner) layout.findViewById(R.id.flatNamesSpinner);
        for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
            localFlats.add(localFlatInfo);
        }
        List<String> flatNames = new ArrayList<>();
        for (LocalFlatInfo flat : localFlats) {
            flatNames.add(flat.getFlatName());
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(context, R.layout.flat_manager_toolbar_spinner_item, flatNames);
        toolbarSpinner.setAdapter(stringArrayAdapter);
        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalFlatInfo flatInfo = localFlats.get(position);
                currentFlatId = flatInfo.getFlatId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

    private void customRoommateSearch() {
        Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
    }

    private void onSwipeRefresh() {
        SearchRoomMatesAsyncTask task = new SearchRoomMatesAsyncTask(context, currentFlatId);
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
                customRoommateSearch();
                break;
            default:
                break;
        }
    }
}
