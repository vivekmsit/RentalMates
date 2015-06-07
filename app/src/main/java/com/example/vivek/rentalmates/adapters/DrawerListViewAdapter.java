package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.DeveloperModeActivity;
import com.example.vivek.rentalmates.activities.ManageExpenseGroupsActivity;
import com.example.vivek.rentalmates.activities.ManageFlatsActivity;
import com.example.vivek.rentalmates.activities.MyLoginActivity;
import com.example.vivek.rentalmates.fragments.NewsFeedFragment;
import com.example.vivek.rentalmates.viewholders.DrawerListItem;

import java.util.Collections;
import java.util.List;

public class DrawerListViewAdapter extends RecyclerView.Adapter<DrawerListViewAdapter.DrawerViewHolder> {
    private static final String TAG = "DrawerListAdapter_Debug";

    private List<DrawerListItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private int currentPosition;

    public DrawerListViewAdapter(Context context, DrawerLayout drawerLayout, FragmentManager fragmentManager, List<DrawerListItem> data) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.fragmentManager = fragmentManager;
        this.data = data;
        currentPosition = -1;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.drawer_list_item, parent, false);
        return new DrawerViewHolder(view);
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

    public void updateDrawerLayoutMainFragment() {
        Log.d(TAG, "inside startFragment" + currentPosition);
        if (currentPosition == -1) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Intent intent;
        switch (currentPosition) {
            case 0:
                break;
            case 1:
                intent = new Intent(context, ManageFlatsActivity.class);
                context.startActivity(intent);
                break;
            case 2:
                intent = new Intent(context, ManageExpenseGroupsActivity.class);
                context.startActivity(intent);
                break;
            case 3:
                intent = new Intent(context, MyLoginActivity.class);
                context.startActivity(intent);
                break;
            case 4:
                intent = new Intent(context, DeveloperModeActivity.class);
                context.startActivity(intent);
                break;
            case 5:
                ft.replace(R.id.mainDrawerView, new NewsFeedFragment());
                ft.addToBackStack("NewsFeedFragment");
                ft.commit();
                break;
            default:
                break;
        }
        currentPosition = -1;
    }

    class DrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView imageView;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.drawerListItemTextView1);
            imageView = (ImageView) itemView.findViewById(R.id.drawerListImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //String currentItem = data.get(getPosition()).title;
            currentPosition = getAdapterPosition();
            drawerLayout.closeDrawers();
        }
    }
}
