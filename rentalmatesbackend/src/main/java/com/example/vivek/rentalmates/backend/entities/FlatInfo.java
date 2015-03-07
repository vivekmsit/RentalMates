package com.example.vivek.rentalmates.backend.entities;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.Date;
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

    private String adminName;

    @Index
    private String flatName; //Should be unique

    @Index
    private boolean available;

    private int numberOfRoommates;

    @Index
    private FlatType type; //1bhk/2bhk/3bhk/villa

    @Index
    private String city;

    private Date date;

    private double[][] vertices;

    @Index
    private String ownerEmailId;

    private List<BlobKey> flatPicturesBlobKeys;

    private List<ExpenseData> expenses = new ArrayList<ExpenseData>();

    //private Map<String, ExpenseData> occupants = new HashMap<>();

    // you can add more fields...

    public FlatInfo() {
        date = new Date();
    }

    public Long getFlatId(){
        return this.id;
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

    public void addExpense(ExpenseData data){
        expenses.add(data);
    }
}