package com.example.vivek.rentalmates.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnAcceptRequestReceiver;
import com.example.vivek.rentalmates.interfaces.OnRejectRequestReceiver;
import com.example.vivek.rentalmates.tasks.AcceptRequestAsyncTask;
import com.example.vivek.rentalmates.tasks.RejectRequestAsyncTask;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class RequestListViewAdapter extends RecyclerView.Adapter<RequestListViewAdapter.RequestViewHolder> {

    private static final String TAG = "RequestAdapter_Debug";

    private List<RequestListItem> data;
    private LayoutInflater inflater;
    private Context context;
    private AppData appData;
    private FragmentManager manager;

    public RequestListViewAdapter(Context context, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        this.data = new ArrayList<>();
        appData = AppData.getInstance();
        updateRequestData();
    }

    public void updateRequestData() {
        this.data.clear();
        if (appData.getRequests() != null) {
            for (Request request : appData.getRequests()) {
                this.data.add(new RequestListItem(request));
            }
        }
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "inside onCreateViewHolder");
        View view = inflater.inflate(R.layout.request_card_view, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder viewHolder, int position) {
        Log.d(TAG, "inside onBindViewHolder");
        RequestListItem current = data.get(position);
        viewHolder.userName.setText(current.requesterName);
        viewHolder.entityName.setText(current.requestedEntityName);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     */
    class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView userName;
        TextView entityName;
        CircularImageView circularImageView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userNameTextView);
            entityName = (TextView) itemView.findViewById(R.id.entityNameTextView);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.profilePicImageView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "inside onClick");
            RequestListItem currentItem = data.get(getAdapterPosition());
            Toast.makeText(context, currentItem.requesterName, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            final int currentPosition = getAdapterPosition();
            DialogFragment newFragment = new DialogFragment() {
                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.requestMenuOptions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    DialogFragment acceptRequestDialog = new DialogFragment() {
                                        private Activity mainTabActivity;
                                        private ProgressDialog progressDialog;

                                        @NonNull
                                        @Override
                                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                                            mainTabActivity = getActivity();
                                            progressDialog = new ProgressDialog(mainTabActivity);
                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            progressDialog.setIndeterminate(true);
                                            final Long requestId = appData.getRequests().get(currentPosition).getId();
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                            alertDialogBuilder.setTitle("Confirm");
                                            alertDialogBuilder.setMessage("Do you really want to accept request?");
                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.setMessage("Accepting Request");
                                                    progressDialog.show();
                                                    AcceptRequestAsyncTask task = new AcceptRequestAsyncTask(context, requestId, currentPosition);
                                                    task.setOnAcceptRequestReceiver(new OnAcceptRequestReceiver() {
                                                        @Override
                                                        public void onAcceptRequestSuccessful(int position) {
                                                            progressDialog.cancel();
                                                            notifyItemRemoved(position);
                                                            appData.deleteRequest(context, position);
                                                            updateRequestData();
                                                        }

                                                        @Override
                                                        public void onAcceptRequestFailed() {
                                                            progressDialog.cancel();
                                                            Toast.makeText(mainTabActivity, "Unable to accept request", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                    task.execute();
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
                                    acceptRequestDialog.show(getFragmentManager(), "MyDialog");
                                    break;
                                case 1:
                                    DialogFragment rejectRequestDialog = new DialogFragment() {
                                        private Activity mainTabActivity;
                                        private ProgressDialog progressDialog;

                                        @NonNull
                                        @Override
                                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                                            mainTabActivity = getActivity();
                                            progressDialog = new ProgressDialog(mainTabActivity);
                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            progressDialog.setIndeterminate(true);
                                            final Long requestId = appData.getRequests().get(currentPosition).getId();
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                            alertDialogBuilder.setTitle("Confirm");
                                            alertDialogBuilder.setMessage("Do you really want to reject request?");
                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.setMessage("Rejecting Request");
                                                    progressDialog.show();
                                                    RejectRequestAsyncTask task = new RejectRequestAsyncTask(context, requestId, currentPosition);
                                                    task.setOnRejectRequestReceiver(new OnRejectRequestReceiver() {
                                                        @Override
                                                        public void onRejectRequestSuccessful(int position) {
                                                            progressDialog.cancel();
                                                            notifyItemRemoved(position);
                                                            appData.deleteRequest(context, position);
                                                            updateRequestData();
                                                        }

                                                        @Override
                                                        public void onRejectRequestFailed() {
                                                            progressDialog.cancel();
                                                            Toast.makeText(mainTabActivity, "Unable to reject request", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
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
                                    rejectRequestDialog.show(getFragmentManager(), "MyDialog");
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

    public class RequestListItem {
        public final String requesterName;
        public final String requestedEntityName;
        public final String requesterProfilePicLink;

        public RequestListItem(Request request) {
            this.requesterName = request.getRequesterName();
            this.requestedEntityName = request.getRequestedEntityName();
            this.requesterProfilePicLink = request.getRequesterProfilePicLink();
        }
    }
}
