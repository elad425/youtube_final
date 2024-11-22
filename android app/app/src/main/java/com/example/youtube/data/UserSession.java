package com.example.youtube.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.youtube.entities.User;

public class UserSession {
    private static UserSession instance;
    private User user;

    private String token;
    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    public void clearUserSession(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("token");
        editor.apply();
        user = null;
        token=null;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setToken (String token){this.token=token;}
    public String getToken (){return token;}
    public User getUser() {
        return user;
    }
}

