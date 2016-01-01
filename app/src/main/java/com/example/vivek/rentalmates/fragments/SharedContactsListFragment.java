package com.example.vivek.rentalmates.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.activities.FlatManagerActivity;
import com.example.vivek.rentalmates.adapters.ContactListViewAdapter;
import com.example.vivek.rentalmates.backend.mainApi.model.Contact;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnAddContactReceiver;
import com.example.vivek.rentalmates.interfaces.OnContactListReceiver;
import com.example.vivek.rentalmates.tasks.AddContactAsyncTask;
import com.example.vivek.rentalmates.tasks.GetContactListAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class SharedContactsListFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "SharedContacts_Debug";

    private AppData appData;
    private Context context;
    private RecyclerView recyclerView;
    private TextView noContactsTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContactListViewAdapter contactListViewAdapter;
    private SharedPreferences prefs;
    private FlatManagerActivity flatManagerActivity;
    private Long flatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_shared_contacts_list, container, false);
        appData = AppData.getInstance();
        context = getActivity().getApplicationContext();
        flatManagerActivity = (FlatManagerActivity) getActivity();
        flatManagerActivity.registerForActivityEvents("sharedContactsFragment", new FlatManagerActivity.OnActivityEventReceiver() {
            @Override
            public void onEventReceived(String eventType) {
                switch (eventType) {
                    case "addFABPressed":
                        addNewContact();
                        break;
                    case "primaryFlatChanged":
                        contactListViewAdapter.updateContactData();
                        contactListViewAdapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        });
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        flatId = prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0);

        //Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.listContacts);
        contactListViewAdapter = new ContactListViewAdapter(context, getFragmentManager());
        recyclerView.setAdapter(contactListViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        //Initialize SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeListContacts);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onSwipeRefresh();
            }
        });
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        swipeRefreshLayout.setColorSchemeColors(R.color.primaryColor, R.color.purple, R.color.green, R.color.orange);

        noContactsTextView = (TextView) layout.findViewById(R.id.noContactsTextView);
        updateView();
        return layout;
    }

    void updateView() {
        if (appData.getContacts(flatId) == null) {
            noContactsTextView.setVisibility(View.VISIBLE);
        } else if (appData.getContacts(flatId) != null && appData.getContacts(flatId).size() == 0) {
            noContactsTextView.setVisibility(View.VISIBLE);
        } else {
            noContactsTextView.setVisibility(View.GONE);
        }
    }

    public void onSwipeRefresh() {
        Log.d(TAG, "inside onRefresh");
        GetContactListAsyncTask task = new GetContactListAsyncTask(context);
        task.setOnContactListReceiver(new OnContactListReceiver() {
            @Override
            public void onContactListLoadSuccessful(List<Contact> contacts) {
                Log.d(TAG, "inside onContactListLoaded");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (contacts == null) {
                    appData.storeContactList(context, flatId, new ArrayList<Contact>());
                } else {
                    appData.storeContactList(context, flatId, contacts);
                }
                contactListViewAdapter.updateContactData();
                contactListViewAdapter.notifyDataSetChanged();
                updateView();
            }

            @Override
            public void onContactListLoadFailed() {
                Log.d(TAG, "inside onContactListLoadFailed");
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        task.execute();
    }

    public void addNewContact() {
        DialogFragment addNewContactDialog = new android.support.v4.app.DialogFragment() {
            private ProgressDialog progressDialog;
            private EditText contactDetailsEditText;
            private EditText contactNumberEditText;

            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.dialog_fragment_add_contact, null);
                contactDetailsEditText = (EditText) view.findViewById(R.id.contactDetailsEditText);
                contactNumberEditText = (EditText) view.findViewById(R.id.contactNumberEditText);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Enter New Contact details");
                alertDialogBuilder.setView(view);
                alertDialogBuilder.setPositiveButton("Add Contact", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (contactDetailsEditText.getText().toString().trim().matches("")) {
                            Toast.makeText(context, "No contact details entered", Toast.LENGTH_LONG).show();
                            return;
                        } else if (contactNumberEditText.getText().toString().trim().matches("")) {
                            Toast.makeText(context, "No contact number entered", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Contact contact = new Contact();
                        contact.setContactDetails(contactDetailsEditText.getText().toString());
                        contact.setContactNumber(Long.parseLong(contactNumberEditText.getText().toString()));
                        contact.setUploaderId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
                        contact.setFlatId(prefs.getLong(AppConstants.PRIMARY_FLAT_ID, 0));

                        AddContactAsyncTask task = new AddContactAsyncTask(context, contact);
                        task.setOnAddContactReceiver(new OnAddContactReceiver() {
                            @Override
                            public void onAddContactSuccessful(Contact contact) {
                                progressDialog.cancel();
                            }

                            @Override
                            public void onAddContactFailed() {
                                progressDialog.cancel();
                            }
                        });
                        task.execute();
                        progressDialog.setMessage("Adding new Contact " + contactDetailsEditText.getText().toString());
                        progressDialog.show();
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Cancel: onClick");
                        dialog.dismiss();
                    }
                });
                return alertDialogBuilder.create();
            }
        };
        addNewContactDialog.show(getFragmentManager(), "Fragment");
    }
}
