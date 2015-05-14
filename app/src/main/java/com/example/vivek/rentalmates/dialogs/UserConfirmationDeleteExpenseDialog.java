package com.example.vivek.rentalmates.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.fragments.ExpenseDataListFragment;
import com.example.vivek.rentalmates.interfaces.OnDeleteExpenseReceiver;
import com.example.vivek.rentalmates.tasks.DeleteExpenseAsyncTask;

public class UserConfirmationDeleteExpenseDialog extends DialogFragment {

    private static final String TAG = "UserConfirmDelete_Debug";
    private Activity mainTabActivity;
    private ProgressDialog progressDialog;

    public UserConfirmationDeleteExpenseDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mainTabActivity = getActivity();
        progressDialog = new ProgressDialog(mainTabActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        final Long expenseId = getArguments().getLong("ExpenseId");
        final int currentPosition = getArguments().getInt("position");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Do you really want to delete selected expense?");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DeleteExpenseAsyncTask task = new DeleteExpenseAsyncTask(getActivity(), expenseId);
                task.setOnDeleteExpenseReceiver(new OnDeleteExpenseReceiver() {
                    @Override
                    public void onExpenseDeleteSuccessful(int position) {
                        progressDialog.cancel();
                        ViewPager pager = (ViewPager) mainTabActivity.findViewById(R.id.pager);
                        MainTabActivity.MyAdapter adapter = (MainTabActivity.MyAdapter) pager.getAdapter();
                        ExpenseDataListFragment fragment = (ExpenseDataListFragment) adapter.getRegisteredFragment(0);
                        fragment.onExpenseDeleteSuccessful(position);
                    }

                    @Override
                    public void onExpenseDeleteFailed() {
                        progressDialog.cancel();
                    }
                });
                task.setPosition(currentPosition);
                task.execute();
                progressDialog.setMessage("Deleting Expense");
                progressDialog.show();
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
