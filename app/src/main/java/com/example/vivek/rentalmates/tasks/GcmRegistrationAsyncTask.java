package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.registration.Registration;
import com.example.vivek.rentalmates.interfaces.OnGcmRegistrationReceiver;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class GcmRegistrationAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GcmRegisterTask_Debug";

    private static Registration regService = null;
    private Context context;
    private SharedPreferences prefs;
    private String regId;
    private IOException exception;
    private OnGcmRegistrationReceiver receiver;

    public GcmRegistrationAsyncTask(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnGcmRegistrationReceiver(OnGcmRegistrationReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (regService == null) {
            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            regService = builder.build();
        }
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            regId = gcm.register(AppConstants.SENDER_ID);
            regService.register(regId).execute();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(AppConstants.GCM_REG_ID, regId);
            editor.apply();
            msg = "SUCCESS";
        } catch (IOException ex) {
            ex.printStackTrace();
            exception = ex;
            msg = "EXCEPTION";
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Log.d(TAG, "inside onPostExecute");
        switch (msg) {
            case "SUCCESS":
                Log.d(TAG, "Gcm Registration Successful");
                if (receiver != null) {
                    receiver.onGcmRegisterSuccessful(regId);
                }
                break;
            case "EXCEPTION":
                Toast.makeText(context, "IOException occurred: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, "IOException occurred: " + exception.getMessage());
                if (receiver != null) {
                    receiver.onGcmRegisterFailed();
                }
                break;
            default:
                break;
        }
    }
}
