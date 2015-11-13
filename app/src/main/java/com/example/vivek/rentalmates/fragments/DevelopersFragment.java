package com.example.vivek.rentalmates.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FirstActivity;
import com.example.vivek.rentalmates.activities.TestActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnDeleteRemoteDataReceiver;
import com.example.vivek.rentalmates.tasks.DeleteRemoteDataAsyncTask;
import com.example.vivek.rentalmates.tasks.SearchFlatsForRentAsyncTask;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevelopersFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "ExpenseList_Debug";

    private Button deleteLocalDataButton;
    private Button deleteRemoteDataButton;
    private Button testButton;
    private AppData appData;
    private Context context;
    private SharedPreferences prefs;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "inside onCreate");
        super.onCreate(savedInstanceState);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        prefs = getActivity().getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_developers, container, false);

        deleteLocalDataButton = (Button) layout.findViewById(R.id.deleteLocalDataButton);
        deleteLocalDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLocalData();
            }
        });

        deleteRemoteDataButton = (Button) layout.findViewById(R.id.deleteRemoteDataButton);
        deleteRemoteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRemoteData();
            }
        });

        testButton = (Button) layout.findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestButtonClicked();
            }
        });

        return layout;
    }

    public void deleteLocalData() {
        //clear SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        //Reset AppData contents
        appData.clearAppData(context);

        //Start FirstActivity
        Intent intent = new Intent(context, FirstActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void deleteRemoteData() {
        DialogFragment deleteRemoteDataDialog = new DialogFragment() {

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Confirm");
                alertDialogBuilder.setMessage("Do you really want to delete remote data?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteRemoteDataAsyncTask task = new DeleteRemoteDataAsyncTask(context);
                        task.setOnDeleteRemoteDataReceiver(new OnDeleteRemoteDataReceiver() {
                            @Override
                            public void onDeleteRemoteDataSuccessful() {
                                progressDialog.cancel();
                                Toast.makeText(context, "Remote Data Deleted Successfully", Toast.LENGTH_SHORT).show();
                                deleteLocalData();
                            }

                            @Override
                            public void onDeleteRemoteDataFailed() {
                                progressDialog.cancel();
                            }
                        });
                        task.execute();
                        progressDialog.setMessage("Deleting Remote Data ");
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
        deleteRemoteDataDialog.show(getFragmentManager(), "DeleteRemoteDataDialog");
    }

    public void onTestButtonClicked() {
        Toast.makeText(context, "To be implemented", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), TestActivity.class);
        startActivity(intent);
    }
}

