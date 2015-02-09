package com.example.vivek.rentalmates.backend;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.repackaged.com.google.protobuf.DescriptorProtos;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.List;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class FlatInfo {

    public enum FlatType {
        NOT_AVAILABLE,
        SINGLE_BHK,
        DOUBLE_BHK,
        TRIPLE_BHK,
        VILLA
    }

    @Id
    Long id;

    @Index
    private String flatName; //Should be unique

    @Index
    private boolean available;

    private int numberOfRoommates;

    @Index
    private FlatType type; //1bhk/2bhk/3bhk/villa

    @Index
    private String city;

    private double[][] vertices;

    @Index
    private String ownerEmailId;

    List<BlobKey> flatPicturesBlobKeys;

    List<ExpenseData> expenses;

    // you can add more fields...

    public FlatInfo() {
    }

    public String getFlatName() {
        return flatName;
    }

    public void setFlatName(String name) {
        this.flatName = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String cityName) {
        this.city = cityName;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId) {
        this.ownerEmailId = emailId;
    }
}