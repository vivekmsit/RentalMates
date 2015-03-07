package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.activities.MainTabActivity;
import com.example.vivek.rentalmates.activities.MyLoginActivity;
import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
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
    private String message = null;
    private Context context;
    SharedPreferences prefs;
    IOException ioException;

    public RegisterFlatAsyncTask(Context context, final FlatInfo flatInfo) {
        this.context = context;
        this.fi = flatInfo;
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
            BackendApiService.storeFlatInfoId(this.context, uploadedFlatInfo.getFlatId());
            msg = "SUCCESS";
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
        if (msg.equals("SUCCESS")){
            prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(MyLoginActivity.FIRST_TIME_LOGIN, true);
            editor.commit();
            Toast.makeText(context, "FlatInfo uploaded successfully", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, MainTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
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
