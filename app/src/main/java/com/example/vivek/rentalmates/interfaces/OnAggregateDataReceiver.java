package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.mainApi.model.AggregateData;

public interface OnAggregateDataReceiver {
    void onAggregateDataLoadSuccessful(AggregateData data);

    void onAggregateDataLoadFailed();
}
