package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.AddExpenseActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.interfaces.OnAllExpenseListLoadedReceiver;
import com.example.vivek.rentalmates.interfaces.OnDeleteExpenseReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;
import com.example.vivek.rentalmates.viewholders.ExpenseListItem;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDataListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnAllExpenseListLoadedReceiver, OnDeleteExpenseReceiver {

    private static final String TAG = "ExpenseList_Debug";

    AppData appData;
    Context context;
    private RecyclerView recyclerView;
    private ExpenseListViewAdapter expenseListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences prefs;

    public ExpenseDataListFragment() {
        Log.d(TAG, "inside constructor");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_expenses, container, false);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listExpenses);
        expenseListViewAdapter = new ExpenseListViewAdapter(getActivity(), getData(), getFragmentManager());
        recyclerView.setAdapter(expenseListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListExpenses);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);
        swipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        return layout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = getActivity().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        startSwipeRefreshLayout();
    }


    public void startSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }


    public List<ExpenseListItem> getData() {
        Log.d(TAG, "inside getData");
        List<ExpenseListItem> mItems = new ArrayList<>();
        List<ExpenseData> expenses = appData.getExpenses();
        if (expenses == null) {
            ExpenseData data = new ExpenseData();
            data.setAmount(0);
            data.setDescription("Description");
            data.setOwnerEmailId("vivekmsit@gmail.com");
            mItems.add(new ExpenseListItem(data));
        } else {
            for (ExpenseData expenseData : expenses) {
                mItems.add(new ExpenseListItem(expenseData));
            }
        }
        return mItems;
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        Long flatId = prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0);
        GetAllExpenseListAsyncTask task = new GetAllExpenseListAsyncTask(context, flatId, false);
        task.loadedReceiver = this;
        task.execute();
    }

    @Override
    public void onExpenseDataListLoadSuccessful() {
        Log.d(TAG, "inside onExpenseDataListLoaded");
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        expenseListViewAdapter.setData(getData());
        expenseListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExpenseDataListLoadFailed() {
        Log.d(TAG, "inside onExpenseDataListLoadFailed");
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onExpenseDeleteSuccessful(int position) {
        expenseListViewAdapter.notifyItemRemoved(position);
        appData.deleteExpenseData(context, position);
        expenseListViewAdapter.setData(getData());
    }

    @Override
    public void onExpenseDeleteFailed() {
        //Do nothing
    }
}
