package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.mainApi.model.Request;

public interface OnRequestJoinExistingEntityReceiver {
    void onRequestJoinExistingEntitySuccessful(Request request);

    void onRequestJoinExistingEntityFailed();
}
