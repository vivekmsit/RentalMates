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

import com.example.vivek.rentalmates.activities.DetermineFlatActivity;
import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfoCollection;
import com.example.vivek.rentalmates.others.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetFlatInfoListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetFlatListTask_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    SharedPreferences prefs;
    IOException ioException;
    boolean appStartup;
    List<FlatInfo> flats;

    public GetFlatInfoListAsyncTask(Context context, final boolean startup) {
        this.context = context;
        this.appStartup = startup;
        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
        if (ufService == null){
            UserProfileApi.Builder builder1 = new UserProfileApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://kinetic-wind-814.appspot.com/_ah/api/");
            ufService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            FlatInfoCollection flatInfoCollection= ufService.getFlatInfoList(userProfileId).execute();
            if (flatInfoCollection == null){
                Log.d(TAG, "expenses is null");
                msg = "SUCCESS_NO_FLATS";
            }
            else {
                flats = flatInfoCollection.getItems();
                msg = "SUCCESS_FLATS";
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

        Log.d(TAG, "inside onPostExecute() for GetFlatInfoListAsyncTask");

        if (msg.equals("SUCCESS_FLATS")){
            Toast.makeText(context, "FlatInfo List retrieved successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, DetermineFlatActivity.class);
            intent.putExtra("FLAT_REGISTERED", true);
            List<Long> flatIds = new ArrayList<>();
            List<String> flatNames = new ArrayList<>();
            int current = 0;
            for (FlatInfo flatInfo: flats) {
                flatIds.add(current,flatInfo.getFlatId());
                flatNames.add(current, flatInfo.getFlatName());
                current++;
            }
            intent.putExtra("flatIds", (java.io.Serializable) flatIds);
            intent.putExtra("flatNames", (java.io.Serializable) flatNames);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
        else if (msg.equals("SUCCESS_NO_FLATS")) {
            //rare case
            Toast.makeText(context, "No flat registered for given user", Toast.LENGTH_LONG).show();
        }
        else if (msg.equals("EXCEPTION")){
            Log.d(TAG, "IOException: "+ ioException.getMessage());
            Toast.makeText(context, "IOException: "+ ioException.getMessage(), Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "Unable to retrieve FlatInfo List");
            Toast.makeText(context, "Unable to retrieve FlatInfo List", Toast.LENGTH_LONG).show();
        }
    }
}
