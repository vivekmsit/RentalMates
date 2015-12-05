package com.example.vivek.rentalmates.library;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.dialogs.CurrentLocationMapDialog;
import com.example.vivek.rentalmates.dialogs.FlatAmenitiesDialog;
import com.example.vivek.rentalmates.dialogs.FlatNameDialog;
import com.example.vivek.rentalmates.dialogs.FlatPropertiesDialog;
import com.example.vivek.rentalmates.dialogs.FlatRentDetailsDialog;
import com.example.vivek.rentalmates.interfaces.OnRegisterNewFlatReceiver;
import com.example.vivek.rentalmates.tasks.RegisterNewFlatAsyncTask;

import java.util.ArrayList;
import java.util.List;

public class RegisterNewFlatTask {
    private static final String TAG = "RegisterNewFlat_Debug";
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private OnRegisterNewFlatTask receiver;
    private String type;
    private Activity activity;
    private Context context;

    public interface OnRegisterNewFlatTask {
        void onRegisterNewFlatTaskSuccess(FlatInfo newFlatInfo);

        void onRegisterNewFlatTaskFailed();
    }

    public RegisterNewFlatTask(Activity activity, FragmentManager fragmentManager, String type) {
        this.fragmentManager = fragmentManager;
        this.type = type;
        this.activity = activity;
        this.context = activity.getApplicationContext();
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnRegisterNewFlatTask(OnRegisterNewFlatTask receiver) {
        this.receiver = receiver;
    }

    public void execute() {
        final FlatInfo flatInfo = new FlatInfo();
        flatInfo.setOwnerEmailId(prefs.getString(AppConstants.EMAIL_ID, "no_email_id"));
        flatInfo.setOwnerId(prefs.getLong(AppConstants.USER_PROFILE_ID, 0));
        flatInfo.setFlatAddress("Bangalore");
        flatInfo.setCity("Bangalore");
        FlatNameDialog flatNameDialog = new FlatNameDialog();
        flatNameDialog.setOnDialogResultListener(new FlatNameDialog.OnDialogResultListener() {
            @Override
            public void onPositiveResult(String flatName) {
                flatInfo.setFlatName(flatName);
                FlatRentDetailsDialog flatRentDetailsDialog = new FlatRentDetailsDialog();
                flatRentDetailsDialog.setOnDialogResultListener(new FlatRentDetailsDialog.OnDialogResultListener() {
                    @Override
                    public void onPositiveResult(Integer securityAmount, Integer rentAmount) {
                        flatInfo.setSecurityAmount(securityAmount);
                        flatInfo.setRentAmount(rentAmount);
                        CurrentLocationMapDialog dialog = new CurrentLocationMapDialog();
                        dialog.setOnDialogResultListener(new CurrentLocationMapDialog.OnDialogResultListener() {
                            @Override
                            public void onPositiveResult(double currentLatitude, double currentLongitude) {
                                List<Double> vertices = new ArrayList<>();
                                vertices.add(currentLatitude);
                                vertices.add(currentLongitude);
                                flatInfo.setVertices(vertices);
                                final FlatAmenitiesDialog flatAmenitiesDialog = new FlatAmenitiesDialog();
                                flatAmenitiesDialog.setOnDialogResultListener(new FlatAmenitiesDialog.OnDialogResultListener() {
                                    @Override
                                    public void onPositiveResult(String amenities) {
                                        if (type.equals("POST")) {
                                            registerFlat(flatInfo);
                                            return;
                                        }
                                        FlatPropertiesDialog flatPropertiesDialog = new FlatPropertiesDialog();
                                        flatPropertiesDialog.setOnDialogResultListener(new FlatPropertiesDialog.OnDialogResultListener() {
                                            @Override
                                            public void onPositiveResult(boolean expenseGroupRequired, boolean availableForRent) {
                                                flatInfo.setAvailable(availableForRent);
                                                registerFlat(flatInfo);
                                            }

                                            @Override
                                            public void onNegativeResult() {
                                                if (receiver != null) {
                                                    receiver.onRegisterNewFlatTaskFailed();
                                                }

                                            }
                                        });
                                        flatPropertiesDialog.show(fragmentManager, "fragment");
                                    }

                                    @Override
                                    public void onNegativeResult() {
                                        if (receiver != null) {
                                            receiver.onRegisterNewFlatTaskFailed();
                                        }
                                    }
                                });
                                flatAmenitiesDialog.show(fragmentManager, "fragment");

                            }

                            @Override
                            public void onNegativeResult() {
                                if (receiver != null) {
                                    receiver.onRegisterNewFlatTaskFailed();
                                }
                            }
                        });
                        dialog.show(fragmentManager, "fragment");
                    }

                    @Override
                    public void onNegativeResult() {
                        if (receiver != null) {
                            receiver.onRegisterNewFlatTaskFailed();
                        }
                    }
                });
                flatRentDetailsDialog.show(fragmentManager, "fragment");
            }

            @Override
            public void onNegativeResult() {
                if (receiver != null) {
                    receiver.onRegisterNewFlatTaskFailed();
                }
            }
        });
        flatNameDialog.show(fragmentManager, "fragment");
    }

    private void registerFlat(FlatInfo flatInfo) {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        RegisterNewFlatAsyncTask task = new RegisterNewFlatAsyncTask(context, flatInfo);
        task.setOnRegisterNewFlatReceiver(new OnRegisterNewFlatReceiver() {
            @Override
            public void onRegisterNewFlatSuccessful(FlatInfo flatInfo) {
                progressDialog.cancel();
                if (flatInfo == null) {
                    Toast.makeText(context, "Flat with given name already registered. \n Please enter different name", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "FlatInfo uploaded");
                Toast.makeText(context, "New Flat Registered", Toast.LENGTH_SHORT).show();
                if (receiver != null) {
                    receiver.onRegisterNewFlatTaskSuccess(flatInfo);
                }
            }

            @Override
            public void onRegisterNewFlatFailed() {
                progressDialog.cancel();
                if (receiver != null) {
                    receiver.onRegisterNewFlatTaskFailed();
                }
            }
        });
        task.execute();
        progressDialog.setMessage("Registering new flat");
        progressDialog.show();
    }
}
