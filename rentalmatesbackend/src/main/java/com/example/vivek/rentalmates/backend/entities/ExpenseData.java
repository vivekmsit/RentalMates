package com.example.vivek.rentalmates.backend.entities;

import com.example.vivek.rentalmates.backend.entities.FlatInfo;
import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sun.rmi.runtime.Log;

/**
 * Created by vivek on 2/9/2015.
 */
@Entity
public class ExpenseData implements Serializable{

    @Id
    Long id;

    private int amount;
    private Date date;
    private String description;
    private String ownerEmailId;
    private Long expenseGroupId;
    private int currencyType;

    ExpenseData(){
        date = new Date();
        amount = 0;
        description = "Description";
        ownerEmailId = "EmailId";
        currencyType = 0;
    }

    public Long getId() {
        return id;
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

    public String getOwnerEmailId(){
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId){
        this.ownerEmailId = emailId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExpenseGroupId() {
        return expenseGroupId;
    }

    public void setExpenseGroupId(Long expenseGroupId) {
        this.expenseGroupId = expenseGroupId;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

}
