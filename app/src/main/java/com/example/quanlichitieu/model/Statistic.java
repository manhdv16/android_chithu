package com.example.quanlichitieu.model;

import java.io.Serializable;

public class Statistic implements Serializable {
    private String id;
    private String timePeriod;
    private double totalIncome,totalExpense;

    public Statistic(){}

    public Statistic(String id, String timePeriod, double totalIncome, double totalExpense) {
        this.id = id;
        this.timePeriod = timePeriod;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public Statistic(String timePeriod, double totalIncome, double totalExpense) {
        this.timePeriod = timePeriod;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }
}
