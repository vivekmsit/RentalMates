package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

@Entity
public class Request implements Serializable {

    enum RequestType {
        NONE,
        ADD_NEW_MEMBER_TO_FLAT,
        ADD_NEW_MEMBER_TO_EXPENSE_GROUP
    }

    enum RequestStatus {
        NONE,
        ACCEPTED,
        REJECTED,
        PENDING
    }

    @Id
    Long id;

    private RequestType type;
    private RequestStatus status;
    private String data;
    private Long requesterId;
    private Long requestProviderId;

    Request() {
        type = RequestType.NONE;
        status = RequestStatus.NONE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
}
