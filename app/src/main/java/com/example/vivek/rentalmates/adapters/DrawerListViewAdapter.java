package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.viewholders.DrawerListItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by vivek on 4/8/2015.
 */
public class DrawerListViewAdapter extends RecyclerView.Adapter <DrawerListViewAdapter.DrawerViewHolder>{
    private static final String TAG = "DrawerListAdapter_Debug";

    private List<DrawerListItem> data = Collections.emptyList();
    private LayoutInflater inflater;

    public DrawerListViewAdapter(Context context, List<DrawerListItem> data) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.data = data;
    }
    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.drawer_list_item, parent, false);
        DrawerViewHolder holder = new DrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder viewHolder, int i) {
        Log.d(TAG, "inside onBindViewHolder");
        DrawerListItem current = data.get(i);
        viewHolder.title.setText(current.title);
        viewHolder.imageView.setImageResource(current.iconId);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imageView;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.drawerListItemTextView1);
            imageView = (ImageView) itemView.findViewById(R.id.drawerListImageView);
        }
    }
}
