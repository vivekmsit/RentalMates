package com.example.vivek.rentalmates.others;

import java.io.Serializable;

/**
 * Created by vivek on 3/11/2015.
 */
public class LocalExpenseData implements Serializable{
    private int amount;
    private String description;
    private String owner;

    public LocalExpenseData(int amount, String description, String owner) {
        this.amount = amount;
        this.description = description;
        this.owner = owner;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
