package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.mainApi.model.Contact;

public interface OnAddContactReceiver {
    void onAddContactSuccessful(Contact contact);

    void onAddContactFailed();
}
