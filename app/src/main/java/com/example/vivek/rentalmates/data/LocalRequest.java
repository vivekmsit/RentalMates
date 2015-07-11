package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.userProfileApi.model.Request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocalRequest implements Serializable {

    Long id;
    private int updateCount;
    private Long requesterId;
    private Long requestProviderId;
    private Long requestedEntity;
    private String entityType;
    private String status;
    private String requesterName;
    private String requestedEntityName;
    private String requesterProfilePicLink;

    public LocalRequest() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public Long getRequestProviderId() {
        return requestProviderId;
    }

    public void setRequestProviderId(Long requestProviderId) {
        this.requestProviderId = requestProviderId;
    }

    public Long getRequestedEntity() {
        return requestedEntity;
    }

    public void setRequestedEntity(Long requestedEntity) {
        this.requestedEntity = requestedEntity;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequestedEntityName() {
        return requestedEntityName;
    }

    public void setRequestedEntityName(String requestedEntityName) {
        this.requestedEntityName = requestedEntityName;
    }

    public String getRequesterProfilePicLink() {
        return requesterProfilePicLink;
    }

    public void setRequesterProfilePicLink(String requesterProfilePicLink) {
        this.requesterProfilePicLink = requesterProfilePicLink;
    }

    public static List<Request> convertLocalRequestToRequest(List<LocalRequest> localRequests) {
        if (localRequests == null) {
            return null;
        }
        List<Request> requests = new ArrayList<>();
        for (LocalRequest localRequest : localRequests) {
            Request data = new Request();

            data.setId(localRequest.getId());
            data.setEntityType(localRequest.getEntityType());
            data.setStatus(localRequest.getStatus());
            data.setRequestProviderId(localRequest.getRequestProviderId());
            data.setRequestedEntity(localRequest.getRequestedEntity());
            data.setRequesterId(localRequest.getRequesterId());
            data.setRequestedEntityName(localRequest.getRequestedEntityName());
            data.setRequesterName(localRequest.getRequesterName());
            data.setRequesterProfilePicLink(localRequest.getRequesterProfilePicLink());

            requests.add(data);
        }
        return requests;
    }

    public static List<LocalRequest> convertRequestToLocalRequest(List<Request> requests) {
        if (requests == null) {
            return null;
        }
        List<LocalRequest> localRequests = new ArrayList<>();
        for (Request request : requests) {
            LocalRequest data = new LocalRequest();

            data.setId(request.getId());
            data.setEntityType(request.getEntityType());
            data.setRequesterId(request.getRequesterId());
            data.setStatus(request.getStatus());
            data.setRequestedEntity(request.getRequestedEntity());
            data.setRequestProviderId(request.getRequestProviderId());
            data.setRequesterName(request.getRequesterName());
            data.setRequestedEntityName(request.getRequestedEntityName());
            data.setRequesterProfilePicLink(request.getRequesterProfilePicLink());

            localRequests.add(data);
        }
        return localRequests;
    }
}
