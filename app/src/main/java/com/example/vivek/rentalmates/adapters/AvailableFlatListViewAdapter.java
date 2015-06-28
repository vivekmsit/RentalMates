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
import com.example.vivek.rentalmates.viewholders.AvailableFlatListItem;

import java.util.List;

public class AvailableFlatListViewAdapter extends RecyclerView.Adapter<AvailableFlatListViewAdapter.AvailableFlatViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<AvailableFlatListItem> data;
    private LayoutInflater inflater;
    private Context context;

    public AvailableFlatListViewAdapter(Context context, List<AvailableFlatListItem> data) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    public void setData(List<AvailableFlatListItem> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public AvailableFlatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.search_flat_card_view, parent, false);
        return new AvailableFlatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AvailableFlatViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        AvailableFlatListItem current = data.get(position);

        viewHolder.address.setText("Address: " + current.address);
        viewHolder.rentAmount.setText("Rent Amount: " + current.rentAmount);
        viewHolder.securityAmount.setText("Security Amount: " + current.securityAmount);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class AvailableFlatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView address;
        TextView rentAmount;
        TextView securityAmount;

        public AvailableFlatViewHolder(View itemView) {
            super(itemView);

            address = (TextView) itemView.findViewById(R.id.addressTextView);
            rentAmount = (TextView) itemView.findViewById(R.id.rentAmountTextView);
            securityAmount = (TextView) itemView.findViewById(R.id.securityAmountTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            AvailableFlatListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.flatName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
