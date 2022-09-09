package com.example.lbar.helpClasses;

public class FriendRequest {

    private String requestID;
    private String fromID;
    private String toID;

    public FriendRequest(String requestID, String fromID, String toID) {
        this.requestID = requestID;
        this.fromID = fromID;
        this.toID = toID;
    }

    public FriendRequest(){

    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getToID() {
        return toID;
    }

    public void setToID(String toID) {
        this.toID = toID;
    }
}
