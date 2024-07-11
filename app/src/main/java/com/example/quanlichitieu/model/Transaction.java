package com.example.quanlichitieu.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private double amount;
    private String type;
    private String dateUpdate;
    private String note;
    private String status;
    public Transaction(){}
    public Transaction(String id, double amount, String type, String dateUpdate, String note,String status) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.dateUpdate = dateUpdate;
        this.note = note;
        this.status=status;
    }
    public Transaction(double amount, String type, String dateUpdate, String note,String status) {
        this.amount = amount;
        this.type = type;
        this.dateUpdate = dateUpdate;
        this.note = note;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
