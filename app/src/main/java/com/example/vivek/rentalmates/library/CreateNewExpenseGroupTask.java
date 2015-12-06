package com.example.vivek.rentalmates.library;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalUserProfile;
import com.example.vivek.rentalmates.dialogs.GetExpenseGroupNameDialog;
import com.example.vivek.rentalmates.interfaces.OnCreateExpenseGroupReceiver;
import com.example.vivek.rentalmates.tasks.CreateExpenseGroupAsyncTask;

public class CreateNewExpenseGroupTask {
    private static final String TAG = "RegisterNewFlat_Debug";
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private OnCreateNewExpenseGroupTask receiver;
    private Activity activity;
    private Context context;
    private AppData appData;

    public interface OnCreateNewExpenseGroupTask {
        void onCreateNewExpenseGroupTaskSuccess(ExpenseGroup expenseGroup);

        void onCreateNewExpenseGroupTaskFailed();
    }

    public CreateNewExpenseGroupTask(Activity activity, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.appData = AppData.getInstance();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnRegisterNewFlatTask(OnCreateNewExpenseGroupTask receiver) {
        this.receiver = receiver;
    }

    public void execute() {
        final ExpenseGroup expenseGroup = new ExpenseGroup();
        GetExpenseGroupNameDialog dialog = new GetExpenseGroupNameDialog();
        dialog.setOnDialogResultListener(new GetExpenseGroupNameDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult(String expenseGroupName) {
                expenseGroup.setName(expenseGroupName);
                createExpenseGroup(expenseGroup);
            }

            @Override
            public void onNegativeResult() {
                if (receiver != null) {
                    receiver.onCreateNewExpenseGroupTaskFailed();
                }
            }
        });
        dialog.show(fragmentManager, "fragment");
    }

    private void createExpenseGroup(ExpenseGroup expenseGroup) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        LocalUserProfile localUserProfile = appData.getLocalUserProfile(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        expenseGroup.setOwnerId(localUserProfile.getUserProfileId());
        expenseGroup.setOwnerEmailId(localUserProfile.getEmailId());

        CreateExpenseGroupAsyncTask task = new CreateExpenseGroupAsyncTask(context, expenseGroup);
        task.setOnCreateExpenseGroupReceiver(new OnCreateExpenseGroupReceiver() {
            @Override
            public void onCreateExpenseGroupSuccessful(ExpenseGroup expenseGroup) {
                progressDialog.cancel();
                if (expenseGroup.getOperationResult().equals("OLD_EXPENSE_GROUP")) {
                    Toast.makeText(context, "Already a member of Expense Group with given name", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Expense Group Created", Toast.LENGTH_SHORT).show();
                }
                if (receiver != null) {
                    receiver.onCreateNewExpenseGroupTaskSuccess(expenseGroup);
                }
            }

            @Override
            public void onCreateExpenseGroupFailed() {
                progressDialog.cancel();
                if (receiver != null) {
                    receiver.onCreateNewExpenseGroupTaskFailed();
                }
            }
        });
        task.execute();
        progressDialog.setMessage("Creating Expense Group " + expenseGroup.getName());
        progressDialog.show();
    }
}
