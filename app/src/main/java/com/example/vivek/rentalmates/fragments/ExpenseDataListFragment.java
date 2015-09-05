package com.example.vivek.rentalmates.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ExpenseListViewAdapter;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalExpenseGroup;
import com.example.vivek.rentalmates.interfaces.OnRequestJoinExistingEntityReceiver;
import com.example.vivek.rentalmates.tasks.GetAllExpenseListAsyncTask;
import com.example.vivek.rentalmates.tasks.RequestAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpenseDataListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ExpenseList_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private ExpenseListViewAdapter expenseListViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView payBackAmountView;
    private TextView expensesTextView;
    private TextView expenseGroupNameView;
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

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_expenses, container, false);

        //Initialize Payback TextView
        payBackAmountView = (TextView) layout.findViewById(R.id.paybackTextView);
        Long expenseGroupId = prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0);
        if (expenseGroupId != 0) {
            LocalExpenseGroup expenseGroup = appData.getLocalExpenseGroup(expenseGroupId);
            payBackAmountView.setText("Payback Amount: Rs. " + expenseGroup.getMembersData().get(prefs.getLong(AppConstants.USER_PROFILE_ID, 0)));
        } else {
            payBackAmountView.setText("Payback Amount: NA");
        }
        payBackAmountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDistributionDialog();
            }
        });

        //Initialize expense Group Text View
        expenseGroupNameView = (TextView) layout.findViewById(R.id.expenseGroupName);
        expenseGroupNameView.setText(prefs.getString(AppConstants.PRIMARY_FLAT_NAME, "no_expense_group"));

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

    public void openDistributionDialog() {
        DialogFragment expenseDistributionDialog = new android.support.v4.app.DialogFragment() {
            private ProgressDialog progressDialog;
            private TextView expenseDistributionTextView;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_expense_distribution, null);
                expenseDistributionTextView = (TextView) view.findViewById(R.id.expenseDistributionTextView);

                LocalExpenseGroup localExpenseGroup = appData.getLocalExpenseGroup(prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0));
                Map<Long, Long> membersData = localExpenseGroup.getMembersData();
                Set<Long> memberIds = localExpenseGroup.getMembersData().keySet();
                String finalData = "";
                for (Long memberId : memberIds) {
                    String name = appData.getLocalUserProfile(memberId).getUserName();
                    Long share = membersData.get(memberId);
                    if (share > 0) {
                        finalData = finalData + "\n" + name + " will pay Rs. " + share.toString() + " to others";
                    } else if (share < 0) {
                        finalData = finalData + "\n" + name + " will get Rs. " + share.toString() + " from others";
                    } else {
                        finalData = finalData + "\n" + name + " will not pay to anyone";
                    }
                }
                expenseDistributionTextView.setText(finalData);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Expense Distribution");
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                return alertDialogBuilder.create();
            }
        };
        expenseDistributionDialog.show(getFragmentManager(), "Fragment");
    }
}
