package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.interfaces.OnDeleteExpenseReceiver;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class DeleteExpenseAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "DeleteExpenseTask_Debug";

    private static ExpenseGroupApi expenseGroupService = null;

    private Long expenseDataId;
    private Context context;
    private IOException ioException;
    private OnDeleteExpenseReceiver receiver;
    private int position;

    public DeleteExpenseAsyncTask(Context context, final Long expenseDataId) {
        this.context = context;
        this.expenseDataId = expenseDataId;
    }

    public void setOnDeleteExpenseReceiver(OnDeleteExpenseReceiver receiver) {
        this.receiver = receiver;
    }

    public void setPosition(int position) {
        this.position = position;
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
            expenseGroupService.deleteExpense(expenseDataId).execute();
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

        Log.d(TAG, "inside onPostExecute() for AddExpenseAsyncTask");

        switch (msg) {
            case "SUCCESS":
                Toast.makeText(context, "ExpenseData deleted", Toast.LENGTH_SHORT).show();
                if (receiver != null) {
                    receiver.onExpenseDeleteSuccessful(position);
                }
                break;
            case "EXCEPTION":
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                if (receiver != null) {
                    receiver.onExpenseDeleteFailed();
                }
                break;
            default:
                break;
        }
    }
}

