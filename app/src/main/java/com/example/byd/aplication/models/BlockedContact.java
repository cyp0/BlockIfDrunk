package com.example.byd.aplication.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BlockedContact {
private String date;
private List<Contact> contacts;

    public BlockedContact() {
    }

    public BlockedContact(String date, List<Contact> contacts) {
        this.date = date;
        this.contacts = contacts;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
