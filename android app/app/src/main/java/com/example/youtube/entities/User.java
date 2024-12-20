package com.example.youtube.entities;

public class User {
    private String _id;
    private String username;
    private String email;
    private String password;
    private String icon;

    public User(String username, String email, String password, String icon) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.icon = icon;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIcon() {return icon;}

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
