package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.R;
import com.example.vivek.rentalmates.data.LocalFlatInfo;

public class AvailableFlatListItem {
    public final String flatName;
    public final String location;
    public final String address;
    public final String rentAmount;
    public final String securityAmount;
    public final int flatPictureResourceId;

    public AvailableFlatListItem(LocalFlatInfo flatInfo) {
        this.flatPictureResourceId = R.drawable.flatview2;
        this.flatName = flatInfo.getFlatName();
        this.location = flatInfo.getCity();
        this.address = flatInfo.getAddress();
        if (flatInfo.getNumberOfUsers() == 0) {
            this.rentAmount = "0";
            this.securityAmount = "0";
        } else {
            this.rentAmount = String.valueOf(flatInfo.getRentAmount() / flatInfo.getNumberOfUsers());
            this.securityAmount = String.valueOf(flatInfo.getSecurityAmount() / flatInfo.getNumberOfUsers());
        }
    }
}
