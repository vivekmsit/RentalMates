package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;

public class FlatRentDetailsDialog extends DialogFragment {
    private EditText totalSecurityAmount;
    private EditText totalRentAmount;
    private OnDialogResultListener listener;
    private Context context;

    public interface OnDialogResultListener {
        void onPositiveResult(Integer securityAmount, Integer rentAmount);

        void onNegativeResult();
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_flat_rent_details, null);
        totalSecurityAmount = (EditText) view.findViewById(R.id.securityAmountEditText);
        totalRentAmount = (EditText) view.findViewById(R.id.rentAmountEditText);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Enter Expense Flat name");
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (verifyInputData()) {
                    if (listener != null) {
                        listener.onPositiveResult(Integer.parseInt(totalSecurityAmount.getText().toString()),
                                Integer.parseInt(totalRentAmount.getText().toString()));
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onNegativeResult();
                }
            }
        });
        return alertDialogBuilder.create();
    }

    public boolean verifyInputData() {
        if (totalSecurityAmount.getText().toString().trim().matches("")) {
            Toast.makeText(context, "Security amount not entered", Toast.LENGTH_LONG).show();
            return false;
        } else if (totalRentAmount.getText().toString().trim().matches("")) {
            Toast.makeText(context, "Rent Amount not entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}