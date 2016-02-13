package com.example.vivek.rentalmates.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.adapters.ChatMessagesRecyclerViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessage;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.tasks.GetChatMessageListAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ChatMessagesActivity extends AppCompatActivity {
    private static final String TAG = "ChatMsgActivity_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView noChatMessagesTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatMessagesRecyclerViewAdapter chatMessagesRecyclerViewAdapter;
    private Long chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_messages);

        appData = AppData.getInstance();
        context = getApplicationContext();

        chatId = Long.valueOf(5);

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

        noChatMessagesTextView = (TextView) findViewById(R.id.noChatMessagesTextView);

        //TODO: Check if any existing chat is there with the given roommate here.
        Intent intent = getIntent();
        Long roomMateId = intent.getLongExtra("ROOMMATE_ID", 0);

        updateView();
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
                chatMessagesRecyclerViewAdapter.updateData();
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
