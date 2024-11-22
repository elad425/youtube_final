package com.example.youtube.entities;

public class UserDetails {
    private String username;
    private String icon;

    public UserDetails(String username, String icon) {
        this.username = username;
        this.icon = icon;
    }

    public String getUsername() {
        return username;
    }

    public String getIcon() {
        return icon;
    }
}