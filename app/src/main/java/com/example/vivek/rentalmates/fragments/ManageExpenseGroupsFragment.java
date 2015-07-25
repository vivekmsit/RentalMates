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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ExpenseGroupListViewAdapter;
import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.tasks.GetExpenseGroupListAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ManageExpenseGroupsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ManageEGroups_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView manageExpenseGroupsTextView;
    private Button joinExistingEGroupButton;
    private Button registerNewEGroupButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpenseGroupListViewAdapter expenseGroupListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_manage_expense_groups, container, false);

        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();

        manageExpenseGroupsTextView = (TextView) layout.findViewById(R.id.manageExpenseGroupsText);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listExpenseGroups);
        expenseGroupListViewAdapter = new ExpenseGroupListViewAdapter(context);
        recyclerView.setAdapter(expenseGroupListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListExpenseGroups);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);

        //Initialize buttons
        joinExistingEGroupButton = (Button) layout.findViewById(R.id.joinExistingEGroupButton);
        registerNewEGroupButton = (Button) layout.findViewById(R.id.registerNewEGroupButton);

        joinExistingEGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinExistingExpenseGroup();
            }
        });

        registerNewEGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewExpenseGroup();
            }
        });

        updateView();
        return layout;
    }

    public void updateView() {
        if (appData.getExpenseGroups().size() == 0) {
            manageExpenseGroupsTextView.setVisibility(View.VISIBLE);
        } else {
            manageExpenseGroupsTextView.setVisibility(View.GONE);
        }
    }

    public void joinExistingExpenseGroup() {
        Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
    }

    public void registerNewExpenseGroup() {
        Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetExpenseGroupListAsyncTask task = new GetExpenseGroupListAsyncTask(context);
        task.setOnExpenseGroupListReceiver(new OnExpenseGroupListReceiver() {
            @Override
            public void onExpenseGroupListLoadSuccessful(List<ExpenseGroup> expenseGroups) {
                Log.d(TAG, "inside onFlatInfoListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (expenseGroups == null) {
                    appData.storeExpenseGroupList(context, new ArrayList<ExpenseGroup>());
                } else {
                    appData.storeExpenseGroupList(context, expenseGroups);
                }
                expenseGroupListViewAdapter.updateExpenseGroupsData();
                expenseGroupListViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onExpenseGroupListLoadFailed() {
                Log.d(TAG, "inside onFlatInfoListLoadFailed");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
