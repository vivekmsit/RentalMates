package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class PostYourFlatAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RentalMatesDebug";

    private static FlatInfoApi flatService = null;
    private Long flatId;
    private Context context;
    private IOException ioException;
    private AsyncTaskReceiver receiver;

    public PostYourFlatAsyncTask(Context context, final Long flatId) {
        this.context = context;
        this.flatId = flatId;
    }

    public interface AsyncTaskReceiver {
        void onAsyncTaskComplete();

        void onAsyncTaskFailed();
    }

    public void setAsyncTaskReceiver(AsyncTaskReceiver receiver) {
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
            flatService.postFlatAsAvailable(flatId).execute();
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

        Log.d(TAG, "inside onPostExecute() for RegisterFlatAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onAsyncTaskComplete();
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onAsyncTaskFailed();
                }
                break;
            default:
                break;
        }
    }
}
