package com.example.vivek.rentalmates.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.vivek.rentalmates.data.LocalChat;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<ChatsRecyclerViewAdapter.ExpenseViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<ChatListItem> data;
    private AppData appData;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager manager;

    public ChatsRecyclerViewAdapter(Context context, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        appData = AppData.getInstance();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        data = new ArrayList<>();
        updateChatData();
    }

    public void updateChatData() {
        this.data.clear();
        for (LocalChat localChat : appData.getLocalChatHashMap().values()) {
            this.data.add(new ChatListItem(localChat));
        }
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.expense_data_list_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        ChatListItem current = data.get(position);
        if (appData.getProfilePicturesPath().containsKey(current.userEmailId)) {
            viewHolder.circularImageView.setImageBitmap(appData.getProfilePictureBitmap(current.userEmailId));
        } else {
            //show ic_launcher in place of profile picture if profile picture is not available
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            Bitmap newBitmap = Bitmap.createScaledBitmap(bm, 200, 200, true);
            viewHolder.circularImageView.setImageBitmap(newBitmap);
        }
        viewHolder.userName.setText(current.userName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CircularImageView circularImageView;
        TextView userName;
        TextView date;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.userProfileImageView);
            userName = (TextView) itemView.findViewById(R.id.userNameTextView);
            date = (TextView) itemView.findViewById(R.id.dateTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            ChatListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.userName, Toast.LENGTH_SHORT).show();
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

    public class ChatListItem {
        public final String profileLink;
        public final String userName;
        public final String userEmailId;
        public final String date;

        public ChatListItem(LocalChat localChat) {
            this.profileLink = "adf";
            this.userName = "vivek sharma";
            this.userEmailId = "vivekmsit@gmail.com";
            this.date = "date";
        }
    }
}
