package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.ExpenseGroupApi;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseDataCollection;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class GetExpenseListAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "GetExpenseTask_Debug";
    private static ExpenseGroupApi expenseService = null;
    private Context context;
    private IOException ioException;
    private List<ExpenseData> expenses;
    private OnExecuteTaskReceiver receiver;
    private Long expenseGroupId;

    public interface OnExecuteTaskReceiver {
        void onTaskCompleted(List<ExpenseData> expenses);

        void onTaskFailed();
    }

    public void setOnExecuteTaskReceiver(OnExecuteTaskReceiver receiver) {
        this.receiver = receiver;
    }

    public GetExpenseListAsyncTask(Context context, Long expenseGroupId) {
        this.context = context;
        this.expenseGroupId = expenseGroupId;
    }

    @Override
    protected String doInBackground(Context... params) {
        String msg;
        if (expenseService == null) {
            ExpenseGroupApi.Builder builder1 = new ExpenseGroupApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl(AppConstants.BACKEND_ROOT_URL);
            expenseService = builder1.build();
        }
        try {
            ExpenseDataCollection expensesCollection = expenseService.getExpenses(expenseGroupId).execute();
            msg = "SUCCESS";
            if (expensesCollection == null) {
                Log.d(TAG, "expenses is null");
            } else {
                expenses = expensesCollection.getItems();
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

        Log.d(TAG, "inside onPostExecute() for GetExpenseDataListAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onTaskCompleted(expenses);
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


