package com.example.andproject.Entities;

public class Report {
    private String reportedId;
    private String reporterId;
    private String message;

    public Report(String reportedId, String reporterId, String message) {
        this.reportedId = reportedId;
        this.reporterId = reporterId;
        this.message = message;
    }

    public String getReportedId() {
        return reportedId;
    }

    public void setReportedId(String reportedId) {
        this.reportedId = reportedId;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
