package com.example.vivek.rentalmates.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ExpenseGroupListViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRequestJoinExistingEntityReceiver;
import com.example.vivek.rentalmates.tasks.GetExpenseGroupListAsyncTask;
import com.example.vivek.rentalmates.tasks.RequestAsyncTask;

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
        DialogFragment joinExistingExpenseGroupDialog = new android.support.v4.app.DialogFragment() {
            private ProgressDialog progressDialog;
            private EditText expenseGroupNameEditText;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_fragment_join_existing_expense_group, null);
                expenseGroupNameEditText = (EditText) view.findViewById(R.id.expenseGroupNameEditText);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Enter Expense Group name");
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setPositiveButton("Join Expense Group", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestAsyncTask task = new RequestAsyncTask(context, "ExpenseGroup", expenseGroupNameEditText.getText().toString());
                        task.setOnRequestJoinExistingEntityReceiver(new OnRequestJoinExistingEntityReceiver() {
                            @Override
                            public void onRequestJoinExistingEntitySuccessful(Request request) {
                                progressDialog.cancel();
                                if (request.getStatus().equals("PENDING")) {
                                    Toast.makeText(context, "Request sent to owner of the Expense Group", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Expense Group with given name doesn't exist.\nPlease enter different name", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onRequestJoinExistingEntityFailed() {
                                progressDialog.cancel();
                            }
                        });
                        task.execute();
                        progressDialog.setMessage("Requesting for Register with flat " + expenseGroupNameEditText.getText().toString());
                        progressDialog.show();
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
        joinExistingExpenseGroupDialog.show(getFragmentManager(), "Fragment");
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
