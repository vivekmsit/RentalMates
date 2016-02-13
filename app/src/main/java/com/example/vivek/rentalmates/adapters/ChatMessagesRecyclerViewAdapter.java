package com.example.vivek.rentalmates.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessagesRecyclerViewAdapter extends RecyclerView.Adapter<ChatMessagesRecyclerViewAdapter.ChatMessageViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<ChatMessageListItem> data;
    private AppData appData;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager manager;
    private Long chatId;

    public ChatMessagesRecyclerViewAdapter(Context context, FragmentManager manager, Long chatId) {
        appData = AppData.getInstance();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        this.chatId = chatId;
        data = new ArrayList<>();
        updateData(chatId);
    }

    public void updateData(Long chatId) {
        this.chatId = chatId;
        this.data.clear();
        if (appData.getLocalChatMessages(chatId) == null) {
            return;
        }
        for (LocalChatMessage localChatMessage : appData.getLocalChatMessages(chatId)) {
            this.data.add(new ChatMessageListItem(localChatMessage));
        }
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.chat_message_list_item, parent, false);
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        ChatMessageListItem current = data.get(position);
        viewHolder.content.setText(current.content);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class ChatMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView content;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.contentTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            ChatMessageListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.content, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            final int currentPosition = getAdapterPosition();
            DialogFragment newFragment = new DialogFragment() {
                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.expenseMenuOptions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    break;
                                case 1:
                                    deleteChat(getFragmentManager(), currentPosition);
                                    break;
                                case 2:
                                    break;
                                default:
                                    dialog.dismiss();
                                    break;
                            }
                            Log.d(TAG, "inside onClick");
                        }
                    });
                    return builder.create();
                }
            };
            newFragment.show(manager, "menus");
            return false;
        }

        private void deleteChat(FragmentManager fragmentManager, final int currentPosition) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
        }
    }

    public class ChatMessageListItem {
        public final String content;
        public final Long senderId;
        public final Long receiverId;

        public ChatMessageListItem(LocalChatMessage localChatMessage) {
            this.content = localChatMessage.getContent();
            this.senderId = localChatMessage.getSenderId();
            this.receiverId = localChatMessage.getReceiverId();
        }
    }
}
