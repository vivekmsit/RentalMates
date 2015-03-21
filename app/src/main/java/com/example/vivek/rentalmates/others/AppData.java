package com.example.vivek.rentalmates.others;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;

import java.io.File;
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
 * Created by vivek on 3/20/2015.
 */
public class AppData implements Serializable{

    private static final String TAG = "AppData_Debug";

    private List<LocalUserProfile> userProfiles = new ArrayList<>();

    private List<LocalFlatInfo> flats = new ArrayList<>();

    private List<LocalExpenseData> expenses = new ArrayList<>();

    private static AppData appDataInstance = new AppData();

    public static AppData getInstance() {
        return appDataInstance;
    }

    private AppData() {
        this.userProfiles = null;
        this.flats = null;
        this.expenses = null;
    }

    public List<LocalUserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(List<LocalUserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }

    public List<LocalFlatInfo> getFlats() {
        return flats;
    }

    public void setFlats(List<LocalFlatInfo> flats) {
        this.flats = flats;
    }

    public List<ExpenseData> getExpenses() {
        return LocalExpenseData.convertLocalExpenseToExpense(this.expenses);
    }

    public void setExpenses(List<LocalExpenseData> expenses) {
        this.expenses = expenses;
    }

    public boolean storeUserProfileList(Context context, List<UserProfile> profiles) {
        this.userProfiles = LocalUserProfile.convertUserProfileToLocalUserProfile(profiles);
        return storeAppData(context);
    }

    public boolean storeFlatInfoList(Context context, List<FlatInfo> flats) {
        this.flats = LocalFlatInfo.convertFlatInfoToLocalFlatInfo(flats);
        return storeAppData(context);
    }

    public boolean storeExpenseDataList(Context context, List<ExpenseData> expenses) {
        this.expenses = LocalExpenseData.convertExpenseToLocalExpense(expenses);
        return storeAppData(context);
    }

    public boolean storeAppData(Context context){
        boolean status = false;
        String path = context.getApplicationContext().getFilesDir().getPath();
        Log.d(TAG, "path is: " + path);
        try {
            FileOutputStream fos = new FileOutputStream(path + "/" + "appData.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(appDataInstance);
            oos.close();
            status = true;
        } catch (IOException e) {
            Toast.makeText(context, "IOException occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            status = false;
        }
        return status;
    }

    public boolean restoreAppData(Context context) {
        String path = context.getApplicationContext().getFilesDir().getPath();
        FileInputStream fis;
        try {
            fis = new FileInputStream(path + "/" + "appData.tmp");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try {
            appDataInstance = (AppData) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Log.d(TAG, "appData read successfully");
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void  clearAppData(Context context){
        String path = context.getFilesDir().getPath()+"/" + "appData.tmp";
        File file = new File(path);
        file.delete();
        this.userProfiles = null;
        this.flats = null;
        this.expenses = null;
        Toast.makeText(context, "AppData cleared", Toast.LENGTH_LONG).show();
        Log.d(TAG, "AppData cleared");
    }
}
