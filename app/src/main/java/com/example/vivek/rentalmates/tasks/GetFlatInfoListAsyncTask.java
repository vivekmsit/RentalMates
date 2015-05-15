package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfoCollection;
import com.example.vivek.rentalmates.interfaces.OnFlatInfoListReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class GetFlatInfoListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetFlatListTask_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private List<FlatInfo> flats;
    private OnFlatInfoListReceiver receiver;

    public GetFlatInfoListAsyncTask(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnFlatInfoListReceiver(OnFlatInfoListReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (ufService == null) {
            UserProfileApi.Builder builder1 = new UserProfileApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            ufService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            FlatInfoCollection flatInfoCollection = ufService.getFlatInfoList(userProfileId).execute();
            if (flatInfoCollection == null) {
                Log.d(TAG, "expenses is null");
                msg = "SUCCESS_NO_FLATS";
            } else {
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

        switch (msg) {
            case "SUCCESS_FLATS":
                if (receiver != null) {
                    receiver.onFlatInfoListLoadSuccessful(flats);
                }
                break;

            case "SUCCESS_NO_FLATS":
                //rare case
                if (receiver != null) {
                    receiver.onFlatInfoListLoadSuccessful(null);
                }
                break;

            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onFlatInfoListLoadFailed();
                }
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
