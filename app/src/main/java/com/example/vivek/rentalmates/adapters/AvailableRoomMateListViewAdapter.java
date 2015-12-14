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
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.data.AppData;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class AvailableRoomMateListViewAdapter extends RecyclerView.Adapter<AvailableRoomMateListViewAdapter.AvailableRoomMateViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<RoomMateListItem> data;
    private LayoutInflater inflater;
    private Context context;
    private AppData appData;

    public AvailableRoomMateListViewAdapter(Context context) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.appData = AppData.getInstance();
        this.data = new ArrayList<>();
        updateAvailableFlatsData();
    }

    public void updateAvailableFlatsData() {
        this.data.clear();
        for (FlatSearchCriteria flatSearchCriteria : appData.getRoomMateList()) {
            this.data.add(new RoomMateListItem(flatSearchCriteria));
        }
    }

    @Override
    public AvailableRoomMateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.search_roommate_card_view, parent, false);
        return new AvailableRoomMateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AvailableRoomMateViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        RoomMateListItem current = data.get(position);

        /*
        if (appData.getProfilePicturesPath().containsKey(current.emailId)) {
            viewHolder.circularImageView.setImageBitmap(appData.getProfilePictureBitmap(current.emailId));
        } else {
            //show ic_launcher in place of profile picture if profile picture is not available
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
            Bitmap newBitmap = Bitmap.createScaledBitmap(bm, 200, 200, true);
            viewHolder.circularImageView.setImageBitmap(newBitmap);
        }*/

        if (appData.getProfilePicturesPath().containsKey("vivekmsit@gmail.com")) {
            viewHolder.circularImageView.setImageBitmap(appData.getProfilePictureBitmap("vivekmsit@gmail.com"));
        }
        viewHolder.roomMateName.setText(current.name);
        viewHolder.rentRange.setText("Rs. " + current.minRent + " to " + current.maxRent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class AvailableRoomMateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CircularImageView circularImageView;
        TextView roomMateName;
        TextView rentRange;

        public AvailableRoomMateViewHolder(View itemView) {
            super(itemView);

            circularImageView = (CircularImageView) itemView.findViewById(R.id.roomMateProfileImageView);
            roomMateName = (TextView) itemView.findViewById(R.id.seekerNameTextView);
            rentRange = (TextView) itemView.findViewById(R.id.rentRangeTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            RoomMateListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.name, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    class RoomMateListItem {
        public final String name;
        public final String profilePictureLink;
        public final int minRent;
        public final int maxRent;

        public RoomMateListItem(FlatSearchCriteria flatSearchCriteria) {
            this.name = flatSearchCriteria.getRequesterName();
            this.minRent = flatSearchCriteria.getMinRentAmountPerPerson();
            this.maxRent = flatSearchCriteria.getMaxRentAmountPerPerson();
            this.profilePictureLink = flatSearchCriteria.getRequesterProfilePicture();
        }
    }
}
