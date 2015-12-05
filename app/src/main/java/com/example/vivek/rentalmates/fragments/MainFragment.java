package com.example.vivek.rentalmates.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.dialogs.ItemPickerDialogFragment;
import com.example.vivek.rentalmates.library.RegisterNewFlatTask;
import com.example.vivek.rentalmates.tasks.PostYourFlatAsyncTask;

import java.util.ArrayList;

public class MainFragment extends android.support.v4.app.Fragment implements MainTabActivity.ActivityEventReceiver {

    private static final String TAG = "MainFragment_Debug";

    CardView sharedContactsCardView;
    CardView expenseManagerCardView;
    CardView flatRulesCardView;
    CardView postYourFlatCardView;
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

        sharedContactsCardView = (CardView) layout.findViewById(R.id.shared_contacts_card_view);
        expenseManagerCardView = (CardView) layout.findViewById(R.id.expense_manager_card_view);
        flatRulesCardView = (CardView) layout.findViewById(R.id.flat_rules_card_view);
        postYourFlatCardView = (CardView) layout.findViewById(R.id.post_your_flat_card_view);

        sharedContactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainTabActivity.OnFragmentTransactionRequest("SharedContacts");
            }
        });

        expenseManagerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainTabActivity.OnFragmentTransactionRequest("ExpenseManager");
            }
        });

        flatRulesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        postYourFlatCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postYourFlat();
            }
        });

        mainTabActivity.registerForActivityEvents("mainfragment", this);

        return layout;
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
        RegisterNewFlatTask registerNewFlatTask = new RegisterNewFlatTask(getActivity(), getFragmentManager(), "POST");
        registerNewFlatTask.setOnRegisterNewFlatTask(new RegisterNewFlatTask.OnRegisterNewFlatTask() {
            @Override
            public void onRegisterNewFlatTaskSuccess(com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo newFlatInfo) {

            }

            @Override
            public void onRegisterNewFlatTaskFailed() {

            }
        });
        registerNewFlatTask.execute();
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
}
