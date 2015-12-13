package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class PostYourRoomRequirementAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "PostReq_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private IOException ioException;
    private OnExecuteTaskReceiver receiver;
    private FlatSearchCriteria uploadedFlatSearchCriteria;
    private FlatSearchCriteria flatSearchCriteria;

    public interface OnExecuteTaskReceiver {
        void onTaskCompleted(FlatSearchCriteria uploadedFlatSearchCriteria);

        void onTaskFailed();
    }

    public void setOnExecuteTaskReceiver(OnExecuteTaskReceiver receiver) {
        this.receiver = receiver;
    }

    public PostYourRoomRequirementAsyncTask(Context context, final FlatSearchCriteria flatSearchCriteria) {
        this.context = context;
        this.flatSearchCriteria = flatSearchCriteria;
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
            uploadedFlatSearchCriteria = ufService.postYourRoomRequirement(flatSearchCriteria).execute();
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

        Log.d(TAG, "inside onPostExecute() for PostYourRoomRequirementAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onTaskCompleted(uploadedFlatSearchCriteria);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onTaskFailed();
                }
                break;
            default:
                break;
        }
    }
}
