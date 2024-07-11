package com.example.quanlichitieu.model;

import java.io.Serializable;

public class Notification implements Serializable {
    private String id,ten,tansuat,time, note;
    private boolean isActive;
    public Notification(){}

    public Notification(String id, String ten, String tansuat, String time, String note, boolean isActive) {
        this.id= id;
        this.ten = ten;
        this.tansuat = tansuat;
        this.time = time;
        this.note = note;
        this.isActive = isActive;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getTansuat() {
        return tansuat;
    }

    public void setTansuat(String tansuat) {
        this.tansuat = tansuat;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
