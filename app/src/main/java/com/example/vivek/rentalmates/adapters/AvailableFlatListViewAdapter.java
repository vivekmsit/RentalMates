package com.example.vivek.rentalmates.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FlatInfoActivity;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.data.LocalFlatInfo;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class AvailableFlatListViewAdapter extends RecyclerView.Adapter<AvailableFlatListViewAdapter.AvailableFlatViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<AvailableFlatListItem> data;
    private LayoutInflater inflater;
    private Context context;
    private AppData appData;

    public AvailableFlatListViewAdapter(Context context) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.appData = AppData.getInstance();
        this.data = new ArrayList<>();
        updateAvailableFlatsData();
    }

    public void updateAvailableFlatsData() {
        this.data.clear();
        for (LocalFlatInfo flatInfo : appData.getAvailableFlats().values()) {
            this.data.add(new AvailableFlatListItem(flatInfo));
        }
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

        if (appData.getProfilePicturesPath().containsKey(current.emailId)) {
            viewHolder.circularImageView.setImageBitmap(appData.getProfilePictureBitmap(current.emailId));
        } else {
            //show ic_launcher in place of profile picture if profile picture is not available
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            Bitmap newBitmap = Bitmap.createScaledBitmap(bm, 200, 200, true);
            viewHolder.circularImageView.setImageBitmap(newBitmap);
        }

        viewHolder.address.setText("Address: " + current.address);
        viewHolder.rentAmount.setText(current.rentAmount);
        viewHolder.securityAmount.setText(current.securityAmount);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), current.flatPictureResourceId);
        Bitmap newBitmap = Bitmap.createScaledBitmap(bm, 600, 400, true);
        viewHolder.flatPicture.setImageBitmap(newBitmap);
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
        CircularImageView circularImageView;
        ImageView flatPicture;
        TextView address;
        TextView rentAmount;
        TextView securityAmount;

        public AvailableFlatViewHolder(View itemView) {
            super(itemView);

            circularImageView = (CircularImageView) itemView.findViewById(R.id.ownerProfileImageView);
            flatPicture = (ImageView) itemView.findViewById(R.id.flatMainPic);
            address = (TextView) itemView.findViewById(R.id.addressTextView);
            rentAmount = (TextView) itemView.findViewById(R.id.rentAmountValueTextView);
            securityAmount = (TextView) itemView.findViewById(R.id.securityAmountValueTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            AvailableFlatListItem currentItem = data.get(getAdapterPosition());
            Intent intent = new Intent(context, FlatInfoActivity.class);
            intent.putExtra("FLAT_ID", currentItem.flatId);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public class AvailableFlatListItem {
        public final String flatName;
        public final String location;
        public final String address;
        public final String rentAmount;
        public final String securityAmount;
        public final String emailId;
        public final int flatPictureResourceId;
        public final Long flatId;

        public AvailableFlatListItem(LocalFlatInfo flatInfo) {
            this.flatPictureResourceId = R.drawable.flatview2;
            this.flatName = flatInfo.getFlatName();
            this.location = flatInfo.getCity();
            this.address = flatInfo.getAddress();
            this.emailId = flatInfo.getOwnerEmailId();
            if (flatInfo.getNumberOfUsers() == 0) {
                this.rentAmount = "0";
                this.securityAmount = "0";
            } else {
                this.rentAmount = String.valueOf(flatInfo.getRentAmount() / flatInfo.getNumberOfUsers());
                this.securityAmount = String.valueOf(flatInfo.getSecurityAmount() / flatInfo.getNumberOfUsers());
            }
            this.flatId = flatInfo.getFlatId();
        }
    }
}
