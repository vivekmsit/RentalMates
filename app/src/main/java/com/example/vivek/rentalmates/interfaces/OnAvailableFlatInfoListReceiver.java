package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;

import java.util.List;

public interface OnAvailableFlatInfoListReceiver {
    void onAvailableFlatInfoListLoadSuccessful(List<FlatInfo> flats);

    void onAvailableFlatInfoListLoadFailed();
}
