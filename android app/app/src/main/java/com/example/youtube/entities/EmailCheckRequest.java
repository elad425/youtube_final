package com.example.youtube.entities;

public class EmailCheckRequest {
    private String email;

    public EmailCheckRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
