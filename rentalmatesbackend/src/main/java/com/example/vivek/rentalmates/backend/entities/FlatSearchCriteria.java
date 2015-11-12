package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

@Entity
public class FlatSearchCriteria implements Serializable {
    @Id
    Long id;
    private boolean sharing;
    private Long maxRentAmountPerPerson;
    private Long maxSecurityAmountPerPerson;
    private int areaRange; // in metres
    private Long gender;
    private Long requesterId;
    private boolean notifyIfAvailable;
    private boolean publicPost; //Your requirement will appear in search roommates section
    private double locationLatitude;
    private double locationLongitude;
    private String message; //Only applicable if @publicPost is true
    private String requesterName;
    private String requesterProfilePicture;

    public FlatSearchCriteria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSharing() {
        return sharing;
    }

    public void setSharing(boolean sharing) {
        this.sharing = sharing;
    }

    public Long getMaxRentAmountPerPerson() {
        return maxRentAmountPerPerson;
    }

    public void setMaxRentAmountPerPerson(Long maxRentAmountPerPerson) {
        this.maxRentAmountPerPerson = maxRentAmountPerPerson;
    }

    public Long getMaxSecurityAmountPerPerson() {
        return maxSecurityAmountPerPerson;
    }

    public void setMaxSecurityAmountPerPerson(Long maxSecurityAmountPerPerson) {
        this.maxSecurityAmountPerPerson = maxSecurityAmountPerPerson;
    }

    public int getAreaRange() {
        return areaRange;
    }

    public void setAreaRange(int areaRange) {
        this.areaRange = areaRange;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public boolean isNotifyIfAvailable() {
        return notifyIfAvailable;
    }

    public void setNotifyIfAvailable(boolean notifyIfAvailable) {
        this.notifyIfAvailable = notifyIfAvailable;
    }

    public boolean isPublicPost() {
        return publicPost;
    }

    public void setPublicPost(boolean publicPost) {
        this.publicPost = publicPost;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterProfilePicture() {
        return requesterProfilePicture;
    }

    public void setRequesterProfilePicture(String requesterProfilePicture) {
        this.requesterProfilePicture = requesterProfilePicture;
    }
}
