package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.flatInfoApi.FlatInfoApi;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.services.BackendApiService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class RegisterNewFlatAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "RentalMatesDebug";

    private static FlatInfoApi flatService = null;
    private FlatInfo fi;
    private FlatInfo newFlatInfo;
    private Context context;
    private IOException ioException;
    private OnRegisterNewFlatReceiver receiver;

    public RegisterNewFlatAsyncTask(Context context, final FlatInfo flatInfo) {
        this.context = context;
        this.fi = flatInfo;
    }

    public void setOnRegisterNewFlatReceiver(OnRegisterNewFlatReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg = "";
        if (flatService == null) {
            FlatInfoApi.Builder builder1 = new FlatInfoApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            flatService = builder1.build();
        }
        try {
            newFlatInfo = flatService.registerNewFlat(fi).execute();
            String status = newFlatInfo.getCreateFlatResult();
            if (status.equals("NEW_FLAT_INFO")) {
                BackendApiService.storePrimaryFlatId(this.context, newFlatInfo.getFlatId());
                BackendApiService.storePrimaryFlatName(this.context, newFlatInfo.getFlatName());
                BackendApiService.storeFlatExpenseGroupId(this.context, newFlatInfo.getExpenseGroupId());
                msg = "SUCCESS_NEW_FLAT";
            } else if (status.equals("OLD_FLAT_INFO")) {
                msg = "SUCCESS_OLD_FLAT";
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
            case "SUCCESS_NEW_FLAT":
                if (receiver != null) {
                    receiver.onRegisterNewFlatSuccessful(newFlatInfo);
                }
                break;
            case "SUCCESS_OLD_FLAT":
                if (receiver != null) {
                    receiver.onRegisterNewFlatSuccessful(null);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }
}
