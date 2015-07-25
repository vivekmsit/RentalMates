package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnDeleteRemoteDataReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class DeleteRemoteDataAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "UploadUser_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private OnDeleteRemoteDataReceiver receiver;

    public DeleteRemoteDataAsyncTask(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnDeleteRemoteDataReceiver(OnDeleteRemoteDataReceiver receiver) {
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
            if (userProfileId == 0) {
                msg = "FAILED";
                return msg;
            }
            ufService.removeDataStoreData(userProfileId).execute();
            msg = "SUCCESS";
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for UploadUserProfileAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onDeleteRemoteDataSuccessful();
                }
                break;
            case "FAILED":
                if (receiver != null) {
                    Toast.makeText(context, "UserProfile Id not present", Toast.LENGTH_LONG).show();
                    receiver.onDeleteRemoteDataFailed();
                }
                break;
            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onDeleteRemoteDataFailed();
                }
                break;
            default:
                break;
        }
    }
}
