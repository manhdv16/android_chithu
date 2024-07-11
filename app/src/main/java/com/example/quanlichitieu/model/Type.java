package com.example.quanlichitieu.model;

import java.io.Serializable;

public class Type implements Serializable {
    private String id;
    private String name;
    private String status;
    public Type(String name,String status) {
        this.name = name;
        this.status = status;
    }

    public Type(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Type() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
