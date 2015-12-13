package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteriaCollection;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class SearchRoomMatesAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "SearchFlats_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private IOException ioException;
    private OnExecuteTaskReceiver receiver;
    private Long flatId;
    private List<FlatSearchCriteria> flatSearchCriteriaList;

    public interface OnExecuteTaskReceiver {
        void onTaskCompleted(List<FlatSearchCriteria> flatSearchCriteriaList);

        void onTaskFailed();
    }

    public void setOnExecuteTaskReceiver(OnExecuteTaskReceiver receiver) {
        this.receiver = receiver;
    }

    public SearchRoomMatesAsyncTask(Context context, final Long flatId) {
        this.context = context;
        this.flatId = flatId;
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
            FlatSearchCriteriaCollection flatSearchCriteriaCollection = ufService.searchRoomMates(flatId).execute();
            if (flatSearchCriteriaCollection == null) {
                msg = "SUCCESS_NO_ROOMMATE_AVAILABLE";
            } else {
                msg = "SUCCESS_ROOMMATE_AVAILABLE";
                flatSearchCriteriaList = flatSearchCriteriaCollection.getItems();
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
            case "SUCCESS_ROOMMATE_AVAILABLE":
                if (receiver != null) {
                    receiver.onTaskCompleted(flatSearchCriteriaList);
                }
                break;
            case "SUCCESS_NO_ROOMMATE_AVAILABLE":
                if (receiver != null) {
                    receiver.onTaskCompleted(flatSearchCriteriaList);
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
