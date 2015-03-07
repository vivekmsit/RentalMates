package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
    private static FlatInfoApi flatService = null;
    private FlatInfo fi = null;
    private String message = null;
    private Context context;
    private static final String TAG = "RentalMatesDebug";

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
            msg = "Flat Info uploaded successfully";
            Log.d(TAG, "inside insert");
        } catch (IOException e) {
            msg = "Exception occurred";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("FLATINFOAPI").log(Level.INFO, msg);
    }
}
