package com.example.vivek.rentalmates.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ChatMessagesRecyclerViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.Chat;
import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessage;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.tasks.GetChatMessageListAsyncTask;
import com.example.vivek.rentalmates.tasks.GetPreviousChatIdAsyncTask;
import com.example.vivek.rentalmates.tasks.SendChatMessageAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ChatMessagesActivity extends AppCompatActivity {
    private static final String TAG = "ChatMsgActivity_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private EditText chatMessageEditText;
    private TextView noChatMessagesTextView;
    private LinearLayout sendChatMessageLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatMessagesRecyclerViewAdapter chatMessagesRecyclerViewAdapter;
    private Long chatId;
    private Long receiverId;
    private ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        appData = AppData.getInstance();
        context = getApplicationContext();

        chatId = Long.valueOf(1);

        //Initialize Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.chatMessagesRecyclerView);
        chatMessagesRecyclerViewAdapter = new ChatMessagesRecyclerViewAdapter(this, getSupportFragmentManager(), chatId);
        recyclerView.setAdapter(chatMessagesRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatMessagesSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onSwipeRefresh();
            }
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);

        sendChatMessageLayout = (LinearLayout) findViewById(R.id.sendChatMessageLayout);
        sendChatMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChatMessage();
            }
        });

        chatMessageEditText = (EditText) findViewById(R.id.chatMessageEditText);
        noChatMessagesTextView = (TextView) findViewById(R.id.noChatMessagesTextView);

        //TODO: Check if any existing chat is there with the given roommate here.
        Intent intent = getIntent();
        receiverId = intent.getLongExtra("ROOMMATE_ID", 0);

        getPreviousChatId();

        //Initialize BroadcastReceiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle b = intent.getExtras();
                String message = b.getString("message");
                Toast.makeText(context, "message received: " + message, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("CHAT_MESSAGE_RECEIVED"));

        updateView();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    void updateView() {
        if (appData.getLocalChatMessages(chatId) == null) {
            noChatMessagesTextView.setVisibility(View.VISIBLE);
        } else if (appData.getLocalChatMessages(chatId) != null && appData.getLocalChatMessages(chatId).size() == 0) {
            noChatMessagesTextView.setVisibility(View.VISIBLE);
        } else {
            noChatMessagesTextView.setVisibility(View.GONE);
        }
    }

    private void getPreviousChatId() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        GetPreviousChatIdAsyncTask task = new GetPreviousChatIdAsyncTask(this, receiverId);
        task.setAsyncTaskReceiver(new GetPreviousChatIdAsyncTask.AsyncTaskReceiver() {
            @Override
            public void onAsyncTaskComplete(Chat chat) {
                progressDialog.cancel();
                chatId = chat.getId();
                swipeRefreshLayout.setRefreshing(true);
                onSwipeRefresh();
            }

            @Override
            public void onAsyncTaskFailed() {
                progressDialog.cancel();
            }
        });
        task.execute();
        progressDialog.setMessage("Getting previous Chats with the person");
        progressDialog.show();
    }

    private void sendChatMessage() {
        if (chatMessageEditText.getText().toString().trim().matches("")) {
            Toast.makeText(context, "Please enter message", Toast.LENGTH_LONG).show();
            return;
        }
        String message = chatMessageEditText.getText().toString();
        chatMessageEditText.setText("");
        SendChatMessageAsyncTask task = new SendChatMessageAsyncTask(this, chatId, receiverId, message);
        task.setAsyncTaskReceiver(new SendChatMessageAsyncTask.AsyncTaskReceiver() {
            @Override
            public void onAsyncTaskComplete(ChatMessage chatMessage) {
                Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show();
                chatId = chatMessage.getChatId();
            }

            @Override
            public void onAsyncTaskFailed() {
                Toast.makeText(context, "Failed to send Message", Toast.LENGTH_SHORT).show();

            }
        });
        task.execute();
    }

    private void onSwipeRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetChatMessageListAsyncTask task = new GetChatMessageListAsyncTask(context, chatId);
        task.setAsyncTaskReceiver(new GetChatMessageListAsyncTask.AsyncTaskReceiver() {
            @Override
            public void onAsyncTaskComplete(List<ChatMessage> chatMessages) {
                Log.d(TAG, "inside onContactListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (chatMessages == null) {
                    appData.storeLocalChatMessages(context, chatId, new ArrayList<ChatMessage>());
                    Toast.makeText(context, "No chat messages found", Toast.LENGTH_SHORT).show();
                } else {
                    appData.storeLocalChatMessages(context, chatId, chatMessages);
                    Toast.makeText(context, chatMessages.size() + " chat messages found", Toast.LENGTH_SHORT).show();
                }
                chatMessagesRecyclerViewAdapter.updateData(chatId);
                chatMessagesRecyclerViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onAsyncTaskFailed() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }
}
