package com.fabianbleile.fordigitalimmigrants.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Contact {
    @PrimaryKey
    private int cid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "phonenumber")
    private String phonenumber;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "birthday")
    private String birthday;

    @ColumnInfo(name = "hometown")
    private String hometown;

    @ColumnInfo(name = "instagram")
    private String instagram;

    @ColumnInfo(name = "facebook")
    private String facebook;

    @ColumnInfo(name = "snapchat")
    private String snapchat;

    @ColumnInfo(name = "twitter")
    private String twitter;

    @ColumnInfo(name = "location")
    private String location;

    public Contact(int cid, String name, String phonenumber, String email, String birthday, String hometown, String instagram, String facebook, String snapchat, String twitter, String location) {
        this.cid = cid;
        this.name = name;
        this.phonenumber = phonenumber;
        this.email = email;
        this.birthday = birthday;
        this.hometown = hometown;
        this.instagram = instagram;
        this.facebook = facebook;
        this.snapchat = snapchat;
        this.twitter = twitter;
        this.location = location;
    }

    public int getCid() {
        return cid;
    }
    public void setCid(int cid) {
        this.cid = cid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getBirthday() {
        return birthday;
    }
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
    public String getHometown() {
        return hometown;
    }
    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
    public String getInstagram() {
        return instagram;
    }
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
    public String getFacebook() {
        return facebook;
    }
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }
    public String getSnapchat() {
        return snapchat;
    }
    public void setSnapchat(String snapchat) {
        this.snapchat = snapchat;
    }
    public String getTwitter() {
        return twitter;
    }
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
