package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.interfaces.OnCreateExpenseGroupReceiver;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class CreateExpenseGroupAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "CreateEGroupATask_Debug";

    private static ExpenseGroupApi expenseGroupService = null;
    private ExpenseGroup expenseGroup;
    private ExpenseGroup uploadedExpenseGroup;
    private Context context;
    private IOException ioException;
    private OnCreateExpenseGroupReceiver receiver;

    public CreateExpenseGroupAsyncTask(Context context, final ExpenseGroup expenseData) {
        this.context = context;
        this.expenseGroup = expenseData;
    }

    public void setOnCreateExpenseGroupReceiver(OnCreateExpenseGroupReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (expenseGroupService == null) {
            ExpenseGroupApi.Builder builder1 = new ExpenseGroupApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            expenseGroupService = builder1.build();
        }
        try {
            uploadedExpenseGroup = expenseGroupService.createExpenseGroup(expenseGroup).execute();
            if (uploadedExpenseGroup == null) {
                Log.d(TAG, "expense is null");
                msg = "FAILED";
            } else {
                Log.d(TAG, "expense successfully uploaded");
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

        Log.d(TAG, "inside onPostExecute() for CreateExpenseGroupAsyncTask");

        switch (msg) {
            case "SUCCESS":
                receiver.onCreateExpenseGroupSuccessful(uploadedExpenseGroup);
                break;
            case "EXCEPTION":
                receiver.onCreateExpenseGroupFailed();
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;
            default:
                receiver.onCreateExpenseGroupFailed();
                Log.d(TAG, "Unable to upload ExpenseGroup");
                Toast.makeText(context, "Unable to upload ExpenseGroup", Toast.LENGTH_LONG).show();
                break;
        }
    }
}

