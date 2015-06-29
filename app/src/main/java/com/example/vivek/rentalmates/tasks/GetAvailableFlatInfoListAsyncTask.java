package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfoCollection;
import com.example.vivek.rentalmates.interfaces.OnAvailableFlatInfoListReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class GetAvailableFlatInfoListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetFlatListTask_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private IOException ioException;
    private List<FlatInfo> flats;
    private OnAvailableFlatInfoListReceiver receiver;

    public GetAvailableFlatInfoListAsyncTask(Context context) {
        this.context = context;
    }

    public void setOnAvailableFlatInfoListReceiver(OnAvailableFlatInfoListReceiver receiver) {
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
            FlatInfoCollection flatInfoCollection = ufService.getAvailableFlatInfoList().execute();
            if (flatInfoCollection == null) {
                Log.d(TAG, "expenses is null");
                msg = "SUCCESS_NO_FLATS";
            } else {
                flats = flatInfoCollection.getItems();
                msg = "SUCCESS_FLATS";
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

        Log.d(TAG, "inside onPostExecute() for GetFlatInfoListAsyncTask");

        switch (msg) {
            case "SUCCESS_FLATS":
                if (receiver != null) {
                    receiver.onAvailableFlatInfoListLoadSuccessful(flats);
                }
                break;

            case "SUCCESS_NO_FLATS":
                //rare case
                if (receiver != null) {
                    receiver.onAvailableFlatInfoListLoadSuccessful(null);
                }
                break;

            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onAvailableFlatInfoListLoadFailed();
                }
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
