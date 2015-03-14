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

import com.example.vivek.rentalmates.activities.AddExpenseActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseDataCollection;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.others.LocalExpenseData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetExpenseDataListAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "GetExpenseTask_Debug";
    private static final String PRIMARY_FLAT_ID = "primary_flat_id";

    private static FlatInfoApi flatService = null;
    private Context context;
    SharedPreferences prefs;
    IOException ioException;
    boolean appStartup;
    public GetExpenseDataListAsyncTask(Context context, final boolean startup) {
        this.context = context;
        this.appStartup = startup;
        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
        if (flatService == null){
            FlatInfoApi.Builder builder1 = new FlatInfoApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
            flatService = builder1.build();
        }
        try {
            Long flatId = prefs.getLong(PRIMARY_FLAT_ID, 0);
            ExpenseDataCollection expensesCollection= flatService.getExpenseDataList(flatId).execute();
            msg = "SUCCESS";
            if (expensesCollection == null){
                Log.d(TAG, "expenses is null");
            }
            else {
                List<ExpenseData> expenses = expensesCollection.getItems();
                msg = LocalExpenseData.storeExpenseDataList(expenses);
            }
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

        Log.d(TAG, "inside onPostExecute() for GetExpenseDataListAsyncTask");

        if (msg.equals("SUCCESS") && this.appStartup == true){
            Toast.makeText(context, "ExpenseData uploaded", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, MainTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        else if (msg.equals("EXCEPTION")){
            Log.d(TAG, "IOException: "+ ioException.getMessage());
            Toast.makeText(context, "IOException: "+ ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "Unable to upload ExpenseData");
            Toast.makeText(context, "Unable to upload ExpenseData", Toast.LENGTH_LONG).show();
        }
    }
}


