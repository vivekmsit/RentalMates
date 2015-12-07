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
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.example.vivek.rentalmates.viewholders.FlatListItem;

import java.util.ArrayList;
import java.util.List;

public class FlatListViewAdapter extends RecyclerView.Adapter<FlatListViewAdapter.FlatViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<FlatListItem> data;
    private LayoutInflater inflater;
    private Context context;
    private AppData appData;

    public FlatListViewAdapter(Context context) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        appData = AppData.getInstance();
        this.data = new ArrayList<>();
        updateFlatData();
    }

    public void updateFlatData() {
        this.data.clear();
        for (LocalFlatInfo localFlatInfo : appData.getFlats().values()) {
            this.data.add(new FlatListItem(localFlatInfo));
        }
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

        public FlatViewHolder(View itemView) {
            super(itemView);
            flatName = (TextView) itemView.findViewById(R.id.flatNameTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            FlatListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.flatName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
