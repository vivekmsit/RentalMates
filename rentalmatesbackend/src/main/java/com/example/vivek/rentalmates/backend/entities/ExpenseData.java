package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.Date;

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
    private String userName;
    private Long expenseGroupId;
    private String expenseGroupName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getExpenseGroupName() {
        return expenseGroupName;
    }

    public void setExpenseGroupName(String expenseGroupName) {
        this.expenseGroupName = expenseGroupName;
    }

    public int getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(int currencyType) {
        this.currencyType = currencyType;
    }

}
