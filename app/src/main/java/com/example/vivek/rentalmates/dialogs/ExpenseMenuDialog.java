package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.vivek.rentalmates.R;

public class ExpenseMenuDialog extends DialogFragment {

    private static final String TAG = "ExpenseMenu_Debug";

    public ExpenseMenuDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Long expenseId = getArguments().getLong("ExpenseId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.expenseMenuOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        break;
                    case 1:
                        UserConfirmationDeleteExpenseDialog expenseDialog = new UserConfirmationDeleteExpenseDialog();
                        Bundle bundle = new Bundle();
                        bundle.putLong("ExpenseId", expenseId);
                        expenseDialog.setArguments(bundle);
                        expenseDialog.show(getFragmentManager(), "MyDialog");
                        break;
                    case 2:
                        break;
                    default:
                        dialog.dismiss();
                        break;
                }
                Log.d(TAG, "inside onClick");
            }
        });
        return builder.create();
    }
}
