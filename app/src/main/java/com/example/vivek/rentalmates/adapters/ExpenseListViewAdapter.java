package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.viewholders.ExpenseListItem;

import java.util.List;

/**
 * Created by vivek on 3/10/2015.
 */
public class ExpenseListViewAdapter extends RecyclerView.Adapter<ExpenseListViewAdapter.ExpenseViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<ExpenseListItem> data;
    private AppData appData;
    private LayoutInflater inflater;
    private Context context;

    public ExpenseListViewAdapter(Context context, List<ExpenseListItem> data) {
        Log.d(TAG, "inside Constructor");
        appData = AppData.getInstance();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
    }

    public void setData(List<ExpenseListItem> data) {
        this.data.clear();
        this.data.addAll(data);
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.expense_data_list_item, parent, false);
        ExpenseViewHolder holder = new ExpenseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        ExpenseListItem current = data.get(position);
        viewHolder.imageView.setImageBitmap(appData.getProfilePictureBitmap(context, current.ownerEmailId));
        viewHolder.amount.setText(Integer.toString(current.amount));
        viewHolder.description.setText(current.description);
        viewHolder.owner.setText(current.ownerEmailId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView imageView;
        TextView amount;
        TextView description;
        TextView owner;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            amount = (TextView) itemView.findViewById(R.id.amountListView);
            description = (TextView) itemView.findViewById(R.id.descriptionListView);
            owner = (TextView) itemView.findViewById(R.id.ownerListView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            ExpenseListItem currentItem = data.get(getPosition());
            Toast.makeText(context, currentItem.description, Toast.LENGTH_SHORT).show();
        }
    }
}
