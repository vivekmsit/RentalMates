package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.example.vivek.rentalmates.R;

public class FlatPropertiesDialog extends DialogFragment {
    private OnDialogResultListener listener;
    private CheckBox expenseGroupRequiredCheckBox;
    private CheckBox availableForRentCheckBox;

    public interface OnDialogResultListener {
        void onPositiveResult(boolean availableForRent, boolean expenseGroupRequired);

        void onNegativeResult();
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fragment_flat_properties, null);

        expenseGroupRequiredCheckBox = (CheckBox) view.findViewById(R.id.expenseGroupRequiredCheckBox);
        availableForRentCheckBox = (CheckBox) view.findViewById(R.id.availableForRentCheckBox);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Flat Properties");
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (verifyInputData()) {
                    if (listener != null) {
                        listener.onPositiveResult(expenseGroupRequiredCheckBox.isChecked(), availableForRentCheckBox.isChecked());
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
        return true;
    }
}