package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.others.AppData;
import com.example.vivek.rentalmates.viewholders.ExpenseListViewItem;

import java.util.List;

/**
 * Created by vivek on 3/10/2015.
 */
public class ExpenseListViewAdapter extends ArrayAdapter<ExpenseListViewItem> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private AppData appData;

    public ExpenseListViewAdapter(Context context, List<ExpenseListViewItem> items) {
        super(context, R.layout.expense_data_list_item, items);
        appData = AppData.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "inside getView()");
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.expense_data_list_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            viewHolder.amount = (TextView) convertView.findViewById(R.id.amountListView);
            viewHolder.description = (TextView) convertView.findViewById(R.id.descriptionListView);
            viewHolder.owner = (TextView) convertView.findViewById(R.id.ownerListView);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        ExpenseListViewItem item = getItem(position);
        viewHolder.imageView.setImageBitmap(appData.getProfilePictureBitmap(getContext(), item.ownerEmailId));
        viewHolder.amount.setText(Integer.toString(item.amount));
        viewHolder.description.setText(item.description);
        viewHolder.owner.setText(item.ownerEmailId);

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    private static class ViewHolder {
        ImageView imageView;
        TextView amount;
        TextView description;
        TextView owner;
    }
}
