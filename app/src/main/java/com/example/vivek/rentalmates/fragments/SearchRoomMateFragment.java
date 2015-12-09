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
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppData;

public class SearchRoomMateFragment extends android.support.v4.app.Fragment {

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView seekersTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_search_room_mate, container, false);

        seekersTextView = (TextView) layout.findViewById(R.id.seekersTextView);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listSeekers);
        //availableFlatListViewAdapter = new AvailableFlatListViewAdapter(getActivity());
        //recyclerView.setAdapter(availableFlatListViewAdapter);
        //recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    }

    private void onSwipeRefresh() {

    }
}
