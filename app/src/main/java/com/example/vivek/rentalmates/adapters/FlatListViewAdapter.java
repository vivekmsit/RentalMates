package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.viewholders.FlatListItem;

import java.util.List;

public class FlatListViewAdapter extends RecyclerView.Adapter<FlatListViewAdapter.FlatViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<FlatListItem> data;
    private AppData appData;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager manager;

    public FlatListViewAdapter(Context context, List<FlatListItem> data, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        appData = AppData.getInstance();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.manager = manager;
    }

    public void setData(List<FlatListItem> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public FlatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.flat_list_item, parent, false);
        return new FlatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FlatViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        FlatListItem current = data.get(position);
        viewHolder.flatName.setText(current.flatName);
        viewHolder.date.setText(current.date);
        viewHolder.location.setText(current.location);
        viewHolder.members.setText(current.ownerName);
        viewHolder.ownerName.setText(current.ownerName);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class FlatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView flatName;
        TextView ownerName;
        TextView location;
        TextView members;
        TextView date;

        public FlatViewHolder(View itemView) {
            super(itemView);
            flatName = (TextView) itemView.findViewById(R.id.flatNameTextView);
            ownerName = (TextView) itemView.findViewById(R.id.ownerTextView);
            location = (TextView) itemView.findViewById(R.id.locationTextView);
            members = (TextView) itemView.findViewById(R.id.membersTextView);
            date = (TextView) itemView.findViewById(R.id.dateTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            FlatListItem currentItem = data.get(getPosition());
            Toast.makeText(context, currentItem.flatName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
