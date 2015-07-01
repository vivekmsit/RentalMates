package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseDataCollection;
import com.example.vivek.rentalmates.interfaces.OnExpenseListReceiver;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GetAllExpenseListAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "GetExpenseTask_Debug";

    private static ExpenseGroupApi expenseService = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private AppData appData;
    private List<ExpenseData> expenses;
    private OnExpenseListReceiver receiver;

    public GetAllExpenseListAsyncTask(Context context) {
        this.context = context;
        this.appData = AppData.getInstance();

        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnExpenseListReceiver(OnExpenseListReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (expenseService == null) {
            ExpenseGroupApi.Builder builder1 = new ExpenseGroupApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            expenseService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            ExpenseDataCollection expensesCollection = expenseService.getAllExpensesList(userProfileId).execute();
            msg = "SUCCESS";
            if (expensesCollection == null) {
                Log.d(TAG, "expenses is null");
            } else {
                expenses = expensesCollection.getItems();
                if (expenses != null) {
                    //Store in Sorted order by Date
                    Collections.reverse(expenses);
                    this.appData.storeExpenseDataList(context, expenses);
                }
            }
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for GetExpenseDataListAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onExpenseDataListLoadSuccessful(expenses);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onExpenseDataListLoadFailed();
                }
                break;
            default:
                break;
        }
    }
}


