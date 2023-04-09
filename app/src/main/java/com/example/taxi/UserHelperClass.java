package com.example.taxi;

public class UserHelperClass {

    String full_name, email, contact, imageUrl;

    public UserHelperClass(){}

    public UserHelperClass(String full_name, String email, String contact,
                            String imageUrl ){
        this.full_name = full_name;
        this.contact = contact;
        this.imageUrl = imageUrl;
        this.email = email;

    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
