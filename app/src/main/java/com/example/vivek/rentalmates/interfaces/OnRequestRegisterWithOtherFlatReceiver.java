package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.Request;

public interface OnRequestRegisterWithOtherFlatReceiver {
    void onRequestRegisterWithOtherFlatSuccessful(Request request);

    void onRequestRegisterWithOtherFlatFailed();
}
