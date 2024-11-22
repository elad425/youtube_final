package com.example.youtube.utils;

import androidx.room.TypeConverter;

import com.example.youtube.entities.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class videoConverters {
    @TypeConverter
    public static String fromUser(User user) {
        if (user == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {}.getType();
        return gson.toJson(user, type);
    }

    @TypeConverter
    public static User toUser(String user) {
        if (user == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<User>() {}.getType();
        return gson.fromJson(user, type);
    }
}
