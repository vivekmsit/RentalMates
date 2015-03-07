package com.example.vivek.rentalmates.backend.entities;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServicePb;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class UserProfile {

    @Id
    Long id;

    @Index
    private String userName;

    private String profileURL;

    private String profilePhotoURL;

    @Index
    private String currentPlace;

    @Index
    private String emailId;

    // you can add more fields...

    public UserProfile() {
    }

    public Long getId(){
        return this.id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String urlName) {
        this.profileURL = urlName;
    }

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String urlName) {
        this.profilePhotoURL = urlName;
    }

    public String getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(String placeName) {
        this.currentPlace = placeName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}