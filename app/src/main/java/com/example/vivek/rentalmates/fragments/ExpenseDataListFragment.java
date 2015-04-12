package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.adapters.DrawerListViewAdapter;
import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.interfaces.ExpenseDataListLoadedListener;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.tasks.GetExpenseDataListAsyncTask;
import com.example.vivek.rentalmates.viewholders.ExpenseListItem;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDataListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ExpenseDataListLoadedListener {

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

        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListExpenses);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) layout.findViewById(R.id.listExpenses);
        expenseListViewAdapter = new ExpenseListViewAdapter(getActivity(), getData());
        recyclerView.setAdapter(expenseListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefs = getActivity().getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
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
        GetExpenseDataListAsyncTask task = new GetExpenseDataListAsyncTask(context, flatId, false);
        task.loadedListener = this;
        task.execute();
    }

    @Override
    public void onExpenseDataListLoaded() {
        Log.d(TAG, "inside onExpenseDataListLoaded");
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        expenseListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExpenseDataListLoadFailed() {
        Log.d(TAG, "inside onExpenseDataListLoadFailed");
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
