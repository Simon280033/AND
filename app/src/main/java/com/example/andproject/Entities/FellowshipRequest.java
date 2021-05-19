package com.example.andproject.Entities;

public class FellowshipRequest {
    private String requestId;
    private String fellowshipId;
    private String requesterId;
    private String requestDate;
    private int isAccepted;

    public FellowshipRequest(String requestId, String fellowshipId, String requesterId, String requestDate, int isAccepted) {
        this.requestId = requestId;
        this.fellowshipId = fellowshipId;
        this.requesterId = requesterId;
        this.requestDate = requestDate;
        this.isAccepted = isAccepted;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getFellowshipId() {
        return fellowshipId;
    }

    public void setFellowshipId(String fellowshipId) {
        this.fellowshipId = fellowshipId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public int getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(int isAccepted) {
        this.isAccepted = isAccepted;
    }
}
