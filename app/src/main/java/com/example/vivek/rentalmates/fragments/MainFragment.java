package com.example.vivek.rentalmates.fragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;

public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment_Debug";

    CardView sharedContactsCardView;
    CardView expenseManagerCardView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main, container, false);
        context = getActivity().getApplicationContext();

        sharedContactsCardView = (CardView) layout.findViewById(R.id.shared_contacts_card_view);
        expenseManagerCardView = (CardView) layout.findViewById(R.id.expense_manager_card_view);

        sharedContactsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        expenseManagerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            }
        });

        return layout;
    }

}
