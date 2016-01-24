package com.example.vivek.rentalmates.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.ExpenseManagerActivity;
import com.example.vivek.rentalmates.activities.FlatManagerActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.activities.NewFlatActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.dialogs.FlatSearchCriteriaDialog;
import com.example.vivek.rentalmates.dialogs.ItemPickerDialogFragment;
import com.example.vivek.rentalmates.tasks.PostYourFlatAsyncTask;
import com.example.vivek.rentalmates.tasks.PostYourRoomRequirementAsyncTask;

import java.util.ArrayList;

public class MainFragment extends android.support.v4.app.Fragment implements MainTabActivity.ActivityEventReceiver {

    //private static final String TAG = "MainFragment_Debug";
    private static final int REGISTER_NEW_FLAT = 1;

    CardView flatManagerCardView;
    CardView expenseManagerCardView;
    CardView postYourFlatCardView;
    CardView postYourRoomRequirementCardView;
    MainTabActivity mainTabActivity;
    Context context;
    AppData appData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        mainTabActivity = (MainTabActivity) getActivity();
        context = getActivity().getApplicationContext();
        appData = AppData.getInstance();

        flatManagerCardView = (CardView) layout.findViewById(R.id.flatManagerCardView);
        expenseManagerCardView = (CardView) layout.findViewById(R.id.expense_manager_card_view);
        postYourFlatCardView = (CardView) layout.findViewById(R.id.post_your_flat_card_view);
        postYourRoomRequirementCardView = (CardView) layout.findViewById(R.id.post_your_room_requirement_card_view);

        flatManagerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchFlatManager();
            }
        });

        expenseManagerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchExpenseManager();
            }
        });

        postYourFlatCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postYourFlat();
            }
        });

        postYourRoomRequirementCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postYourRoomRequirement();
            }
        });

        mainTabActivity.registerForActivityEvents("mainfragment", this);

        return layout;
    }

    private void launchFlatManager() {
        Intent intent = new Intent(context, FlatManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void launchExpenseManager() {
        Intent intent = new Intent(context, ExpenseManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void postYourFlat() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<>();
        for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
            pickerItems.add(new ItemPickerDialogFragment.Item(localFlatInfo.getFlatName(), localFlatInfo.getFlatId()));
        }

        ItemPickerDialogFragment dialog = ItemPickerDialogFragment.newInstance("Select flat", "Create New Flat", pickerItems, -1);
        dialog.setOnDialogResultListener(new ItemPickerDialogFragment.OnDialogResultListener() {
            @Override
            public void onPositiveResult(Long flatId) {
                PostYourFlatAsyncTask task = new PostYourFlatAsyncTask(context, flatId);
                task.setAsyncTaskReceiver(new PostYourFlatAsyncTask.AsyncTaskReceiver() {
                    @Override
                    public void onAsyncTaskComplete() {
                        progressDialog.cancel();
                        Toast.makeText(context, "Flat posted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAsyncTaskFailed() {
                        progressDialog.cancel();
                        Toast.makeText(context, "Failed to post Flat", Toast.LENGTH_SHORT).show();
                    }
                });
                task.execute();
                progressDialog.setMessage("Posting your flat");
                progressDialog.show();
            }

            @Override
            public void onNeutralButtonResult() {
                postNewFlat();
            }

            @Override
            public void onNegativeResult() {
            }
        });
        dialog.show(getFragmentManager(), "ItemPicker");
    }

    private void postNewFlat() {
        Intent intent = new Intent(context, NewFlatActivity.class);
        startActivityForResult(intent, REGISTER_NEW_FLAT);
    }

    private void postYourRoomRequirement() {
        FlatSearchCriteriaDialog dialog = new FlatSearchCriteriaDialog();
        dialog.setOnDialogResultListener(new FlatSearchCriteriaDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult(FlatSearchCriteria flatSearchCriteria) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                PostYourRoomRequirementAsyncTask task = new PostYourRoomRequirementAsyncTask(context, flatSearchCriteria);
                task.setOnExecuteTaskReceiver(new PostYourRoomRequirementAsyncTask.OnExecuteTaskReceiver() {
                    @Override
                    public void onTaskCompleted(FlatSearchCriteria uploadedFlatSearchCriteria) {
                        progressDialog.cancel();
                        Toast.makeText(context, "Your Room Requirement Uploaded", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTaskFailed() {
                        progressDialog.cancel();
                    }
                });
                task.execute();
                progressDialog.setMessage("posting your room requirement");
                progressDialog.show();
            }

            @Override
            public void onNegativeResult() {

            }
        });
        dialog.show(getFragmentManager(), "fragment");
    }

    @Override
    public void onEventReceived(String eventType) {
        switch (eventType) {
            case "addFABPressed":
                Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
