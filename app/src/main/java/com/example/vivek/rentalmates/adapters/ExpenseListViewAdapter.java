package com.example.vivek.rentalmates.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.data.AppData;
import com.example.vivek.rentalmates.interfaces.OnDeleteExpenseReceiver;
import com.example.vivek.rentalmates.tasks.DeleteExpenseAsyncTask;
import com.pkmmte.view.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseListViewAdapter extends RecyclerView.Adapter<ExpenseListViewAdapter.ExpenseViewHolder> {

    private static final String TAG = "ExpenseAdapter_Debug";

    private List<ExpenseListItem> data;
    private AppData appData;
    private LayoutInflater inflater;
    private Context context;
    private FragmentManager manager;
    private SharedPreferences prefs;
    private Long primaryExpenseGroupId;

    public ExpenseListViewAdapter(Context context, FragmentManager manager) {
        Log.d(TAG, "inside Constructor");
        appData = AppData.getInstance();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.manager = manager;
        data = new ArrayList<>();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
        primaryExpenseGroupId = prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0);
        updateExpenseData();
    }

    public void updateExpenseData() {
        this.data.clear();
        primaryExpenseGroupId = prefs.getLong(AppConstants.FLAT_EXPENSE_GROUP_ID, 0);
        List<ExpenseData> expenses = appData.getExpenseDataList(primaryExpenseGroupId);
        if (expenses != null) {
            for (ExpenseData expenseData : expenses) {
                this.data.add(new ExpenseListItem(expenseData));
            }
        }
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
            final int currentPosition = getAdapterPosition();
            DialogFragment newFragment = new DialogFragment() {
                @NonNull
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setItems(R.array.expenseMenuOptions, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    break;
                                case 1:
                                    DialogFragment expenseDialog = new DialogFragment() {
                                        private Activity mainTabActivity;
                                        private ProgressDialog progressDialog;

                                        @NonNull
                                        @Override
                                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                                            mainTabActivity = getActivity();
                                            progressDialog = new ProgressDialog(mainTabActivity);
                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                            progressDialog.setIndeterminate(true);
                                            final Long expenseId = appData.getExpenseDataList(primaryExpenseGroupId).get(currentPosition).getId();
                                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                            alertDialogBuilder.setTitle("Confirm");
                                            alertDialogBuilder.setMessage("Do you really want to delete selected expense?");
                                            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DeleteExpenseAsyncTask task = new DeleteExpenseAsyncTask(getActivity(), expenseId);
                                                    task.setOnDeleteExpenseReceiver(new OnDeleteExpenseReceiver() {
                                                        @Override
                                                        public void onExpenseDeleteSuccessful(int position) {
                                                            progressDialog.cancel();
                                                            notifyItemRemoved(position);
                                                            //appData.deleteExpenseData(context, position);
                                                            updateExpenseData();
                                                        }

                                                        @Override
                                                        public void onExpenseDeleteFailed() {
                                                            progressDialog.cancel();
                                                        }
                                                    });
                                                    task.setPosition(currentPosition);
                                                    task.execute();
                                                    progressDialog.setMessage("Deleting Expense");
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
                                    expenseDialog.show(getFragmentManager(), "MyDialog");
                                    break;
                                case 2:
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

    public class ExpenseListItem {
        public final int amount;
        public final String description;
        public final String ownerEmailId;
        public final String userName;
        public final String groupName;
        public final String date;

        public ExpenseListItem(ExpenseData expenseData) {
            this.amount = expenseData.getAmount();
            this.description = expenseData.getDescription();
            this.ownerEmailId = expenseData.getOwnerEmailId();
            this.userName = expenseData.getUserName();
            this.groupName = expenseData.getExpenseGroupName();
            this.date = expenseData.getDate().toString();
        }
    }
}
