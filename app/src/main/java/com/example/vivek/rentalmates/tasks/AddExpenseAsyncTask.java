package com.example.vivek.rentalmates.tasks;

/**
 * Created by vivek on 3/8/2015.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.AddExpenseActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.others.LocalExpenseData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AddExpenseAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "ExpenseAsyncTask_Debug";
    private static final String FLAT_INFO_ID = "flat_info_id";

    private static FlatInfoApi flatService = null;
    ExpenseData ed;
    private Context context;
    AddExpenseActivity activity;
    SharedPreferences prefs;
    IOException ioException;

    public AddExpenseAsyncTask(AddExpenseActivity expenseActivity, Context context, final ExpenseData expenseData) {
        this.context = context;
        this.ed = expenseData;
        this.activity = expenseActivity;
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
            Long flatId = prefs.getLong(FLAT_INFO_ID, 0);
            ed.setFlatId(flatId);
            FlatInfo uploadedFlatInfo = flatService.addExpenseData(flatId, ed).execute();
            if (uploadedFlatInfo == null){
                Log.d(TAG, "uploadedFlatInfo is null");
            }
            else {
                List<ExpenseData> expenses = uploadedFlatInfo.getExpenses();
                if (expenses == null){
                    Log.d(TAG, "expenses is null");
                } else {
                    //storing expenses into a getFiles
                    List<LocalExpenseData> localExpenses = new ArrayList<>();
                    for(ExpenseData uploadedExpenseData : expenses) {
                        int amount = uploadedExpenseData.getAmount();
                        String description = uploadedExpenseData.getDescription();
                        String owner = uploadedExpenseData.getOwnerEmailId();
                        Log.d(TAG, "description is: " + uploadedExpenseData.getDescription());
                        Log.d(TAG, "amount is: " + uploadedExpenseData.getAmount());
                        Log.d(TAG, "owner is: " + uploadedExpenseData.getOwnerEmailId());
                        LocalExpenseData data = new LocalExpenseData(amount, description, owner);
                        localExpenses.add(data);
                    }

                    String path = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_MOVIES).getPath();
                    FileOutputStream fos = new FileOutputStream(path + "/" + "expenses.tmp");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(localExpenses);
                    oos.close();
                }
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

        if (msg.equals("SUCCESS")){
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

