package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;

public interface OnRegisterNewFlatReceiver {
    void onRegisterNewFlatSuccessful(FlatInfo flatInfo);

    void onRegisterNewFlatFailed();
}
