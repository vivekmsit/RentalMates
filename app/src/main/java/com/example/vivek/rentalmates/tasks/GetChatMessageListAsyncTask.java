package com.example.vivek.rentalmates.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.mainApi.MainApi;
import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessage;
import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessageCollection;
import com.example.vivek.rentalmates.data.AppConstants;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;

public class GetChatMessageListAsyncTask extends AsyncTask<Context, Void, String> {

    private static final String TAG = "ChatMsgListTask_Debug";

    private static MainApi mainApi = null;
    private Context context;
    private IOException ioException;
    private Long chatId;
    private List<ChatMessage> chatMessages;
    private AsyncTaskReceiver receiver;

    public GetChatMessageListAsyncTask(Context context, Long chatId) {
        this.context = context;
        this.chatId = chatId;
    }

    public interface AsyncTaskReceiver {
        void onAsyncTaskComplete(List<ChatMessage> chats);

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
            ChatMessageCollection chatMessageCollection = mainApi.getChatMessageList(chatId).execute();
            if (chatMessageCollection == null) {
                Log.d(TAG, "contacts is null");
                msg = "SUCCESS_NO_CHATMESSAGES";
            } else {
                chatMessages = chatMessageCollection.getItems();
                msg = "SUCCESS_CHATMESSAGES";
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

        Log.d(TAG, "inside onPostExecute() for GetMsgChatListAsyncTask");

        switch (msg) {
            case "SUCCESS_CHATMESSAGES":
                if (receiver != null) {
                    receiver.onAsyncTaskComplete(chatMessages);
                }
                break;

            case "SUCCESS_NO_CHATMESSAGES":
                //rare case
                if (receiver != null) {
                    receiver.onAsyncTaskComplete(null);
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
