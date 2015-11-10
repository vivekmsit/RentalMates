package com.example.vivek.rentalmates.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.dialogs.CurrentLocationMapDialog;
import com.example.vivek.rentalmates.dialogs.FlatFacilitiesDialog;
import com.example.vivek.rentalmates.dialogs.FlatNameDialog;
import com.example.vivek.rentalmates.dialogs.FlatPropertiesDialog;
import com.example.vivek.rentalmates.dialogs.FlatRentDetailsDialog;

import java.util.ArrayList;
import java.util.List;

public class GetNewFlatInfoTask {
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private OnGetFlatInfoTask receiver;

    public interface OnGetFlatInfoTask {
        void onRegisterNewFlatSuccessful(FlatInfo newFlatInfo);

        void onRegisterNewFlatFailed();
    }

    public GetNewFlatInfoTask(Context context, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        prefs = context.getSharedPreferences(AppConstants.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setOnGetFlatInfoTask(OnGetFlatInfoTask receiver) {
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
                                final FlatFacilitiesDialog flatFacilitiesDialog = new FlatFacilitiesDialog();
                                flatFacilitiesDialog.setOnDialogResultListener(new FlatFacilitiesDialog.OnDialogResultListener() {
                                    @Override
                                    public void onPositiveResult(boolean cookAvailable, boolean maidAvailable, boolean wifiAvailable) {
                                        FlatPropertiesDialog flatPropertiesDialog = new FlatPropertiesDialog();
                                        flatPropertiesDialog.setOnDialogResultListener(new FlatPropertiesDialog.OnDialogResultListener() {
                                            @Override
                                            public void onPositiveResult(boolean availableForRent, boolean expenseGroupRequired) {
                                                flatInfo.setAvailable(availableForRent);
                                                if (receiver != null) {
                                                    receiver.onRegisterNewFlatSuccessful(flatInfo);
                                                }
                                            }

                                            @Override
                                            public void onNegativeResult() {
                                                if (receiver != null) {
                                                    receiver.onRegisterNewFlatFailed();
                                                }

                                            }
                                        });
                                        flatPropertiesDialog.show(fragmentManager, "fragment");
                                    }

                                    @Override
                                    public void onNegativeResult() {
                                        if (receiver != null) {
                                            receiver.onRegisterNewFlatFailed();
                                        }
                                    }
                                });
                                flatFacilitiesDialog.show(fragmentManager, "fragment");

                            }

                            @Override
                            public void onNegativeResult() {
                                if (receiver != null) {
                                    receiver.onRegisterNewFlatFailed();
                                }
                            }
                        });
                        dialog.show(fragmentManager, "fragment");
                    }

                    @Override
                    public void onNegativeResult() {
                        if (receiver != null) {
                            receiver.onRegisterNewFlatFailed();
                        }
                    }
                });
                flatRentDetailsDialog.show(fragmentManager, "fragment");
            }

            @Override
            public void onNegativeResult() {
                if (receiver != null) {
                    receiver.onRegisterNewFlatFailed();
                }
            }
        });
        flatNameDialog.show(fragmentManager, "fragment");
    }
}
