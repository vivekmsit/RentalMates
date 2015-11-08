package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.mainApi.model.FlatInfoCollection;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class SearchFlatsForRentAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "SearchFlats_Debug";

    private static MainApi mainApi = null;
    double latitude;
    double longitude;
    private Context context;
    private IOException ioException;
    private OnExecuteTaskReceiver receiver;
    private List<FlatInfo> flats;

    public interface OnExecuteTaskReceiver {
        void onTaskCompleted(List<FlatInfo> flatInfos);

        void onTaskFailed();
    }

    public void setOnExecuteTaskReceiver(OnExecuteTaskReceiver receiver) {
        this.receiver = receiver;
    }

    public SearchFlatsForRentAsyncTask(Context context, final double latitude, final double longitude) {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
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
            FlatInfoCollection flatInfoCollection = mainApi.searchFlatsForRent(latitude, longitude).execute();
            if (flatInfoCollection == null) {
                msg = "SUCCESS_NO_FLAT_AVAILABLE";
            } else {
                msg = "SUCCESS_FLAT_AVAILABLE";
                flats = flatInfoCollection.getItems();
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
                    receiver.onTaskCompleted(flats);
                }
                break;
            case "SUCCESS_NO_FLAT_AVAILABLE":
                if (receiver != null) {
                    receiver.onTaskCompleted(flats);
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
