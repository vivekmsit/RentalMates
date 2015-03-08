package com.example.vivek.rentalmates.backend.entities;

import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 2/9/2015.
 */
@Entity
public class ExpenseData {

    @Id
    Long id;

    private int amount;
    private Date date;
    private String description;
    private Long flatId;
    private int currencyType;
    private List<Key> userKeys = new ArrayList<Key>();

    ExpenseData(){
        date = new Date();
        amount = 0;
        description = "NA";
        currencyType = 0;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFlatId(Long id){
        this.flatId = id;
    }

    public Long getFlatId(){
        return this.flatId;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

    public List<Key> getUserKeys() {
        return userKeys;
    }

    public void setUserKeys(List<Key> userKeys) {
        this.userKeys = userKeys;
    }

}
