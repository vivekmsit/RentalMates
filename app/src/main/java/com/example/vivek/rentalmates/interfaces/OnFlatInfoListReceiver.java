package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;

import java.util.List;

public interface OnFlatInfoListReceiver {
    public void onFlatInfoListLoadSuccessful(List<FlatInfo> flats);

    public void onFlatInfoListLoadFailed();
}
