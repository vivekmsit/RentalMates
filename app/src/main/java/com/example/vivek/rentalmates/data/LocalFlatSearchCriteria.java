package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocalFlatSearchCriteria implements Serializable {

    private Long id;
    private boolean sharing;
    private int minRentAmountPerPerson;
    private int maxRentAmountPerPerson;
    private int minSecurityAmountPerPerson;
    private int maxSecurityAmountPerPerson;
    private int areaRange; // in metres
    private Long gender;
    private Long requesterId;
    private boolean filterResetDone;
    private boolean notifyIfAvailable;
    private boolean publicPost; //Your requirement will appear in search roommates section
    private double locationLatitude;
    private double locationLongitude;
    private String message; //Only applicable if @publicPost is true
    private String requesterName;
    private String requesterProfilePicture;
    private String selectedLocation;

    public LocalFlatSearchCriteria() {
        locationLatitude = 12.8486324;
        locationLongitude = 77.6570782;
        areaRange = 10000;
        minRentAmountPerPerson = 0;
        maxRentAmountPerPerson = 100000;
        minSecurityAmountPerPerson = 0;
        maxSecurityAmountPerPerson = 200000;
        filterResetDone = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isSharing() {
        return sharing;
    }

    public void setSharing(boolean sharing) {
        this.sharing = sharing;
    }

    public int getMinRentAmountPerPerson() {
        return minRentAmountPerPerson;
    }

    public void setMinRentAmountPerPerson(int minRentAmountPerPerson) {
        this.minRentAmountPerPerson = minRentAmountPerPerson;
    }

    public int getMaxRentAmountPerPerson() {
        return maxRentAmountPerPerson;
    }

    public void setMaxRentAmountPerPerson(int maxRentAmountPerPerson) {
        this.maxRentAmountPerPerson = maxRentAmountPerPerson;
    }

    public int getMinSecurityAmountPerPerson() {
        return minSecurityAmountPerPerson;
    }

    public void setMinSecurityAmountPerPerson(int minSecurityAmountPerPerson) {
        this.minSecurityAmountPerPerson = minSecurityAmountPerPerson;
    }

    public int getMaxSecurityAmountPerPerson() {
        return maxSecurityAmountPerPerson;
    }

    public void setMaxSecurityAmountPerPerson(int maxSecurityAmountPerPerson) {
        this.maxSecurityAmountPerPerson = maxSecurityAmountPerPerson;
    }

    public int getAreaRange() {
        return areaRange;
    }

    public void setAreaRange(int areaRange) {
        this.areaRange = areaRange;
    }

    public Long getGender() {
        return gender;
    }

    public void setGender(Long gender) {
        this.gender = gender;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public boolean isNotifyIfAvailable() {
        return notifyIfAvailable;
    }

    public void setNotifyIfAvailable(boolean notifyIfAvailable) {
        this.notifyIfAvailable = notifyIfAvailable;
    }

    public boolean isPublicPost() {
        return publicPost;
    }

    public void setPublicPost(boolean publicPost) {
        this.publicPost = publicPost;
    }

    public double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterProfilePicture() {
        return requesterProfilePicture;
    }

    public void setRequesterProfilePicture(String requesterProfilePicture) {
        this.requesterProfilePicture = requesterProfilePicture;
    }

    public static List<FlatSearchCriteria> convertLocalFlatSearchCriteriaListToFlatSearchCriteriaList(List<LocalFlatSearchCriteria> localFlatSearchCriteriaList) {
        if (localFlatSearchCriteriaList == null) {
            return null;
        }
        List<FlatSearchCriteria> flatSearchCriteriaList = new ArrayList<>();
        for (LocalFlatSearchCriteria localFlatSearchCriteria : localFlatSearchCriteriaList) {
            flatSearchCriteriaList.add(convertLocalFlatSearchCriteriaToFlatSearchCriteria(localFlatSearchCriteria));
        }
        return flatSearchCriteriaList;
    }

    public static List<LocalFlatSearchCriteria> convertFlatSearchCriteriaListToLocalFlatSearchCriteriaList(List<FlatSearchCriteria> flatSearchCriteriaList) {
        if (flatSearchCriteriaList == null) {
            return null;
        }
        List<LocalFlatSearchCriteria> localFlatSearchCriteriaList = new ArrayList<>();
        for (FlatSearchCriteria flatSearchCriteria : flatSearchCriteriaList) {
            localFlatSearchCriteriaList.add(convertFlatSearchCriteriaToLocalFlatSearchCriteria(flatSearchCriteria));
        }
        return localFlatSearchCriteriaList;
    }

    public static FlatSearchCriteria convertLocalFlatSearchCriteriaToFlatSearchCriteria(LocalFlatSearchCriteria localFlatSearchCriteria) {
        if (localFlatSearchCriteria == null) {
            return null;
        }
        FlatSearchCriteria flatSearchCriteria = new FlatSearchCriteria();

        flatSearchCriteria.setId(localFlatSearchCriteria.getId());
        //flatSearchCriteria.setSharing(localFlatSearchCriteria.isSharing());
        flatSearchCriteria.setMinRentAmountPerPerson(localFlatSearchCriteria.getMinRentAmountPerPerson());
        flatSearchCriteria.setMaxRentAmountPerPerson(localFlatSearchCriteria.getMaxRentAmountPerPerson());
        flatSearchCriteria.setMinSecurityAmountPerPerson(localFlatSearchCriteria.getMinSecurityAmountPerPerson());
        flatSearchCriteria.setMaxSecurityAmountPerPerson(localFlatSearchCriteria.getMaxSecurityAmountPerPerson());
        flatSearchCriteria.setAreaRange(localFlatSearchCriteria.getAreaRange());
        //flatSearchCriteria.setGender(localFlatSearchCriteria.getGender());
        flatSearchCriteria.setRequesterId(localFlatSearchCriteria.getRequesterId());
        //flatSearchCriteria.setNotifyIfAvailable(localFlatSearchCriteria.isNotifyIfAvailable());
        //flatSearchCriteria.setPublicPost(localFlatSearchCriteria.isPublicPost());
        flatSearchCriteria.setLocationLatitude(localFlatSearchCriteria.getLocationLatitude());
        flatSearchCriteria.setLocationLongitude(localFlatSearchCriteria.getLocationLongitude());
        //flatSearchCriteria.setMessage(localFlatSearchCriteria.getMessage());
        flatSearchCriteria.setRequesterName(localFlatSearchCriteria.getRequesterName());
        flatSearchCriteria.setRequesterProfilePicture(localFlatSearchCriteria.getRequesterProfilePicture());
        flatSearchCriteria.setSelectedLocation(localFlatSearchCriteria.getSelectedLocation());
        flatSearchCriteria.setFilterResetDone(localFlatSearchCriteria.isFilterResetDone());

        return flatSearchCriteria;
    }

    public static LocalFlatSearchCriteria convertFlatSearchCriteriaToLocalFlatSearchCriteria(FlatSearchCriteria flatSearchCriteria) {
        if (flatSearchCriteria == null) {
            return null;
        }
        LocalFlatSearchCriteria localFlatSearchCriteria = new LocalFlatSearchCriteria();

        localFlatSearchCriteria.setId(flatSearchCriteria.getId());
        //localFlatSearchCriteria.setSharing(flatSearchCriteria.getSharing());
        localFlatSearchCriteria.setMinRentAmountPerPerson(flatSearchCriteria.getMinRentAmountPerPerson());
        localFlatSearchCriteria.setMaxRentAmountPerPerson(flatSearchCriteria.getMaxRentAmountPerPerson());
        localFlatSearchCriteria.setMinSecurityAmountPerPerson(flatSearchCriteria.getMinSecurityAmountPerPerson());
        localFlatSearchCriteria.setMaxSecurityAmountPerPerson(flatSearchCriteria.getMaxSecurityAmountPerPerson());
        localFlatSearchCriteria.setAreaRange(flatSearchCriteria.getAreaRange());
        //localFlatSearchCriteria.setGender(flatSearchCriteria.getGender());
        localFlatSearchCriteria.setRequesterId(flatSearchCriteria.getRequesterId());
        //localFlatSearchCriteria.setNotifyIfAvailable(flatSearchCriteria.getNotifyIfAvailable());
        //localFlatSearchCriteria.setPublicPost(flatSearchCriteria.getPublicPost());
        localFlatSearchCriteria.setLocationLatitude(flatSearchCriteria.getLocationLatitude());
        localFlatSearchCriteria.setLocationLongitude(flatSearchCriteria.getLocationLongitude());
        //localFlatSearchCriteria.setMessage(flatSearchCriteria.getMessage());
        localFlatSearchCriteria.setRequesterName(flatSearchCriteria.getRequesterName());
        localFlatSearchCriteria.setRequesterProfilePicture(flatSearchCriteria.getRequesterProfilePicture());
        localFlatSearchCriteria.setSelectedLocation(flatSearchCriteria.getSelectedLocation());
        localFlatSearchCriteria.setFilterResetDone(flatSearchCriteria.getFilterResetDone());

        return localFlatSearchCriteria;
    }

    public String getSelectedLocation() {
        return selectedLocation;
    }

    public void setSelectedLocation(String selectedLocation) {
        this.selectedLocation = selectedLocation;
    }

    public boolean isFilterResetDone() {
        return filterResetDone;
    }

    public void setFilterResetDone(boolean filterResetDone) {
        this.filterResetDone = filterResetDone;
    }
}
