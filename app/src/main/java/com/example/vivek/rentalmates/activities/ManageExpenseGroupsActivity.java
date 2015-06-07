package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ExpenseGroupListViewAdapter;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalExpenseGroup;
import com.example.vivek.rentalmates.tasks.GetExpenseGroupListAsyncTask;
import com.example.vivek.rentalmates.viewholders.ExpenseGroupListItem;

import java.util.ArrayList;
import java.util.List;

public class ManageExpenseGroupsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ManageEGroups_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ExpenseGroupListViewAdapter expenseGroupListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_expense_groups);

        appData = AppData.getInstance();
        context = getApplicationContext();

        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.listExpenseGroups);
        expenseGroupListViewAdapter = new ExpenseGroupListViewAdapter(this, getData());
        recyclerView.setAdapter(expenseGroupListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeListExpenseGroups);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetExpenseGroupListAsyncTask task = new GetExpenseGroupListAsyncTask(context);
        task.setOnExpenseGroupListReceiver(new OnExpenseGroupListReceiver() {
            @Override
            public void onExpenseGroupListLoadSuccessful() {
                Log.d(TAG, "inside onFlatInfoListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                expenseGroupListViewAdapter.setData(getData());
                expenseGroupListViewAdapter.notifyDataSetChanged();
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

    public List<ExpenseGroupListItem> getData() {
        Log.d(TAG, "inside getData");
        List<ExpenseGroupListItem> mItems = new ArrayList<>();
        if (appData.getExpenseGroups() == null) {
            LocalExpenseGroup data = new LocalExpenseGroup();
            data.setName("name");
            mItems.add(new ExpenseGroupListItem(data));
        } else {
            for (LocalExpenseGroup localExpenseGroup : appData.getExpenseGroups()) {
                mItems.add(new ExpenseGroupListItem(localExpenseGroup));
            }
        }
        return mItems;
    }
}
