package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.Request;

public interface OnAcceptRequestRegisterWithOtherFlatReceiver {
    void onAcceptRequestRegisterWithOtherFlatSuccessful(int position);

    void onAcceptRequestRegisterWithOtherFlatFailed();
}
