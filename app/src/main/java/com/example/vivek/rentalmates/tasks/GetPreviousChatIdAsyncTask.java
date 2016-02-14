package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.Chat;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class GetPreviousChatIdAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "ChatListTask_Debug";

    private static MainApi mainApi = null;
    private Context context;
    private SharedPreferences prefs;
    private IOException ioException;
    private Chat chat;
    private Long receiverId;
    private AsyncTaskReceiver receiver;

    public GetPreviousChatIdAsyncTask(Context context, Long receiverId) {
        this.context = context;
        this.receiverId = receiverId;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public interface AsyncTaskReceiver {
        void onAsyncTaskComplete(Chat chat);

        void onAsyncTaskFailed();
    }

    public void setAsyncTaskReceiver(AsyncTaskReceiver receiver) {
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
            Long userProfileId = prefs.getLong(AppConstants.USER_PROFILE_ID, 0);
            chat = mainApi.getPreviousChatId(userProfileId, receiverId).execute();
            msg = "SUCCESS_CHAT_ID";
        } catch (IOException e) {
            ioException = e;
            msg = "EXCEPTION";
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {

        Log.d(TAG, "inside onPostExecute() for GetPrevChatIdAsyncTask");

        switch (msg) {
            case "SUCCESS_CHAT_ID":
                if (receiver != null) {
                    receiver.onAsyncTaskComplete(chat);
                }
                break;

            case "EXCEPTION":
                if (receiver != null) {
                    receiver.onAsyncTaskFailed();
                }
                Log.d(TAG, "IOException: " + ioException.getMessage());
                Toast.makeText(context, "IOException: " + ioException.getMessage(), Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
    }
}
