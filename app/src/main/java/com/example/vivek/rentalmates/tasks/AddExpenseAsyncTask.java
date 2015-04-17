package com.example.vivek.rentalmates.tasks;

/**
 * Created by vivek on 3/8/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.AddExpenseActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Collections;

public class AddExpenseAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "ExpenseAsyncTask_Debug";

    private static ExpenseGroupApi expenseGroupService = null;
    ExpenseData expenseData;
    private Context context;
    AddExpenseActivity activity;
    SharedPreferences prefs;
    IOException ioException;
    AppData appData;

    public AddExpenseAsyncTask(AddExpenseActivity expenseActivity, Context context, final ExpenseData expenseData) {
        this.context = context;
        this.expenseData = expenseData;
        this.activity = expenseActivity;
        appData = AppData.getInstance();
        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
        if (expenseGroupService == null) {
            ExpenseGroupApi.Builder builder1 = new ExpenseGroupApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            expenseGroupService = builder1.build();
        }
        try {
            ExpenseData uploadedExpenseData = expenseGroupService.addExpenseData(expenseData).execute();
            if (uploadedExpenseData == null) {
                Log.d(TAG, "expense is null");
            } else {
                boolean status = appData.addLocalExpenseData(context, uploadedExpenseData);
                if (status) {
                    msg = "SUCCESS";
                } else {
                    msg = "EXCEPTION";
                }
                return msg;
            }
            msg = "SUCCESS";
            Log.d(TAG, "inside addExpense");
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for AddExpenseAsyncTask");
        activity.setAddExpenseButtonClicked(false);

        if (msg.equals("SUCCESS")) {
            Toast.makeText(context, "ExpenseData uploaded", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else if (msg.equals("EXCEPTION")) {
            Log.d(TAG, "IOException: " + ioException.getMessage());
            Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Unable to upload ExpenseData");
            Toast.makeText(context, "Unable to upload ExpenseData", Toast.LENGTH_LONG).show();
        }
    }
}

