package com.example.youtube.repositories;

import android.app.Application;
import com.example.youtube.entities.User;
import com.example.youtube.api.UserApi;

public class UserRepository {
    private final UserApi api;

    public UserRepository(Application application) {
        api = new UserApi(application.getApplicationContext());
    }

    public void insertUser(User newUser) {
        api.createUser(newUser);
    }
}