package com.example.vivek.rentalmates.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.vivek.rentalmates.tasks.DeleteExpenseAsyncTask;

public class UserConfirmationDeleteExpenseDialog extends DialogFragment {

    private static final String TAG = "UserConfirmDelete_Debug";

    public UserConfirmationDeleteExpenseDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Long expenseId = getArguments().getLong("ExpenseId");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Do you really want to delete selected expense?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteExpenseAsyncTask task = new DeleteExpenseAsyncTask(getActivity(), expenseId);
                task.execute();
                Log.d(TAG, "Ok: onClick");
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Cancel: onClick");
                dialog.dismiss();
            }
        });
        return alertDialogBuilder.create();
    }
}
