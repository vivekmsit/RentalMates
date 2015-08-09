package com.example.vivek.rentalmates.interfaces;

import com.example.vivek.rentalmates.backend.mainApi.model.Contact;

import java.util.List;

public interface OnContactListReceiver {
    void onContactListLoadSuccessful(List<Contact> contacts);

    void onContactListLoadFailed();
}
