package com.example.vivek.rentalmates.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import com.example.vivek.rentalmates.adapters.FlatListViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.interfaces.OnRequestJoinExistingEntityReceiver;
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
        DialogFragment joinExistingFlatDialog = new android.support.v4.app.DialogFragment() {
            private ProgressDialog progressDialog;
            private EditText flatNameEditText;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_fragment_join_existing_flat, null);
                flatNameEditText = (EditText) view.findViewById(R.id.flatNameEditText);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Enter flat name");
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setPositiveButton("Join Flat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestAsyncTask task = new RequestAsyncTask(context, "FlatInfo", flatNameEditText.getText().toString());
                        task.setOnRequestJoinExistingEntityReceiver(new OnRequestJoinExistingEntityReceiver() {
                            @Override
                            public void onRequestJoinExistingEntitySuccessful(Request request) {
                                progressDialog.cancel();
                                if (request.getStatus().equals("PENDING")) {
                                    Toast.makeText(context, "Request sent to owner of the Flat", Toast.LENGTH_LONG).show();
                                } else if (request.getStatus().equals("ENTITY_NOT_AVAILABLE")) {
                                    Toast.makeText(context, "Flat with given name doesn't exist.\nPlease enter different name", Toast.LENGTH_LONG).show();
                                } else if (request.getStatus().equals("ALREADY_MEMBER")) {
                                    Toast.makeText(context, "You are already member of " + flatNameEditText.getText().toString(), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context, "Failed Request due to Unknown Reason", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onRequestJoinExistingEntityFailed() {
                                progressDialog.cancel();
                            }
                        });
                        task.execute();
                        progressDialog.setMessage("Requesting for Register with flat " + flatNameEditText.getText().toString());
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
        joinExistingFlatDialog.show(getFragmentManager(), "Fragment");
    }

    public void registerNewFlat() {
        DialogFragment registerNewFlatDialog = new android.support.v4.app.DialogFragment() {
            private ProgressDialog progressDialog;
            private EditText flatNameEditText;
            private EditText addressEditText;
            private EditText cityEditText;
            private EditText totalSecurityAmount;
            private EditText totalRentAmount;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_fragment_register_new_flat, null);

                flatNameEditText = (EditText) view.findViewById(R.id.flatNameEditText);
                addressEditText = (EditText) view.findViewById(R.id.addressEditText);
                cityEditText = (EditText) view.findViewById(R.id.cityEditText);
                totalSecurityAmount = (EditText) view.findViewById(R.id.securityAmountEditText);
                totalRentAmount = (EditText) view.findViewById(R.id.rentAmountEditText);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Fill New Flat Details");
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setPositiveButton("Register New Flat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo flatInfo = new com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo();
                        
                        flatInfo.setFlatName(flatNameEditText.getText().toString());
                        flatInfo.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
                        flatInfo.setOwnerId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
                        flatInfo.setFlatAddress(addressEditText.getText().toString());
                        flatInfo.setCity(cityEditText.getText().toString());
                        flatInfo.setRentAmount(Integer.parseInt(totalRentAmount.getText().toString()));
                        flatInfo.setSecurityAmount(Integer.parseInt(totalSecurityAmount.getText().toString()));

                        RegisterNewFlatAsyncTask task = new RegisterNewFlatAsyncTask(context, flatInfo);
                        task.setOnRegisterNewFlatReceiver(new OnRegisterNewFlatReceiver() {
                            @Override
                            public void onRegisterNewFlatSuccessful(com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo flatInfo) {
                                //registerButtonClicked = false;
                                progressDialog.cancel();
                                if (flatInfo == null) {
                                    Toast.makeText(context, "Flat with given name already registered. \n Please enter different name", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                Log.d(TAG, "FlatInfo uploaded");
                                Toast.makeText(context, "New Flat Registered", Toast.LENGTH_SHORT).show();

                                //getCompleteUserInformation();
                            }

                            @Override
                            public void onRegisterNewFlatFailed() {
                                //registerButtonClicked = false;
                                progressDialog.cancel();
                            }
                        });
                        task.execute();
                        progressDialog.setMessage("Registering new flat");
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
        registerNewFlatDialog.show(getFragmentManager(), "Fragment");
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
