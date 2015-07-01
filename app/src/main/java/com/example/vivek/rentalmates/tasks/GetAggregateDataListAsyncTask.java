package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.AggregateData;
import com.example.vivek.rentalmates.interfaces.OnAggregateDataReceiver;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class GetAggregateDataListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetAggregateTask_Debug";

    private static MainApi mainApi = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    AggregateData aggregateData;
    private OnAggregateDataReceiver receiver;

    public GetAggregateDataListAsyncTask(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnFlatInfoListReceiver(OnAggregateDataReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (mainApi == null) {
            MainApi.Builder builder = new MainApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            mainApi = builder.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            aggregateData = mainApi.getAggregateData(userProfileId).execute();
            if (aggregateData == null) {
                Log.d(TAG, "expenses is null");
                msg = "SUCCESS_NO_DATA";
            } else {
                msg = "SUCCESS";
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

        Log.d(TAG, "inside onPostExecute() for GetFlatInfoListAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onAggregateDataLoadSuccessful(aggregateData);
                }
                break;

            case "SUCCESS_NO_DATA":
                if (receiver != null) {
                    receiver.onAggregateDataLoadSuccessful(null);
                }
                break;

            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onAggregateDataLoadFailed();
                }
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
