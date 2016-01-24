package com.example.vivek.rentalmates.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFlatBasicInfoFragment extends Fragment {

    EditText flatNameEditText;
    EditText flatDescriptionEditText;
    EditText securityAmountEditText;
    EditText rentAmountEditText;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_new_flat_basic_info, container, false);

        context = getActivity().getApplicationContext();

        //Initialize all EditText
        flatNameEditText = (EditText) layout.findViewById(R.id.flatNameEditText);
        flatDescriptionEditText = (EditText) layout.findViewById(R.id.flatDescriptionEditText);
        rentAmountEditText = (EditText) layout.findViewById(R.id.rentAmountEditText);
        securityAmountEditText = (EditText) layout.findViewById(R.id.securityAmountEditText);

        return layout;
    }

    public String getFlatName() {
        return flatNameEditText.getText().toString();
    }

    public String getFlatDescription() {
        return flatDescriptionEditText.getText().toString();
    }

    public int getRentAmount() {
        return Integer.parseInt(rentAmountEditText.getText().toString());
    }

    public int getSecurityAmount() {
        return Integer.parseInt(securityAmountEditText.getText().toString());
    }

    public boolean verifyInputData() {
        if (flatNameEditText.getText().toString().trim().matches("")) {
            Toast.makeText(context, "No flat name entered", Toast.LENGTH_LONG).show();
            return false;
        } else if (flatDescriptionEditText.getText().toString().trim().matches("")) {
            Toast.makeText(context, "No flat description entered", Toast.LENGTH_LONG).show();
            return false;
        } else if (securityAmountEditText.getText().toString().trim().matches("")) {
            Toast.makeText(context, "No flat security entered", Toast.LENGTH_LONG).show();
            return false;
        } else if (rentAmountEditText.getText().toString().trim().matches("")) {
            Toast.makeText(context, "No flat rent amount entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

}
