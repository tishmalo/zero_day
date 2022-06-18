package com.example.blooddonationapp.Model;

public class Location2 {

    private double Longitude;
    private double Latitude;
    private String bloodgroup;
    private String number;


    public Location2() {
    }

    public Location2(double longitude, double latitude, String bloodgroup, String number) {
        Longitude = longitude;
        Latitude = latitude;
        this.bloodgroup = bloodgroup;
        this.number = number;

    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }


}
