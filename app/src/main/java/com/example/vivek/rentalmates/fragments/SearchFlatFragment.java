package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.adapters.AvailableFlatListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.dialogs.FlatSearchCriteriaDialog;
import com.example.vivek.rentalmates.tasks.SearchFlatsForRentAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SearchFlatFragment extends android.support.v4.app.Fragment implements MainTabActivity.ActivityEventReceiver {

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView availableFlatsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AvailableFlatListViewAdapter availableFlatListViewAdapter;
    private Button searchCriteriaButton;
    private MainTabActivity mainTabActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        mainTabActivity = (MainTabActivity) getActivity();
        mainTabActivity.registerForActivityEvents("searchflatsfragment", this);
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

        //Initialize SearchCriteriaButton
        searchCriteriaButton = (Button) layout.findViewById(R.id.searchCriteriaButton);
        searchCriteriaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFlats();
            }
        });

        updateView();
        return layout;
    }

    public void searchFlats() {
        FlatSearchCriteriaDialog dialog = new FlatSearchCriteriaDialog();
        dialog.setOnDialogResultListener(new FlatSearchCriteriaDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult(FlatSearchCriteria flatSearchCriteria) {
                swipeRefreshLayout.setRefreshing(true);
                appData.storeFlatSearchCriteria(context, flatSearchCriteria);
                onSwipeRefresh();
            }

            @Override
            public void onNegativeResult() {

            }
        });
        dialog.show(getFragmentManager(), "fragment");
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
            case "filterFABPressed":
                searchFlats();
                break;
            default:
                break;
        }
    }
}
