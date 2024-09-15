package com.example.smartshell.Model;

public class HarvestDatesModel {
    String startDate;
    String endDate;
    String docId;
    long remainingDays;

    public HarvestDatesModel(String startDate, String endDate, String docId, long remainingDays) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.docId = docId;
        this.remainingDays = remainingDays;
    }

    public String getStartDate() {
        return startDate;
    }

    public long getRemainingDays(){ return  remainingDays;}

    public String getEndDate() {
        return endDate;
    }

    public String getDocId() {
        return docId;
    }
}
