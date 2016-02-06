package com.example.vivek.rentalmates.backend.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Entity
public class ChatMessage implements Serializable {
    @Id
    Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private Date date;
    private boolean sent;
    private boolean received;
    private boolean seen;

    public ChatMessage() {

    }

    public Long getId() {
        return id;
    }
}