package com.example.vivek.rentalmates.data;

import java.io.Serializable;
import java.util.List;

public class LocalChat implements Serializable {
    Long id;
    private Long firstMemberId;
    private Long secondMemberId;
    private List<Long> messages;
    private int numberOfMessages;

    public LocalChat() {
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
