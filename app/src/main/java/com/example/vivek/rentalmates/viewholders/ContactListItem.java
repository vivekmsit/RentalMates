package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.backend.mainApi.model.Contact;

public class ContactListItem {
    public final String contactDetails;
    public final Long contactNumber;

    public ContactListItem(Contact contact) {
        this.contactDetails = contact.getContactDetails();
        this.contactNumber = contact.getContactNumber();
    }
}
