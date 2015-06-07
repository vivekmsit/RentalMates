package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.dialogs.ExpenseMenuDialog;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.viewholders.ExpenseListItem;
import com.pkmmte.view.CircularImageView;

import java.util.List;

public class ExpenseListViewAdapter extends RecyclerView.Adapter<ExpenseListViewAdapter.ExpenseViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<ExpenseListItem> data;
    private AppData appData;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager manager;

    public ExpenseListViewAdapter(Context context, List<ExpenseListItem> data, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        appData = AppData.getInstance();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.manager = manager;
    }

    public void setData(List<ExpenseListItem> data) {
        this.data.clear();
        this.data.addAll(data);
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
        ExpenseListItem current = data.get(position);
        if (appData.getProfilePicturesPath().containsKey(current.ownerEmailId)) {
            viewHolder.circularImageView.setImageBitmap(appData.getProfilePictureBitmap(current.ownerEmailId));
        } else {
            //show ic_launcher in place of profile picture if profile picture is not available
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            Bitmap newBitmap = Bitmap.createScaledBitmap(bm, 200, 200, true);
            viewHolder.circularImageView.setImageBitmap(newBitmap);
        }
        viewHolder.amount.setText("Rs " + Integer.toString(current.amount));
        viewHolder.description.setText(current.description);
        viewHolder.userName.setText(current.userName);
        viewHolder.groupName.setText(current.groupName);
        viewHolder.date.setText(current.date);
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
        TextView amount;
        TextView description;
        TextView userName;
        TextView groupName;
        TextView date;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.expenseImageView);
            amount = (TextView) itemView.findViewById(R.id.amountTextView);
            description = (TextView) itemView.findViewById(R.id.descriptionTextView);
            userName = (TextView) itemView.findViewById(R.id.userNameTextView);
            groupName = (TextView) itemView.findViewById(R.id.groupNameTextView);
            date = (TextView) itemView.findViewById(R.id.dateTextView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            ExpenseListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.description, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            DialogFragment newFragment = new ExpenseMenuDialog();
            ExpenseData expenseData = appData.getExpenses().get(getAdapterPosition());
            Bundle bundle = new Bundle();
            bundle.putLong("ExpenseId", expenseData.getId());
            bundle.putInt("position", getAdapterPosition());
            newFragment.setArguments(bundle);
            newFragment.show(manager, "menus");
            return false;
        }
    }
}
