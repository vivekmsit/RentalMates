package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.vivek.rentalmates.R;

/**
 * Created by vivek on 4/24/2015.
 */
public class ExpenseMenuDialog extends DialogFragment{

    private static final String TAG = "ExpenseMenu_Debug";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.expenseMenuOptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "inside onClick");
                    }
                });
        return builder.create();
    }
}
