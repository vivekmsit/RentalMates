package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnSave;

import java.io.Serializable;

@Entity
public class Request implements Serializable {
    @Id
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
    private String requestResult;

    public Request() {
        updateCount = -1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getUpdateCount() {
        return updateCount;
    }

    @OnSave
    public void incrementUpdateCount() {
        updateCount++;
    }

    public void resetUpdateCount() {
        updateCount = -1;
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

    public void setRequesterProfilePicLink(String requesterProfilePic) {
        this.requesterProfilePicLink = requesterProfilePicLink;
    }

    public String getRequestResult() {
        return requestResult;
    }

    public void setRequestResult(String requestResult) {
        this.requestResult = requestResult;
    }

}
