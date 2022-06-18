package com.example.blooddonationapp.Model;

public class User {
    String bloodgroup, email, id, name, number, profileimage;

    public User() {
    }

    public User(String bloodgroup, String email, String id, String name, String number, String profileimage) {
        this.bloodgroup = bloodgroup;
        this.email = email;
        this.id = id;
        this.name = name;
        this.number = number;
        this.profileimage = profileimage;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
