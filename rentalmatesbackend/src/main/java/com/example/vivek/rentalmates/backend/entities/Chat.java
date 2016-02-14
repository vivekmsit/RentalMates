package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat implements Serializable {
    @Id
    Long id;
    private Long firstMemberId;
    private Long secondMemberId;
    private List<Long> messages = new ArrayList<>();
    private int numberOfMessages;

    public Chat() {
        id = Long.valueOf(1);
        numberOfMessages = 0;
    }

    public Long getId() {
        return id;
    }

    public Long getFirstMemberId() {
        return firstMemberId;
    }

    public void setFirstMemberId(Long firstMemberId) {
        this.firstMemberId = firstMemberId;
    }

    public Long getSecondMemberId() {
        return secondMemberId;
    }

    public void setSecondMemberId(Long secondMemberId) {
        this.secondMemberId = secondMemberId;
    }

    public List<Long> getMessages() {
        return messages;
    }

    public void setMessages(List<Long> messages) {
        this.messages = messages;
    }

    public void addMessage(Long messageId) {
        this.messages.add(messageId);
        numberOfMessages++;
    }

    public void deleteMessage(Long messageId) {
        this.messages.remove(messageId);
        numberOfMessages--;
    }
}