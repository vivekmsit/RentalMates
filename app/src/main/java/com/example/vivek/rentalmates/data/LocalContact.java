package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.mainApi.model.Contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocalContact implements Serializable {

    Long id;
    private Long contactNumber;
    private Long uploaderId;
    private String contactDetails;

    public LocalContact() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public Long getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Long uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public static List<Contact> convertLocalContactToContact(List<LocalContact> localContacts) {
        if (localContacts == null) {
            return null;
        }
        List<Contact> contacts = new ArrayList<>();
        for (LocalContact localContact : localContacts) {
            Contact data = new Contact();

            data.setId(localContact.getId());
            data.setContactNumber(localContact.getContactNumber());
            data.setContactDetails(localContact.getContactDetails());
            data.setUploaderId(localContact.getUploaderId());

            contacts.add(data);
        }
        return contacts;
    }

    public static List<LocalContact> convertContactToLocalContact(List<Contact> contacts) {
        if (contacts == null) {
            return null;
        }
        List<LocalContact> localContacts = new ArrayList<>();
        for (Contact contact : contacts) {
            LocalContact data = new LocalContact();

            data.setId(contact.getId());
            data.setContactDetails(contact.getContactDetails());
            data.setUploaderId(contact.getUploaderId());
            data.setContactNumber(contact.getContactNumber());

            localContacts.add(data);
        }
        return localContacts;
    }
}
