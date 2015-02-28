package com.example.vivek.rentalmates.backend;

import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vivek on 2/9/2015.
 */
public class ExpenseData {

    private int amount;
    private Date date;
    private String info;
    private Long flatId;
    private int currencyType;
    private List<Key> userKeys = new ArrayList<Key>();

    ExpenseData(){
        date = new Date();
        amount = 0;
        info = "NA";
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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setFlatId(Long id){
        this.flatId = id;
    }

    public Long getFlatId(){
        return this.flatId;
    }

    public FlatInfo getFlatInfo() {
        FlatInfo flatInfo = new FlatInfo();
        return flatInfo;
        //To be implemented
    }

    public void setFlatInfo(FlatInfo flatInfo) {
        //To be implemented
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
