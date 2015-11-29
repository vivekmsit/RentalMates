package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.dialogs.ItemPickerDialogFragment;

import java.util.ArrayList;

public class MainFragment extends android.support.v4.app.Fragment {

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

        return layout;
    }

    private void postYourFlat() {
        ArrayList<ItemPickerDialogFragment.Item> pickerItems = new ArrayList<>();
        for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
            pickerItems.add(new ItemPickerDialogFragment.Item(localFlatInfo.getFlatName(), localFlatInfo.getAddress()));
        }

        ItemPickerDialogFragment dialog = ItemPickerDialogFragment.newInstance("Select flat", "Create New Flat", pickerItems, -1);
        dialog.setOnDialogResultListener(new ItemPickerDialogFragment.OnDialogResultListener() {
            @Override
            public void onPositiveResult(String flatName) {
                Toast.makeText(context, "selected flat: " + flatName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNeutralButtonResult() {
                Toast.makeText(context, "New Flat To be Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNegativeResult() {

            }
        });
        dialog.show(getFragmentManager(), "ItemPicker");
    }
}
