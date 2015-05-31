package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;

public interface OnRegisterWithOldFlatReceiver {
    void onRegisterWithOldFlatSuccessful(String message, FlatInfo flat);

    void onRegisterWithOldFlatFailed();
}
