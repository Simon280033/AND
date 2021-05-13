package com.example.andproject.Entities;

public class FellowshipRequest {
    public String requestId;
    public String fellowshipId;
    public String requesterId;
    public String requestDate;
    public int isAccepted;

    public FellowshipRequest(String requestId, String fellowshipId, String requesterId, String requestDate, int isAccepted) {
        this.requestId = requestId;
        this.fellowshipId = fellowshipId;
        this.requesterId = requesterId;
        this.requestDate = requestDate;
        this.isAccepted = isAccepted;
    }
}
