package com.example.vivek.rentalmates.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.flatInfoApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.flatInfoApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.tasks.LoadProfileImageAsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by vivek on 3/20/2015.
 */
public class AppData implements Serializable{

    private static final String TAG = "AppData_Debug";

    private Map<String, String> profilePicturesPath = new HashMap<>();

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

    public Map<String, String> getProfilePicturesPath() {
        return profilePicturesPath;
    }

    public void setProfilePicturesPath(Map<String, String> profilePicturesPath) {
        this.profilePicturesPath = profilePicturesPath;
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

    public boolean updateProfilePictures(Context context, List<UserProfile> currentProfiles) {
        String emailId;
        for (UserProfile userProfile: currentProfiles) {
            emailId = userProfile.getEmailId();
            if (!profilePicturesPath.containsKey(emailId)){
                String personPhotoUrl = userProfile.getProfilePhotoURL();
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + AppConstants.PROFILE_PIC_SIZE;
                new LoadProfileImageAsyncTask(context, emailId).execute(personPhotoUrl);
            }
        }
        return storeAppData(context);
    }

    public Bitmap getProfilePictureBitmap(Context context, String emailId) {
        Bitmap bitmap = null;
        if (this.profilePicturesPath.containsKey(emailId)) {
            bitmap = BitmapFactory.decodeFile(this.profilePicturesPath.get(emailId));
        } else {
            Toast.makeText(context, "ProfilePicture not found for " + emailId, Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }
}
