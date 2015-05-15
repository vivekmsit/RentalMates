package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.vivek.rentalmates.activities.MyLoginActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.interfaces.OnUploadUserProfileReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class UploadUserProfileAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "UploadUser_Debug";

    private static UserProfileApi ufService = null;
    private UserProfile uf = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private MyLoginActivity activity;
    private Long primaryFlatId;
    private OnUploadUserProfileReceiver receiver;

    public UploadUserProfileAsyncTask(MyLoginActivity myLoginActivity, Context context, final UserProfile userProfile) {
        this.context = context;
        this.uf = userProfile;
        activity = myLoginActivity;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnUploadUserProfileReceiver(OnUploadUserProfileReceiver receiver) {
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
            UserProfile uploadedUserProfile = ufService.insert(uf).execute();
            BackendApiService.storeUserProfileId(this.context, uploadedUserProfile.getId());
            if (uploadedUserProfile.getNumberOfFlats() == 0) {
                msg = "SUCCESS_NO_FLAT_REGISTERED";
            } else {
                msg = "SUCCESS_FLAT_REGISTERED";
                primaryFlatId = uploadedUserProfile.getPrimaryFlatId();
                BackendApiService.storePrimaryFlatId(this.context, primaryFlatId);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(AppConstants.USER_PROFILE_UPDATED, 1);
            editor.apply();
            new GetUserProfileListAsyncTask(context).execute();
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

        Log.d(TAG, "inside onPostExecute() for UploadUserProfileAsyncTask");

        switch (msg) {
            case "SUCCESS_NO_FLAT_REGISTERED":
            case "SUCCESS_FLAT_REGISTERED":
                if (receiver != null) {
                    receiver.onUploadUserProfileSuccessful(msg);
                }
                break;
            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onUploadUserProfileFailed();
                }
                break;
            default:
                break;
        }
    }
}
