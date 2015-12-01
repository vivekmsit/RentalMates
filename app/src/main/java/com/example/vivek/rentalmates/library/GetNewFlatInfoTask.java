package com.example.vivek.rentalmates.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.data.AppConstants;
import com.example.vivek.rentalmates.dialogs.CurrentLocationMapDialog;
import com.example.vivek.rentalmates.dialogs.FlatAmenitiesDialog;
import com.example.vivek.rentalmates.dialogs.FlatNameDialog;
import com.example.vivek.rentalmates.dialogs.FlatPropertiesDialog;
import com.example.vivek.rentalmates.dialogs.FlatRentDetailsDialog;

import java.util.ArrayList;
import java.util.List;

public class GetNewFlatInfoTask {
    private FragmentManager fragmentManager;
    private SharedPreferences prefs;
    private OnGetFlatInfoTask receiver;
    private String type;

    public interface OnGetFlatInfoTask {
        void onGetFlatInfoTaskSuccess(FlatInfo newFlatInfo);

        void onGetFlatInfoTaskFailed();
    }

    public GetNewFlatInfoTask(Context context, FragmentManager fragmentManager, String type) {
        this.fragmentManager = fragmentManager;
        this.type = type;
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
                                final FlatAmenitiesDialog flatAmenitiesDialog = new FlatAmenitiesDialog();
                                flatAmenitiesDialog.setOnDialogResultListener(new FlatAmenitiesDialog.OnDialogResultListener() {
                                    @Override
                                    public void onPositiveResult(String amenities) {
                                        if (type.equals("POST")) {
                                            if (receiver != null) {
                                                receiver.onGetFlatInfoTaskSuccess(flatInfo);
                                            }
                                            return;
                                        }
                                        FlatPropertiesDialog flatPropertiesDialog = new FlatPropertiesDialog();
                                        flatPropertiesDialog.setOnDialogResultListener(new FlatPropertiesDialog.OnDialogResultListener() {
                                            @Override
                                            public void onPositiveResult(boolean expenseGroupRequired, boolean availableForRent) {
                                                flatInfo.setAvailable(availableForRent);
                                                if (receiver != null) {
                                                    receiver.onGetFlatInfoTaskSuccess(flatInfo);
                                                }
                                            }

                                            @Override
                                            public void onNegativeResult() {
                                                if (receiver != null) {
                                                    receiver.onGetFlatInfoTaskFailed();
                                                }

                                            }
                                        });
                                        flatPropertiesDialog.show(fragmentManager, "fragment");
                                    }

                                    @Override
                                    public void onNegativeResult() {
                                        if (receiver != null) {
                                            receiver.onGetFlatInfoTaskFailed();
                                        }
                                    }
                                });
                                flatAmenitiesDialog.show(fragmentManager, "fragment");

                            }

                            @Override
                            public void onNegativeResult() {
                                if (receiver != null) {
                                    receiver.onGetFlatInfoTaskFailed();
                                }
                            }
                        });
                        dialog.show(fragmentManager, "fragment");
                    }

                    @Override
                    public void onNegativeResult() {
                        if (receiver != null) {
                            receiver.onGetFlatInfoTaskFailed();
                        }
                    }
                });
                flatRentDetailsDialog.show(fragmentManager, "fragment");
            }

            @Override
            public void onNegativeResult() {
                if (receiver != null) {
                    receiver.onGetFlatInfoTaskFailed();
                }
            }
        });
        flatNameDialog.show(fragmentManager, "fragment");
    }
}
