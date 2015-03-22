package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.DetermineFlatActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.activities.MyLoginActivity;
import com.example.vivek.rentalmates.activities.RegisterFlatActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

/**
 * Created by vivek on 3/6/2015.
 */
public class RegisterWithOldFlatAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RegisterWithOld_Debug";

    private static FlatInfoApi flatService = null;
    private String flatName;
    private Context context;
    FlatInfo newFlatInfo;
    DetermineFlatActivity activity;
    SharedPreferences prefs;
    IOException ioException;
    Long flatId;

    public RegisterWithOldFlatAsyncTask(DetermineFlatActivity flatActivity, Context context, final String flatName) {
        this.context = context;
        this.activity = flatActivity;
        this.flatName = flatName;

        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
        if (flatService == null) {
            FlatInfoApi.Builder builder1 = new FlatInfoApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
            flatService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            Log.d(TAG, "userprofileid is: " + userProfileId);
            newFlatInfo = flatService.registerWithOldFlat(this.flatName, userProfileId).execute();
            if (newFlatInfo == null) {
                msg = "SUCCESS_NO_FLAT_AVAILABLE";
            } else {
                msg = "SUCCESS_FLAT_AVAILABLE";
                flatId = newFlatInfo.getFlatId();
                BackendApiService.storePrimaryFlatName(this.context, newFlatInfo.getFlatName());
                BackendApiService.storePrimaryFlatId(this.context, newFlatInfo.getFlatId());
            }
            Log.d(TAG, "inside insert");
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for RegisterFlatAsyncTask");
        activity.setRegisterWithOldFlatButtonClicked(false);

        if (msg.equals("SUCCESS_FLAT_AVAILABLE")) {
            Toast.makeText(context, "Registered with old flat: " + flatName + "\nretrieving ExpenseData info", Toast.LENGTH_SHORT).show();
            new GetExpenseDataListAsyncTask(context, flatId, true).execute();
        } else if (msg.equals("SUCCESS_NO_FLAT_AVAILABLE")) {
            Toast.makeText(context, "Flat with given name doesn't exist.\nPlease enter different name", Toast.LENGTH_LONG).show();
        } else if (msg.equals("EXCEPTION")) {
            Log.d(TAG, "IOException: " + ioException.getMessage());
            Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Unable to upload FlatInfo data");
            Toast.makeText(context, "Unable to upload FlatInfo data", Toast.LENGTH_LONG).show();
        }
    }
}
