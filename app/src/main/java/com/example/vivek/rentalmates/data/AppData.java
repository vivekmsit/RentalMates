package com.example.vivek.rentalmates.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.example.vivek.rentalmates.backend.entities.expenseGroupApi.model.ExpenseData;
import com.example.vivek.rentalmates.backend.mainApi.model.Chat;
import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessage;
import com.example.vivek.rentalmates.backend.mainApi.model.Contact;
import com.example.vivek.rentalmates.backend.userProfileApi.model.ExpenseGroup;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatInfo;
import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;
import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;
import com.example.vivek.rentalmates.backend.userProfileApi.model.UserProfile;
import com.example.vivek.rentalmates.tasks.LoadProfileImageAsyncTask;

import java.io.ByteArrayOutputStream;
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

    private List<LocalRequest> requests;
    private List<String> gcmTypes;

    private HashMap<String, String> profilePicturesPath;
    private HashMap<Long, LocalFlatInfo> flats;
    private HashMap<Long, LocalFlatInfo> availableFlats;
    private HashMap<Long, LocalUserProfile> userProfiles;
    private HashMap<Long, LocalExpenseGroup> expenseGroups;
    private HashMap<String, String> gcmData;
    private HashMap<Long, List<LocalContact>> contacts;
    private HashMap<Long, List<LocalExpenseData>> expenses;
    private HashMap<Long, List<LocalFlatSearchCriteria>> roomMateList;
    private HashMap<Long, LocalChat> localChats;
    private HashMap<Long, List<LocalChatMessage>> localChatMessages;

    private LocalFlatSearchCriteria localFlatSearchCriteria;

    private double lastLocationLatitude;
    private double lastLocationLongitude;
    private float lastLocationZoom;

    private static AppData appDataInstance = new AppData();

    public static AppData getInstance() {
        return appDataInstance;
    }

    private AppData() {
        requests = new ArrayList<>();
        gcmTypes = new ArrayList<>();
        gcmTypes.add("NEW_EXPENSE_DATA");
        gcmTypes.add("NEW_FLAT_USER");
        gcmTypes.add("NEW_EXPENSE_GROUP_USER");
        gcmTypes.add("NEW_REQUEST");
        gcmTypes.add("message");
        roomMateList = new HashMap<>();
        profilePicturesPath = new HashMap<>();
        flats = new HashMap<>();
        availableFlats = new HashMap<>();
        userProfiles = new HashMap<>();
        expenseGroups = new HashMap<>();
        gcmData = new HashMap<>();
        contacts = new HashMap<>();
        expenses = new HashMap<>();
        localChats = new HashMap<>();
        localChatMessages = new HashMap<>();
        localFlatSearchCriteria = new LocalFlatSearchCriteria();
        lastLocationLatitude = 23.3192728;
        lastLocationLongitude = 81.9220346;
        lastLocationZoom = 5;
    }

    public List<String> getGcmTypes() {
        return gcmTypes;
    }

    public void setGcmTypes(List<String> gcmTypes) {
        this.gcmTypes = gcmTypes;
    }

    public HashMap<String, String> getGcmData() {
        return gcmData;
    }

    public boolean addGcmData(String key, String value, Context context) {
        this.gcmData.put(key, value);
        return storeAppData(context);
    }

    public boolean storeLastLocationData(Context context, double lastLocationLatitude, double lastLocationLongitude, float lastLocationZoom) {
        this.lastLocationLatitude = lastLocationLatitude;
        this.lastLocationLongitude = lastLocationLongitude;
        this.lastLocationZoom = lastLocationZoom;
        return storeAppData(context);
    }

    public double getLastLocationLatitude() {
        return lastLocationLatitude;
    }

    public double getLastLocationLongitude() {
        return lastLocationLongitude;
    }

    public float getLastLocationZoom() {
        return lastLocationZoom;
    }

    public boolean clearGcmData(Context context) {
        this.gcmData.clear();
        return storeAppData(context);
    }

    public List<Request> getRequests() {
        return LocalRequest.convertLocalRequestToRequest(this.requests);
    }

    public void setRequests(List<LocalRequest> requests) {
        this.requests = requests;
    }

    public List<Contact> getContacts(Long flatId) {
        return LocalContact.convertLocalContactToContact(this.contacts.get(flatId));
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

    public HashMap<Long, LocalFlatInfo> getAvailableFlats() {
        return availableFlats;
    }

    public void setAvailableFlats(HashMap<Long, LocalFlatInfo> availableFlats) {
        this.availableFlats = availableFlats;
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
        this.expenseGroups = new HashMap<>();
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
        this.userProfiles = new HashMap<>();
        List<LocalUserProfile> userProfiles = LocalUserProfile.convertUserProfileToLocalUserProfile(profiles);
        for (LocalUserProfile profile : userProfiles) {
            this.userProfiles.put(profile.getUserProfileId(), profile);
        }
        return storeAppData(context);
    }

    public List<LocalFlatSearchCriteria> getRoomMateList(Long flatId) {
        List<LocalFlatSearchCriteria> list = new ArrayList<>();
        if (roomMateList.get(flatId) == null) {
            return list;
        } else {
            return roomMateList.get(flatId);
        }
    }

    public boolean storeRoomMateList(Context context, Long flatId, List<FlatSearchCriteria> roomMateList) {
        this.roomMateList.remove(flatId);
        this.roomMateList.put(flatId, LocalFlatSearchCriteria.convertFlatSearchCriteriaListToLocalFlatSearchCriteriaList(roomMateList));
        return storeAppData(context);
    }

    public boolean storeAvailableFlatInfoList(Context context, List<FlatInfo> flats) {
        this.availableFlats = new HashMap<>();
        List<LocalFlatInfo> localFlats = LocalFlatInfo.convertFlatInfoToLocalFlatInfo(flats);
        for (LocalFlatInfo flatInfo : localFlats) {
            this.availableFlats.put(flatInfo.getFlatId(), flatInfo);
        }
        return storeAppData(context);
    }

    public boolean storeFlatInfoList(Context context, List<FlatInfo> flats) {
        this.flats = new HashMap<>();
        List<LocalFlatInfo> localFlats = LocalFlatInfo.convertFlatInfoToLocalFlatInfo(flats);
        for (LocalFlatInfo flatInfo : localFlats) {
            this.flats.put(flatInfo.getFlatId(), flatInfo);
        }
        return storeAppData(context);
    }

    public boolean storeFlatSearchCriteria(Context context, FlatSearchCriteria flatSearchCriteria) {
        this.localFlatSearchCriteria = LocalFlatSearchCriteria.convertFlatSearchCriteriaToLocalFlatSearchCriteria(flatSearchCriteria);
        return storeAppData(context);
    }

    public FlatSearchCriteria getFlatSearchCriteria() {
        return LocalFlatSearchCriteria.convertLocalFlatSearchCriteriaToFlatSearchCriteria(this.localFlatSearchCriteria);
    }

    public boolean storeContactList(Context context, Long flatId, List<Contact> contacts) {
        this.contacts.remove(flatId);
        this.contacts.put(flatId, LocalContact.convertContactToLocalContact(contacts));
        return storeAppData(context);
    }

    public boolean storeExpenseDataList(Context context, Long expenseGroupId, List<ExpenseData> expenses) {
        this.expenses.remove(expenseGroupId);
        this.expenses.put(expenseGroupId, LocalExpenseData.convertExpenseToLocalExpense(expenses));
        return storeAppData(context);
    }

    public List<ExpenseData> getExpenseDataList(Long expenseGroupId) {
        return LocalExpenseData.convertLocalExpenseToExpense(this.expenses.get(expenseGroupId));
    }

    public boolean storeRequestList(Context context, List<Request> requests) {
        this.requests = LocalRequest.convertRequestToLocalRequest(requests);
        return storeAppData(context);
    }

    public boolean deleteRequest(Context context, int position) {
        if (this.requests.size() != 0) {
            this.requests.remove(position);
        }
        return storeAppData(context);
    }

    public boolean addLocalExpenseData(Context context, Long expenseGroupId, ExpenseData expense) {
        LocalExpenseData data = new LocalExpenseData();
        data.setExpenseId(expense.getId());
        data.setAmount(expense.getAmount());
        data.setDescription(expense.getDescription());
        data.setOwnerEmailId(expense.getOwnerEmailId());
        data.setDate(expense.getDate());
        data.setExpenseGroupName(expense.getExpenseGroupName());
        data.setUserName(expense.getUserName());
        List<LocalExpenseData> localExpenseDataList = this.expenses.get(expenseGroupId);
        localExpenseDataList.add(data);
        this.expenses.put(expenseGroupId, localExpenseDataList);
        return storeAppData(context);
    }

    public boolean storeAppData(Context context) {
        boolean status;
        String path = context.getApplicationContext().getFilesDir().getPath();
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

        //Initialize all the variables (code copied from constructor)
        requests = new ArrayList<>();
        gcmTypes = new ArrayList<>();
        gcmTypes.add("NEW_EXPENSE_DATA");
        gcmTypes.add("NEW_FLAT_USER");
        gcmTypes.add("NEW_EXPENSE_GROUP_USER");
        gcmTypes.add("NEW_REQUEST");
        gcmTypes.add("message");
        roomMateList = new HashMap<>();
        profilePicturesPath = new HashMap<>();
        flats = new HashMap<>();
        availableFlats = new HashMap<>();
        userProfiles = new HashMap<>();
        expenseGroups = new HashMap<>();
        gcmData = new HashMap<>();
        contacts = new HashMap<>();
        expenses = new HashMap<>();
        localChats = new HashMap<>();
        localChatMessages = new HashMap<>();
        localFlatSearchCriteria = new LocalFlatSearchCriteria();

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

    public boolean isSerializable(Object e) {
        boolean result;
        try {
            new ObjectOutputStream(new ByteArrayOutputStream()).writeObject(e);
            result = true;
        } catch (IOException e1) {
            e1.printStackTrace();
            result = false;
        }
        return result;
    }

    public HashMap<Long, LocalChat> getChats() {
        return localChats;
    }

    public boolean storeChats(Context context, List<Chat> chats) {
        this.localChats.clear();
        for (LocalChat localChat : LocalChat.convertChatListToLocalChatList(chats)) {
            this.localChats.put(localChat.getId(), localChat);
        }
        return storeAppData(context);
    }

    public List<LocalChatMessage> getLocalChatMessages(Long chatId) {
        return localChatMessages.get(chatId);
    }

    public void storeLocalChatMessages(Context context, Long chatId, List<ChatMessage> chatMessages) {
        this.localChatMessages.put(chatId, LocalChatMessage.convertChatMessageListToLocalChatMessageList(chatMessages));
    }
}
