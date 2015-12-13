package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.backend.userProfileApi.model.FlatSearchCriteria;

public class RoomMateListItem {
    public final String name;
    public final String profilePictureLink;
    public final int minRent;
    public final int maxRent;

    public RoomMateListItem(FlatSearchCriteria flatSearchCriteria) {
        this.name = flatSearchCriteria.getRequesterName();
        this.minRent = flatSearchCriteria.getMinRentAmountPerPerson();
        this.maxRent = flatSearchCriteria.getMaxRentAmountPerPerson();
        this.profilePictureLink = flatSearchCriteria.getRequesterProfilePicture();
    }
}
