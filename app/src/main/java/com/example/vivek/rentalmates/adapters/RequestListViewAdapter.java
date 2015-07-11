package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.viewholders.RequestListItem;
import com.pkmmte.view.CircularImageView;

import java.util.List;

public class RequestListViewAdapter extends RecyclerView.Adapter<RequestListViewAdapter.RequestViewHolder> {

    private static final String TAG = "RequestAdapter_Debug";

    private List<RequestListItem> data;
    private LayoutInflater inflater;
    private Context context;

    public RequestListViewAdapter(Context context, List<RequestListItem> data) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    public void setData(List<RequestListItem> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.request_card_view, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        RequestListItem current = data.get(position);
        viewHolder.userName.setText(current.requesterName);
        viewHolder.entityName.setText(current.requestedEntityName);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView userName;
        TextView entityName;
        CircularImageView circularImageView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userNameTextView);
            entityName = (TextView) itemView.findViewById(R.id.entityNameTextView);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.profilePicImageView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            RequestListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.requesterName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
