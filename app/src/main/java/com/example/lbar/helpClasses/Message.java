package com.example.lbar.helpClasses;

public class Message {
    private String senderUserId;
    private String receiverUserId;
    private String message;

    public Message(String senderUserId, String receiverUserId, String message) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.message = message;
    }

    public Message() {
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
