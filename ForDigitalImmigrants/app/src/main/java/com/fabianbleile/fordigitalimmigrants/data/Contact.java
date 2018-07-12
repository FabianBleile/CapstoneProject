package com.fabianbleile.fordigitalimmigrants.data;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity
public class Contact implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private Integer cid;

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

    public Contact(String name, String phonenumber, String email, String birthday, String hometown, String instagram, String facebook, String snapchat, String twitter, String location) {
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

    public Integer getCid() {
        return cid;
    }
    public void setCid(Integer cid) {
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

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel parcel) {
            return new Contact(parcel);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    private Contact(Parcel parcel) {
        this.name = parcel.readString();
        this.phonenumber = parcel.readString();
        this.email = parcel.readString();
        this.birthday = parcel.readString();
        this.hometown = parcel.readString();
        this.instagram = parcel.readString();
        this.facebook = parcel.readString();
        this.snapchat = parcel.readString();
        this.twitter = parcel.readString();
        this.location = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(phonenumber);
        parcel.writeString(email);
        parcel.writeString(birthday);
        parcel.writeString(hometown);
        parcel.writeString(instagram);
        parcel.writeString(facebook);
        parcel.writeString(snapchat);
        parcel.writeString(twitter);
        parcel.writeString(location);
    }
}
