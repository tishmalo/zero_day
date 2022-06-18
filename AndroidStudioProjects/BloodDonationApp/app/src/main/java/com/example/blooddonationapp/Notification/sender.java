package com.example.blooddonationapp.Notification;

import android.provider.ContactsContract;

public class sender {

    public Data1 data;
    public String to;

    public sender() {
    }

    public sender(Data1 data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data1 getData() {
        return data;
    }

    public void setData(Data1 data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
