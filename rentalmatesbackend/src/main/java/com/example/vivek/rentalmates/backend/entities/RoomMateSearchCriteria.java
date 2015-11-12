package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

@Entity
public class RoomMateSearchCriteria implements Serializable {
    @Id
    Long id;
    private boolean sharing;
    private Long rentAmountPerPerson;
    private Long securityAmountPerPerson;
    private Long gender;
    private boolean notifyIfAvailable;
    private boolean publicPost; //Your requirement will appear in search roommates section
    private double flatLatitude;
    private double flatLongitude;
    private String message; //Only applicable if @publicPost is true

    public RoomMateSearchCriteria() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
