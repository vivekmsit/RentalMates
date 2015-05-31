package com.example.vivek.rentalmates.interfaces;

public interface OnGcmRegistrationReceiver {
    void onGcmRegisterSuccessful(String regId);

    void onGcmRegisterFailed();
}
