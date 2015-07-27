package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.Request;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnAcceptRequestReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class AcceptRequestAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "AcceptRequest_Debug";

    private static MainApi mainApi;
    private Long requestId;
    private int position;
    private Context context;
    private Request request;
    private IOException ioException;
    private OnAcceptRequestReceiver receiver;

    public AcceptRequestAsyncTask(Context context, final Long requestId, int position) {
        this.context = context;
        this.requestId = requestId;
        this.position = position;
    }

    public void setOnAcceptRequestReceiver(OnAcceptRequestReceiver receiver) {
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
            request = mainApi.acceptRequest(requestId).execute();
            if (request == null) {
                msg = "SUCCESS_NO_REQUEST_ACCEPTED";
            } else {
                msg = "SUCCESS_REQUEST_ACCEPTED";
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

        Log.d(TAG, "inside onPostExecute() for AcceptRequestRegisterWithOtherFlatAsyncTask");

        switch (msg) {
            case "SUCCESS_REQUEST_ACCEPTED":
                if (receiver != null) {
                    receiver.onAcceptRequestSuccessful(position);
                }
                break;
            case "SUCCESS_NO_REQUEST_ACCEPTED":
                if (receiver != null) {
                    receiver.onAcceptRequestSuccessful(position);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onAcceptRequestFailed();
                }
                break;
            default:
                break;
        }
    }
}
