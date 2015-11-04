package com.example.vivek.rentalmates.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.vivek.rentalmates.adapters.FlatListViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.dialogs.GetExistingFlatInfoDialog;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.interfaces.OnRequestJoinExistingEntityReceiver;
import com.example.vivek.rentalmates.libraries.GetNewFlatInfoTask;
import com.example.vivek.rentalmates.tasks.GetFlatInfoListAsyncTask;
import com.example.vivek.rentalmates.tasks.RegisterNewFlatAsyncTask;
import com.example.vivek.rentalmates.tasks.RequestAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ManageFlatsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ManageFlats_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView manageFlatsTextView;
    private Button joinExistingFlatButton;
    private Button registerNewFlatButton;
    private SharedPreferences prefs;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlatListViewAdapter flatListViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_manage_flats, container, false);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();

        manageFlatsTextView = (TextView) layout.findViewById(R.id.manageFlatsText);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listFlats);
        flatListViewAdapter = new FlatListViewAdapter(context);
        recyclerView.setAdapter(flatListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListFlats);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);

        //Initialize Buttons
        joinExistingFlatButton = (Button) layout.findViewById(R.id.joinExistingFlatButton);
        registerNewFlatButton = (Button) layout.findViewById(R.id.registerNewFlatButton);
        joinExistingFlatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinExistingFlat();
            }
        });

        registerNewFlatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewFlat();
            }
        });

        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);

        updateView();
        return layout;
    }

    public void updateView() {
        if (appData.getFlats().size() == 0) {
            manageFlatsTextView.setVisibility(View.VISIBLE);
        } else {
            manageFlatsTextView.setVisibility(View.GONE);
        }
    }

    public void joinExistingFlat() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        GetExistingFlatInfoDialog dialog = new GetExistingFlatInfoDialog();
        dialog.setOnDialogResultListener(new GetExistingFlatInfoDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult(final String flatName, String ownerEmailId) {
                RequestAsyncTask task = new RequestAsyncTask(context, "FlatInfo", flatName, ownerEmailId);
                task.setOnRequestJoinExistingEntityReceiver(new OnRequestJoinExistingEntityReceiver() {
                    @Override
                    public void onRequestJoinExistingEntitySuccessful(Request request) {
                        progressDialog.cancel();
                        switch (request.getStatus()) {
                            case "PENDING":
                                Toast.makeText(context, "Request sent to owner of the Flat", Toast.LENGTH_LONG).show();
                                break;
                            case "ENTITY_NOT_AVAILABLE":
                                Toast.makeText(context, "Flat with given name doesn't exist.\nPlease enter different name", Toast.LENGTH_LONG).show();
                                break;
                            case "ALREADY_MEMBER":
                                Toast.makeText(context, "You are already member of " + flatName, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(context, "Failed request due to Unknown Reason", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }

                    @Override
                    public void onRequestJoinExistingEntityFailed() {
                        progressDialog.cancel();
                    }
                });
                task.execute();
                progressDialog.setMessage("Requesting for Register with flat " + flatName);
                progressDialog.show();
            }

            @Override
            public void onNegativeResult() {

            }
        });
        dialog.show(getFragmentManager(), "Fragment");
    }

    public void registerNewFlat() {
        GetNewFlatInfoTask getNewFlatInfoTask = new GetNewFlatInfoTask(context, getFragmentManager());
        getNewFlatInfoTask.setOnGetFlatInfoTask(new GetNewFlatInfoTask.OnGetFlatInfoTask() {
            @Override
            public void onRegisterNewFlatSuccessful(com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo newFlatInfo) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                RegisterNewFlatAsyncTask task = new RegisterNewFlatAsyncTask(context, newFlatInfo);
                task.setOnRegisterNewFlatReceiver(new OnRegisterNewFlatReceiver() {
                    @Override
                    public void onRegisterNewFlatSuccessful(com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo flatInfo) {
                        progressDialog.cancel();
                        if (flatInfo == null) {
                            Toast.makeText(context, "Flat with given name already registered. \n Please enter different name", Toast.LENGTH_LONG).show();
                            return;
                        }
                        Log.d(TAG, "FlatInfo uploaded");
                        Toast.makeText(context, "New Flat Registered", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(true);
                        onRefresh();
                    }

                    @Override
                    public void onRegisterNewFlatFailed() {
                        progressDialog.cancel();
                    }
                });
                task.execute();
                progressDialog.setMessage("Registering new flat");
                progressDialog.show();
            }

            @Override
            public void onRegisterNewFlatFailed() {

            }
        });
        getNewFlatInfoTask.execute();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetFlatInfoListAsyncTask task = new GetFlatInfoListAsyncTask(context);
        task.setOnFlatInfoListReceiver(new OnFlatInfoListReceiver() {
            @Override
            public void onFlatInfoListLoadSuccessful(List<FlatInfo> flats) {
                Log.d(TAG, "inside onFlatInfoListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (flats == null) {
                    appData.storeFlatInfoList(context, new ArrayList<FlatInfo>());
                } else {
                    appData.storeFlatInfoList(context, flats);
                }
                flatListViewAdapter.updateFlatData();
                flatListViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onFlatInfoListLoadFailed() {
                Log.d(TAG, "inside onFlatInfoListLoadFailed");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
