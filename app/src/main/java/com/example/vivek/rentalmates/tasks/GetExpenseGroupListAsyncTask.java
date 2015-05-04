package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.activities.MainActivity;
import com.example.vivek.rentalmates.backend.userProfileApi.UserProfileApi;
import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroupCollection;
import com.example.vivek.rentalmates.interfaces.OnExpenseGroupListReceiver;
import com.example.vivek.rentalmates.others.AppConstants;
import com.example.vivek.rentalmates.others.AppData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class GetExpenseGroupListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "GetEGroupListTask_Debug";

    private static UserProfileApi ufService = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private AppData appData;
    private OnExpenseGroupListReceiver receiver;
    private List<com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup> expenseGroups;

    public GetExpenseGroupListAsyncTask(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        this.appData = AppData.getInstance();
    }

    public void setOnExpenseGroupListReceiver(OnExpenseGroupListReceiver receiver) {
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
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            ExpenseGroupCollection expenseGroupCollection = ufService.getExpenseGroupList(userProfileId).execute();
            if (expenseGroupCollection == null) {
                Log.d(TAG, "expenseGroups is null");
                msg = "SUCCESS_NO_EXPENSE_GROUPS";
            } else {
                this.expenseGroups = expenseGroupCollection.getItems();
                this.appData.storeExpenseGroupList(context, this.expenseGroups);
                msg = "SUCCESS_EXPENSE_GROUPS";
            }
            Log.d(TAG, "inside getExpenseGroupList");
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for GetExpenseGroupListAsyncTask");

        switch (msg) {
            case "SUCCESS_EXPENSE_GROUPS":
                Toast.makeText(context, "ExpenseGroup List retrieved successfully", Toast.LENGTH_SHORT).show();
                if (receiver != null) {
                    receiver.onExpenseGroupListLoadSuccessful();
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onExpenseGroupListLoadFailed();
                }
                break;
            default:
                break;
        }
    }
}
