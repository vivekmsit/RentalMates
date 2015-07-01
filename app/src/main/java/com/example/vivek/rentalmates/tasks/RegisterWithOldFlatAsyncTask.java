package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnRegisterWithOldFlatReceiver;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class RegisterWithOldFlatAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RegisterWithOld_Debug";

    private static FlatInfoApi flatService = null;
    private String flatName;
    private Context context;
    private FlatInfo oldFlatInfo;
    private SharedPreferences prefs;
    private IOException ioException;
    private OnRegisterWithOldFlatReceiver receiver;

    public RegisterWithOldFlatAsyncTask(Context context, final String flatName) {
        this.context = context;
        this.flatName = flatName;

        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnRegisterWithOldFlatReceiver(OnRegisterWithOldFlatReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (flatService == null) {
            FlatInfoApi.Builder builder1 = new FlatInfoApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            flatService = builder1.build();
        }
        try {
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            Log.d(TAG, "userprofileid is: " + userProfileId);
            oldFlatInfo = flatService.registerWithOldFlat(this.flatName, userProfileId).execute();
            if (oldFlatInfo == null) {
                msg = "SUCCESS_NO_FLAT_AVAILABLE";
            } else {
                msg = "SUCCESS_FLAT_AVAILABLE";
                BackendApiService.storePrimaryFlatId(this.context, oldFlatInfo.getFlatId());
                BackendApiService.storePrimaryFlatName(this.context, oldFlatInfo.getFlatName());
                BackendApiService.storeFlatExpenseGroupId(this.context, oldFlatInfo.getExpenseGroupId());
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
                    receiver.onRegisterWithOldFlatSuccessful(msg, oldFlatInfo);
                }
                break;
            case "SUCCESS_NO_FLAT_AVAILABLE":
                if (receiver != null) {
                    receiver.onRegisterWithOldFlatSuccessful(msg, null);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onRegisterWithOldFlatFailed();
                }
                break;
            default:
                break;
        }
    }
}
