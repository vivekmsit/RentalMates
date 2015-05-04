package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;

public interface OnRegisterWithOldFlatReceiver {
    public void onRegisterWithOldFlatSuccessful(String message, FlatInfo flat);

    public void onRegisterWithOldFlatFailed();
}
