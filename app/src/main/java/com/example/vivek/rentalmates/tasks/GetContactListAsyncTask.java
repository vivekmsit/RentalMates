package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.Contact;
import com.example.vivek.rentalmates.backend.mainApi.model.ContactCollection;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnContactListReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class GetContactListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "ContactListTask_Debug";

    private static MainApi mainApi = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private List<Contact> contacts;
    private OnContactListReceiver receiver;

    public GetContactListAsyncTask(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnContactListReceiver(OnContactListReceiver receiver) {
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
            Long flatId = prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0);
            ContactCollection contactCollection = mainApi.getContactList(userProfileId, flatId).execute();
            if (contactCollection == null) {
                Log.d(TAG, "contacts is null");
                msg = "SUCCESS_NO_CONTACTS";
            } else {
                contacts = contactCollection.getItems();
                msg = "SUCCESS_CONTACTS";
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
            case "SUCCESS_CONTACTS":
                if (receiver != null) {
                    receiver.onContactListLoadSuccessful(contacts);
                }
                break;

            case "SUCCESS_NO_CONTACTS":
                //rare case
                if (receiver != null) {
                    receiver.onContactListLoadSuccessful(null);
                }
                break;

            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onContactListLoadFailed();
                }
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
