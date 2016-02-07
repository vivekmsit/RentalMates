package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessage;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

public class SendChatMessageAsyncTask extends AsyncTask<Context, Void, String> {
    private static final String TAG = "SendChatMessage_Debug";
    private static MainApi mainApi = null;

    private Context context;
    private Long chatId;
    private Long receiverId;
    private String content;
    private ChatMessage chatMessage;
    private SharedPreferences prefs;
    private IOException ioException;
    private AsyncTaskReceiver receiver;

    public SendChatMessageAsyncTask(Context context, final Long chatId, final Long receiverId, final String content) {
        this.context = context;
        this.chatId = chatId;
        this.receiverId = receiverId;
        this.content = content;

        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public interface AsyncTaskReceiver {
        void onAsyncTaskComplete(ChatMessage chatMessage);

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
            chatMessage = mainApi.sendChatMessage(chatId, content, receiverId, userProfileId).execute();
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

        Log.d(TAG, "inside onPostExecute() for SendChatMessageAsyncTask");

        switch (msg) {
            case "SUCCESS":
                if (receiver != null) {
                    receiver.onAsyncTaskComplete(chatMessage);
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
