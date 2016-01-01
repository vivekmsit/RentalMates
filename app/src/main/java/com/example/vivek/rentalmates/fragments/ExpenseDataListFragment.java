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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FlatManagerActivity;
import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ExpenseDataListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ExpenseList_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private ExpenseListViewAdapter expenseListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView expensesTextView;
    private FlatManagerActivity flatManagerActivity;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        prefs = getActivity().getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");

        flatManagerActivity = (FlatManagerActivity) getActivity();
        flatManagerActivity.registerForActivityEvents("expensesFragment", new FlatManagerActivity.OnActivityEventReceiver() {
            @Override
            public void onEventReceived(String eventType) {
                switch (eventType) {
                    case "addFABPressed":
                        Toast.makeText(getActivity().getApplicationContext(), "To be implemented", Toast.LENGTH_SHORT).show();
                        break;
                    case "primaryFlatChanged":
                        break;
                    default:
                        break;
                }
            }
        });

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_expenses, container, false);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listExpenses);
        expenseListViewAdapter = new ExpenseListViewAdapter(getActivity(), getFragmentManager());
        recyclerView.setAdapter(expenseListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListExpenses);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.white);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.purple);
        swipeRefreshLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        expensesTextView = (TextView) layout.findViewById(R.id.expensesText);
        updateView();
        return layout;
    }

    void updateView() {
        if (appData.getExpenses().size() == 0) {
            expensesTextView.setVisibility(View.VISIBLE);
        } else {
            expensesTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if ((bundle != null) && (bundle.getInt("newExpenseAvailable", 0) == 1)) {
            Log.d(TAG, "new expense available");
            startSwipeRefreshLayout();
        }
    }


    public void startSwipeRefreshLayout() {
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetAllExpenseListAsyncTask task = new GetAllExpenseListAsyncTask(context);
        task.setOnExpenseListReceiver(new OnExpenseListReceiver() {
            @Override
            public void onExpenseDataListLoadSuccessful(List<ExpenseData> expenses) {
                Log.d(TAG, "inside onExpenseDataListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (expenses == null) {
                    appData.storeExpenseDataList(context, new ArrayList<ExpenseData>());
                } else {
                    appData.storeExpenseDataList(context, expenses);
                }
                expenseListViewAdapter.updateExpenseData();
                expenseListViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onExpenseDataListLoadFailed() {
                Log.d(TAG, "inside onExpenseDataListLoadFailed");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
