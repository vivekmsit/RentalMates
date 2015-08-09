package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.Contact;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnAddContactReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class AddContactAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "AddContactTask_Debug";

    private static MainApi mainApi = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private Contact contact;
    private OnAddContactReceiver receiver;

    public AddContactAsyncTask(Context context, final Contact contact) {
        this.context = context;
        this.contact = contact;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnAddContactReceiver(OnAddContactReceiver receiver) {
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
            Contact contact = mainApi.addContact(userProfileId, this.contact).execute();
            if (contact == null) {
                Log.d(TAG, "contacts is null");
                msg = "SUCCESS_NO_CONTACTS";
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

        Log.d(TAG, "inside onPostExecute() for GetContactListAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onAddContactSuccessful(contact);
                }
                break;

            case "SUCCESS_NO_CONTACTS":
                //rare case
                if (receiver != null) {
                    receiver.onAddContactSuccessful(null);
                }
                break;

            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onAddContactFailed();
                }
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
