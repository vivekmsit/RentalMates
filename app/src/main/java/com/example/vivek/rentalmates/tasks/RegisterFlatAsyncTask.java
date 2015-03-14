package com.example.vivek.rentalmates.tasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.activities.MyLoginActivity;
import com.example.vivek.rentalmates.activities.RegisterFlatActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.registration.Registration;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by vivek on 3/6/2015.
 */
public class RegisterFlatAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RentalMatesDebug";

    private static FlatInfoApi flatService = null;
    private FlatInfo fi = null;
    private Context context;
    RegisterFlatActivity activity;
    SharedPreferences prefs;
    IOException ioException;

    public RegisterFlatAsyncTask(RegisterFlatActivity flatActivity, Context context, final FlatInfo flatInfo) {
        this.context = context;
        this.fi = flatInfo;
        this.activity = flatActivity;
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
            FlatInfo uploadedFlatInfo = flatService.insert(fi).execute();
            String status = uploadedFlatInfo.getCreateFlatResult();
            if (status.equals("NEW_FLAT_INFO")){
                BackendApiService.storePrimaryFlatId(this.context, uploadedFlatInfo.getFlatId());
                msg = "SUCCESS_NEW_FLAT";
            } else if (status.equals("OLD_FLAT_INFO")){
                msg = "SUCCESS_OLD_FLAT";
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
        activity.setRegisterButtonClicked(false);

        if (msg.equals("SUCCESS_NEW_FLAT")){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(MyLoginActivity.FIRST_TIME_LOGIN, true);
            editor.commit();

            Toast.makeText(context, "FlatInfo uploaded", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(context, MainTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } else if (msg.equals("SUCCESS_OLD_FLAT")) {
            Toast.makeText(context, "Flat with given name already registered. \n Please enter different name", Toast.LENGTH_LONG).show();
        }
        else if (msg.equals("EXCEPTION")){
            Log.d(TAG, "IOException: "+ ioException.getMessage());
            Toast.makeText(context, "IOException: "+ ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "Unable to upload FlatInfo data");
            Toast.makeText(context, "Unable to upload FlatInfo data", Toast.LENGTH_LONG).show();
        }
    }
}
