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

public class GetExpenseGroupNameDialog extends DialogFragment {
    private EditText expenseGroupNameEditText;
    private OnDialogResultListener listener;
    private Context context;

    public interface OnDialogResultListener {
        void onPositiveResult(String expenseGroupName);

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
        View view = inflater.inflate(R.layout.dialog_fragment_create_expense_group, null);
        expenseGroupNameEditText = (EditText) view.findViewById(R.id.expenseGroupNameEditText);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Enter Expense Group name");
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("Create Expense Group", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (verifyInputData()) {
                    if (listener != null) {
                        listener.onPositiveResult(expenseGroupNameEditText.getText().toString());
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
        if (expenseGroupNameEditText.getText().toString().trim().matches("")) {
            Toast.makeText(context, "No expense group name entered", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }
}