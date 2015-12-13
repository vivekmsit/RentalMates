package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.io.Serializable;

@Entity
public class LogStore implements Serializable {
    @Id
    Long id;

    @Index
    private String ownerEmailId;

    private String logs;

    public Long getId() {
        return id;
    }

    public String getLogs() {
        return logs;
    }

    public void writeLog(String log) {
        this.logs += log;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String ownerEmailId) {
        this.ownerEmailId = ownerEmailId;
    }
}
