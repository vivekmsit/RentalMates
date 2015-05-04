package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;

public interface OnRegisterNewFlatReceiver {
    public void onRegisterNewFlatSuccessful(FlatInfo flatInfo);

    public void onRegisterNewFlatFailed();
}
