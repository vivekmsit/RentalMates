package com.example.vivek.rentalmates.others;

import android.os.Environment;
import android.util.Log;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivek on 3/11/2015.
 */
public class LocalExpenseData implements Serializable{

    private static final String TAG = "LocalExpenseData_Debug";

    private int amount;
    private String description;
    private String ownerEmailId;

    public LocalExpenseData(){
        this.amount = 0;
        this.description = "description";
        this.ownerEmailId = "emailId";
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

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String emailId) {
        this.ownerEmailId = emailId;
    }

    public static List<ExpenseData> convertLocalExpenseToExpense(List<LocalExpenseData> expenses){
        List<ExpenseData> localExpenses = new ArrayList<>();
        for (LocalExpenseData expenseData: expenses){
            ExpenseData data = new ExpenseData();
            data.setAmount( expenseData.getAmount());
            data.setDescription(expenseData.getDescription());
            data.setOwnerEmailId(expenseData.getOwnerEmailId());
            localExpenses.add(data);
        }
        return localExpenses;
    }

    public static List<LocalExpenseData> convertExpenseToLocalExpense(List<ExpenseData> expenses){
        List<LocalExpenseData> localExpenses = new ArrayList<>();
        for (ExpenseData expenseData: expenses){
            LocalExpenseData data = new LocalExpenseData();
            data.setAmount( expenseData.getAmount());
            data.setDescription(expenseData.getDescription());
            data.setOwnerEmailId(expenseData.getOwnerEmailId());
            localExpenses.add(data);
        }
        return localExpenses;
    }

    public static String storeExpenseDataList(List<ExpenseData> expenses){
        String message = "";
        List<LocalExpenseData> localExpenses = convertExpenseToLocalExpense(expenses);
        try {
            String path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_MOVIES).getPath();
            FileOutputStream fos = new FileOutputStream(path + "/" + "expenses.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(localExpenses);
            oos.close();
            Log.d(TAG, "localExpenses stored successfully");
            message = "SUCCESS";
        } catch (IOException e) {
            Log.d(TAG, "exception occurred during writing to file " + e.toString());
            e.printStackTrace();
            message = "EXCEPTION";
        }
        return message;
    }

    public static List<ExpenseData> restoreExpenseDataList() {
        List<LocalExpenseData> localExpenses = new ArrayList<>();
        String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES).getPath();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path + "/" + "expenses.tmp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            localExpenses = (List<LocalExpenseData>) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Log.d(TAG, "localExpenses read successfully");
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<ExpenseData> restoredList = convertLocalExpenseToExpense(localExpenses);
        return restoredList;
    }
}
