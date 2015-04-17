package com.example.vivek.rentalmates.tasks;

/**
 * Created by vivek on 3/14/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseDataCollection;
import com.example.vivek.rentalmates.interfaces.OnAllExpenseListLoadedListener;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GetAllExpenseListAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "GetExpenseTask_Debug";

    private static ExpenseGroupApi expenseService = null;
    private Context context;
    SharedPreferences prefs;
    IOException ioException;
    boolean appStartup;
    Long flatId;
    AppData appData;
    public OnAllExpenseListLoadedListener loadedListener;

    public GetAllExpenseListAsyncTask(Context context, final Long flatId, final boolean startup) {
        this.context = context;
        this.appStartup = startup;
        this.flatId = flatId;
        this.appData = AppData.getInstance();

        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
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
                List<ExpenseData> expenses = expensesCollection.getItems();
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

        if (msg.equals("SUCCESS") && this.appStartup == true) {
            Toast.makeText(context, "ExpenseData retrieved successfully", Toast.LENGTH_SHORT).show();
            BackendApiService.storePrimaryFlatId(this.context, flatId);

            Intent intent = new Intent(context, MainTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else if (msg.equals("SUCCESS") && this.appStartup == false && loadedListener != null) {
            loadedListener.onExpenseDataListLoaded();
        } else if (msg.equals("EXCEPTION")) {
            Log.d(TAG, "IOException: " + ioException.getMessage());
            Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
            loadedListener.onExpenseDataListLoadFailed();
        } else {
            Log.d(TAG, "Unable to upload ExpenseData");
            Toast.makeText(context, "Unable to upload ExpenseData", Toast.LENGTH_LONG).show();
        }
    }
}


