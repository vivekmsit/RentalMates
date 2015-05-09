package com.example.vivek.rentalmates.interfaces;

public interface OnGcmRegistrationReceiver {
    public void onGcmRegisterSuccessful(String regId);

    public void onGcmRegisterFailed();
}
