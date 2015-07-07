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

    Request() {
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
}
