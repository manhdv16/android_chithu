package com.example.quanlichitieu.model;

import java.util.List;

public class TransactionGroup {
    private String date;
    private List<Transaction> transactions;

    public TransactionGroup(String date, List<Transaction> transactions) {
        this.date = date;
        this.transactions = transactions;
    }

    public String getDate() {
        return date;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}

