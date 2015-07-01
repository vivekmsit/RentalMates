package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.data.LocalFlatInfo;

public class FlatListItem {
    public final String flatName;
    public final String ownerName;
    public final String location;
    public final String members;
    public final String date;

    public FlatListItem(LocalFlatInfo flatInfo) {
        this.flatName = flatInfo.getFlatName();
        this.ownerName ="ownerName";
        this.location = flatInfo.getCity();
        this.members = "vivek, ashish";
        this.date = "date";
    }
}
