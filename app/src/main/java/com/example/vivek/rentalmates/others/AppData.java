package com.example.vivek.rentalmates.others;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Singleton Class to store serializable data*/
public class AppData implements Serializable {

    private static final String TAG = "AppData_Debug";

    private List<LocalExpenseData> expenses = new ArrayList<>();

    private HashMap<String, String> profilePicturesPath = new HashMap<>();
    private HashMap<Long, LocalFlatInfo> flats = new HashMap<>();
    private HashMap<Long, LocalUserProfile> userProfiles = new HashMap<>();
    private HashMap<Long, LocalExpenseGroup> expenseGroups = new HashMap<>();

    private static AppData appDataInstance = new AppData();

    public static AppData getInstance() {
        return appDataInstance;
    }

    /* private Constructor of singleton class*/
    private AppData() {
    }

    public HashMap<Long, LocalUserProfile> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(HashMap<Long, LocalUserProfile> userProfiles) {
        this.userProfiles = userProfiles;
    }

    public HashMap<Long, LocalFlatInfo> getFlats() {
        return flats;
    }

    public void setFlats(HashMap<Long, LocalFlatInfo> flats) {
        this.flats = flats;
    }

    public List<ExpenseData> getExpenses() {
        return LocalExpenseData.convertLocalExpenseToExpense(this.expenses);
    }

    public void setExpenses(List<LocalExpenseData> expenses) {
        this.expenses = expenses;
    }

    public Collection<LocalExpenseGroup> getExpenseGroups() {
        return expenseGroups.values();
    }

    public void setExpenseGroups(HashMap<Long, LocalExpenseGroup> expenseGroups) {
        this.expenseGroups = expenseGroups;
    }

    public Map<String, String> getProfilePicturesPath() {
        return profilePicturesPath;
    }

    public void setProfilePicturesPath(HashMap<String, String> profilePicturesPath) {
        this.profilePicturesPath = profilePicturesPath;
    }

    public boolean storeExpenseGroupList(Context context, List<ExpenseGroup> expenseGroups) {
        List<LocalExpenseGroup> localExpenseGroups = LocalExpenseGroup.convertEGroupToLocalEGroup(expenseGroups);
        for (LocalExpenseGroup group : localExpenseGroups) {
            this.expenseGroups.put(group.getId(), group);
        }
        return storeAppData(context);
    }

    public LocalExpenseGroup getLocalExpenseGroup(Long id) {
        if (this.expenseGroups.containsKey(id)) {
            return this.expenseGroups.get(id);
        } else {
            return null;
        }
    }

    public LocalUserProfile getLocalUserProfile(Long id) {
        if (this.userProfiles.containsKey(id)) {
            return this.userProfiles.get(id);
        } else {
            return null;
        }
    }

    public boolean storeUserProfileList(Context context, List<UserProfile> profiles) {
        List<LocalUserProfile> userProfiles = LocalUserProfile.convertUserProfileToLocalUserProfile(profiles);
        for (LocalUserProfile profile : userProfiles) {
            this.userProfiles.put(profile.getUserProfileId(), profile);
        }
        return storeAppData(context);
    }

    public boolean storeFlatInfoList(Context context, List<FlatInfo> flats) {
        List<LocalFlatInfo> localFlats = LocalFlatInfo.convertFlatInfoToLocalFlatInfo(flats);
        for (LocalFlatInfo flatInfo : localFlats) {
            this.flats.put(flatInfo.getFlatId(), flatInfo);
        }
        return storeAppData(context);
    }

    public boolean storeExpenseDataList(Context context, List<ExpenseData> expenses) {
        this.expenses = LocalExpenseData.convertExpenseToLocalExpense(expenses);
        return storeAppData(context);
    }

    public boolean deleteExpenseData(Context context, int position) {
        if (this.expenses != null) {
            this.expenses.remove(position);
        }
        return storeAppData(context);
    }

    public boolean addLocalExpenseData(Context context, ExpenseData expense) {
        LocalExpenseData data = new LocalExpenseData();
        data.setAmount(expense.getAmount());
        data.setDescription(expense.getDescription());
        data.setOwnerEmailId(expense.getOwnerEmailId());
        data.setDate(expense.getDate());
        data.setExpenseGroupName(expense.getExpenseGroupName());
        data.setUserName(expense.getUserName());
        this.expenses.add(0, data);
        return storeAppData(context);
    }

    public boolean storeAppData(Context context) {
        boolean status;
        String path = context.getApplicationContext().getFilesDir().getPath();
        Log.d(TAG, "path is: " + path);
        try {
            FileOutputStream fos = new FileOutputStream(path + "/" + "appData.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(appDataInstance);
            oos.close();
            status = true;
        } catch (IOException e) {
            Log.d(TAG, "IOException occurred: " + e.getCause() + e.getLocalizedMessage() + e.getMessage());
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

    public boolean clearAppData(Context context) {
        //Delete file containing serialized App Data
        String path = context.getFilesDir().getPath() + "/" + "appData.tmp";
        File file = new File(path);
        if (!file.delete()) {
            Toast.makeText(context, "File " + path + "could not be deleted", Toast.LENGTH_LONG).show();
            return false;
        }

        //Initialize all the variables
        this.expenses = new ArrayList<>();
        this.userProfiles = new HashMap<>();
        this.flats = new HashMap<>();
        this.expenseGroups = new HashMap<>();

        Toast.makeText(context, "AppData cleared", Toast.LENGTH_LONG).show();
        Log.d(TAG, "AppData cleared");
        return true;
    }

    public boolean updateProfilePictures(Context context, List<UserProfile> currentProfiles) {
        String emailId;
        for (UserProfile userProfile : currentProfiles) {
            emailId = userProfile.getEmailId();
            if (!profilePicturesPath.containsKey(emailId)) {
                String personPhotoUrl = userProfile.getProfilePhotoURL();
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + AppConstants.PROFILE_PIC_SIZE;
                new LoadProfileImageAsyncTask(context, emailId).execute(personPhotoUrl);
            }
        }
        return storeAppData(context);
    }

    public Bitmap getProfilePictureBitmap(String emailId) {
        Bitmap bitmap = null;
        if (this.profilePicturesPath.containsKey(emailId)) {
            bitmap = BitmapFactory.decodeFile(this.profilePicturesPath.get(emailId));
        } else {
            Log.d(TAG, "ProfilePicture not found for " + emailId);
        }
        return bitmap;
    }
}
