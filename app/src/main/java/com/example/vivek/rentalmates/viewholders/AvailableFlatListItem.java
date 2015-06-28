package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.others.LocalFlatInfo;

public class AvailableFlatListItem {
    public final String flatName;
    public final String location;
    public final String address;
    public final String rentAmount;
    public final String securityAmount;

    public AvailableFlatListItem(LocalFlatInfo flatInfo) {
        this.flatName = flatInfo.getFlatName();
        this.location = flatInfo.getCity();
        this.address = flatInfo.getAddress();
        this.rentAmount = String.valueOf(flatInfo.getRentAmount() / flatInfo.getNumberOfUsers());
        this.securityAmount = String.valueOf(flatInfo.getSecurityAmount() / flatInfo.getNumberOfUsers());
    }
}
