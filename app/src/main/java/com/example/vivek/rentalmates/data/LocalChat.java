package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.mainApi.model.Chat;

import java.io.Serializable;
import java.util.ArrayList;
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

    public void setId(Long id) {
        this.id = id;
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

    public static List<Chat> convertLocalChatListToChatList(List<LocalChat> localChats) {
        if (localChats == null) {
            return null;
        }
        List<Chat> chats = new ArrayList<>();
        for (LocalChat localChat : localChats) {
            Chat chat = new Chat();

            chat.setId(localChat.getId());
            chat.setId(localChat.getId());
            chat.setFirstMemberId(localChat.getFirstMemberId());
            chat.setSecondMemberId(localChat.getSecondMemberId());
            chat.setMessages(localChat.getMessages());

            chats.add(chat);
        }
        return chats;
    }

    public static List<LocalChat> convertChatListToLocalChatList(List<Chat> chats) {
        if (chats == null) {
            return null;
        }
        List<LocalChat> localChats = new ArrayList<>();
        for (Chat chat : chats) {
            LocalChat localChat = new LocalChat();

            localChat.setId(chat.getId());
            localChat.setFirstMemberId(chat.getFirstMemberId());
            localChat.setSecondMemberId(chat.getSecondMemberId());
            localChat.setMessages(chat.getMessages());

            localChats.add(localChat);
        }
        return localChats;
    }
}
