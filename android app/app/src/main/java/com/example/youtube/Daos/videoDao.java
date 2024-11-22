package com.example.youtube.Daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.youtube.entities.Video;

import java.util.List;

@Dao
public interface videoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Video video);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertList(List<Video> video);

    @Update
    void update(Video video);

    @Delete
    void delete(Video video);

    @Query("SELECT * FROM video WHERE _id = :id")
    Video getVideoById(String id);

    @Query("SELECT * FROM video")
    List<Video> getAllVideos();

    @Query("SELECT * FROM video")
    LiveData<List<Video>> getAllVideosLive();

    @Query("DELETE FROM video")
    void clear();
}