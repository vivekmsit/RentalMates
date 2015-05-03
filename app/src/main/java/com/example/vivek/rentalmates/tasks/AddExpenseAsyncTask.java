package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.interfaces.OnAddExpenseReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class AddExpenseAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "ExpenseAsyncTask_Debug";

    private static ExpenseGroupApi expenseGroupService = null;
    private ExpenseData expenseData;
    private Context context;
    private IOException ioException;
    private AppData appData;

    public OnAddExpenseReceiver receiver;

    public AddExpenseAsyncTask(Context context, final ExpenseData expenseData) {
        this.context = context;
        this.expenseData = expenseData;
        appData = AppData.getInstance();
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
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

        switch (msg) {
            case "SUCCESS":
                receiver.onAddExpenseSuccessful();
                break;
            case "EXCEPTION":
                receiver.onAddExpenseFailed();
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;
            default:
                receiver.onAddExpenseFailed();
                Log.d(TAG, "Unable to upload ExpenseData");
                Toast.makeText(context, "Unable to upload ExpenseData", Toast.LENGTH_LONG).show();
                break;
        }
    }
}

