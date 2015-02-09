package com.example.vivek.rentalmates.backend;

import com.google.appengine.api.datastore.Key;

import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 2/9/2015.
 */
public class ExpenseData {

    private int amount;
    private Date date;
    private String info;
    private FlatInfo flatInfo;
    private int currencyType;
    private List<Key> userKeys;

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public FlatInfo getFlatInfo() {
        return flatInfo;
    }

    public void setFlatInfo(FlatInfo flatInfo) {
        this.flatInfo = flatInfo;
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
