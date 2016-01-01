package com.example.vivek.rentalmates.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.vivek.rentalmates.backend.mainApi.model.Contact;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.viewholders.ContactListItem;

import java.util.ArrayList;
import java.util.List;

public class ContactListViewAdapter extends RecyclerView.Adapter<ContactListViewAdapter.ContactViewHolder> {

    private static final String TAG = "ContactAdapter_Debug";

    private List<ContactListItem> data;
    private LayoutInflater inflater;
    private Context context;
    private AppData appData;
    private FragmentManager manager;
    private SharedPreferences prefs;
    private Long flatId;

    public ContactListViewAdapter(Context context, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        this.data = new ArrayList<>();
        appData = AppData.getInstance();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        flatId = prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0);
        updateContactData();
    }

    public void updateContactData() {
        this.data.clear();
        flatId = prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0);
        if (appData.getContacts(flatId) != null) {
            for (Contact contact : appData.getContacts(flatId)) {
                this.data.add(new ContactListItem(contact));
            }
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.contact_list_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        ContactListItem current = data.get(position);
        viewHolder.contactNumber.setText(current.contactNumber.toString());
        viewHolder.contactDetails.setText(current.contactDetails);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView contactNumber;
        TextView contactDetails;

        public ContactViewHolder(View itemView) {
            super(itemView);
            contactNumber = (TextView) itemView.findViewById(R.id.contactNumberTextView);
            contactDetails = (TextView) itemView.findViewById(R.id.contactDetailsTextView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            ContactListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.contactDetails, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            final int currentPosition = getAdapterPosition();
            DialogFragment newFragment = new DialogFragment() {
                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.contactMenuOptions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    break;
                                case 1:
                                    break;
                                default:
                                    dialog.dismiss();
                                    break;
                            }
                            Log.d(TAG, "inside onClick");
                        }
                    });
                    return builder.create();
                }
            };
            newFragment.show(manager, "menus");
            return false;
        }
    }
}
