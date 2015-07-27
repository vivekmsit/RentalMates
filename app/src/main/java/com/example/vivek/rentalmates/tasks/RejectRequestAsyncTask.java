package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnRejectRequestReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class RejectRequestAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RegisterWithOld_Debug";

    private static MainApi mainApi = null;
    private Long requestId;
    private int position;
    private Context context;
    private Request request;
    private SharedPreferences prefs;
    private IOException ioException;
    private OnRejectRequestReceiver receiver;

    public RejectRequestAsyncTask(Context context, final Long requestId, int position) {
        this.context = context;
        this.requestId = requestId;
        this.position = position;

        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnRejectRequestReceiver(OnRejectRequestReceiver receiver) {
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
            Log.d(TAG, "userprofileid is: " + userProfileId);
            request = mainApi.acceptRequest(requestId).execute();
            if (request == null) {
                msg = "SUCCESS_NO_REQUEST_ACCEPTED";
            } else {
                msg = "SUCCESS_REQUEST_ACCEPTED";
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

        Log.d(TAG, "inside onPostExecute() for AcceptRequestRegisterWithOtherFlatAsyncTask");

        switch (msg) {
            case "SUCCESS_REQUEST_ACCEPTED":
                if (receiver != null) {
                    receiver.onRejectRequestSuccessful(position);
                }
                break;
            case "SUCCESS_NO_REQUEST_ACCEPTED":
                if (receiver != null) {
                    receiver.onRejectRequestSuccessful(position);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onRejectRequestFailed();
                }
                break;
            default:
                break;
        }
    }
}
