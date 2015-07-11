package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;

import java.util.List;

public interface OnRequestListReceiver {
    void onRequestListLoadSuccessful(List<Request> requests);

    void onRequestListLoadFailed();
}
