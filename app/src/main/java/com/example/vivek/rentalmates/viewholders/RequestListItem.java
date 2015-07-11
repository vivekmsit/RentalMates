package com.example.vivek.rentalmates.viewholders;

import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;

public class RequestListItem {
    public final String requesterName;
    public final String requestedEntityName;
    public final String requesterProfilePicLink;

    public RequestListItem(Request request) {
        this.requesterName = request.getRequesterName();
        this.requestedEntityName = request.getRequestedEntityName();
        this.requesterProfilePicLink = request.getRequesterProfilePicLink();
    }
}
