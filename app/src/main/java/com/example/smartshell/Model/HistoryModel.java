package com.example.smartshell.Model;

public class HistoryModel {
    String waterSalinity;
    String dateAndTime;

    public HistoryModel(String waterSalinity, String dateAndTime) {
        this.waterSalinity = waterSalinity;
        this.dateAndTime = dateAndTime;
    }

    public String getWaterSalinity() {
        return waterSalinity;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }
}
