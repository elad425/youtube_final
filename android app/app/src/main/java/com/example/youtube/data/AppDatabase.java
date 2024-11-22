package com.example.youtube.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.youtube.Daos.imgDao;
import com.example.youtube.Daos.videoDao;
import com.example.youtube.entities.Image;
import com.example.youtube.entities.Video;
import com.example.youtube.utils.userConverters;
import com.example.youtube.utils.videoConverters;

@Database(entities = {Video.class, Image.class}, version = 1)
@TypeConverters({videoConverters.class, userConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    static AppDatabase instance;
    public abstract videoDao videoDao();
    public abstract imgDao imgDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "userDb")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}