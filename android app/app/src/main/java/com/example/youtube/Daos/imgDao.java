package com.example.youtube.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube.entities.Image;

import java.util.List;

@Dao
public interface imgDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Image img);

    @Update
    void update(Image img);

    @Query("SELECT * FROM image WHERE path = :path")
    Image getImageById(String path);

    @Query("SELECT * FROM image")
    LiveData<List<Image>> getAllImagesLive();
}
