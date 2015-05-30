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
import android.widget.Button;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.others.LocalUserProfile;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;
import com.example.vivek.rentalmates.viewholders.ExpenseListItem;
import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpenseDataListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ExpenseList_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private ExpenseListViewAdapter expenseListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView payBackAmountView;
    private TextView expenseGroupNameView;
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
        prefs = getActivity().getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreateView");

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_expenses, container, false);

        //Initialize Payback TextView
        payBackAmountView = (TextView) layout.findViewById(R.id.paybackTextView);
        LocalUserProfile userProfile = appData.getLocalUserProfile(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        payBackAmountView.setText("Payback Amount: Rs. " + userProfile.getPayback());

        //Initialize expense Group Text View
        expenseGroupNameView = (TextView) layout.findViewById(R.id.expenseGroupName);
        expenseGroupNameView.setText(prefs.getString(AppConstants.PRIMARY_FLAT_NAME, "no_expense_group"));

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


    public List<ExpenseListItem> getData() {
        Log.d(TAG, "inside getData");
        List<ExpenseListItem> mItems = new ArrayList<>();
        List<ExpenseData> expenses = appData.getExpenses();
        if (expenses == null) {
            ExpenseData data = new ExpenseData();
            data.setAmount(0);
            data.setDescription("Description");
            data.setOwnerEmailId("vivekmsit@gmail.com");
            data.setDate(new DateTime(new Date()));
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
        GetAllExpenseListAsyncTask task = new GetAllExpenseListAsyncTask(context);
        task.setOnExpenseListReceiver(new OnExpenseListReceiver() {
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
        });
        task.execute();
    }

    public void onExpenseDeleteSuccessful(int position) {
        expenseListViewAdapter.notifyItemRemoved(position);
        appData.deleteExpenseData(context, position);
        expenseListViewAdapter.setData(getData());
    }
}
