package com.example.vivek.rentalmates.data;

import com.example.vivek.rentalmates.backend.mainApi.model.ChatMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalChatMessage implements Serializable {
    Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private Long chatId;
    private Date date;
    private boolean sent;
    private boolean received;
    private boolean seen;

    public LocalChatMessage() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public static List<ChatMessage> convertLocalChatMessageListToChatMessageList(List<LocalChatMessage> localChatMessages) {
        if (localChatMessages == null) {
            return null;
        }
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (LocalChatMessage localChatMessage : localChatMessages) {
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setId(localChatMessage.getId());
            chatMessage.setContent(localChatMessage.getContent());
            chatMessage.setReceived(localChatMessage.received);
            chatMessage.setSent(localChatMessage.sent);
            chatMessage.setSeen(localChatMessage.seen);
            chatMessage.setSenderId(localChatMessage.getSenderId());
            chatMessage.setReceiverId(localChatMessage.getReceiverId());

            chatMessages.add(chatMessage);
        }
        return chatMessages;
    }

    public static List<LocalChatMessage> convertChatMessageListToLocalChatMessageList(List<ChatMessage> chatMessages) {
        if (chatMessages == null) {
            return null;
        }
        List<LocalChatMessage> localChatMessages = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            LocalChatMessage localChatMessage = new LocalChatMessage();

            localChatMessage.setId(chatMessage.getId());
            localChatMessage.setContent(chatMessage.getContent());
            localChatMessage.setReceived(chatMessage.getReceived());
            localChatMessage.setSent(chatMessage.getSent());
            localChatMessage.setSeen(chatMessage.getSeen());
            localChatMessage.setSenderId(chatMessage.getSenderId());
            localChatMessage.setReceiverId(chatMessage.getReceiverId());

            localChatMessages.add(localChatMessage);
        }
        return localChatMessages;
    }
}
