package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnRequestJoinExistingEntityReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class RequestAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RegisterWithOld_Debug";

    private static MainApi mainApi = null;
    private String entityType;
    private String entityName;
    private String ownerEmailId;
    private Context context;
    private Request request;
    private SharedPreferences prefs;
    private IOException ioException;
    private OnRequestJoinExistingEntityReceiver receiver;

    public RequestAsyncTask(Context context, final String entityType, final String entityName, final String ownerEmailId) {
        this.context = context;
        this.entityType = entityType;
        this.entityName = entityName;
        this.ownerEmailId = ownerEmailId;

        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnRequestJoinExistingEntityReceiver(OnRequestJoinExistingEntityReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (mainApi == null) {
            MainApi.Builder builder1 = new MainApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            mainApi = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            request = mainApi.requestJoinExistingEntity(this.entityType, this.entityName, this.ownerEmailId, userProfileId).execute();
            if (request == null) {
                msg = "SUCCESS_NO_FLAT_AVAILABLE";
            } else {
                msg = "SUCCESS_FLAT_AVAILABLE";
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

        switch (msg) {
            case "SUCCESS_FLAT_AVAILABLE":
                if (receiver != null) {
                    receiver.onRequestJoinExistingEntitySuccessful(request);
                }
                break;
            case "SUCCESS_NO_FLAT_AVAILABLE":
                if (receiver != null) {
                    receiver.onRequestJoinExistingEntitySuccessful(request);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onRequestJoinExistingEntityFailed();
                }
                break;
            default:
                break;
        }
    }
}
