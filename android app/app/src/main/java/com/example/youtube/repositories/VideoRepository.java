package com.example.youtube.repositories;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.example.youtube.api.MediaApi;
import com.example.youtube.entities.Video;
import com.example.youtube.api.VideoApi;
import com.example.youtube.data.AppDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoRepository {
    private final AppDatabase db;
    private final VideoApi videoApi;

    private final MediaApi mediaApi;

    private final Context context;

    private final LiveData<List<Video>> videos;

    public VideoRepository(Application application) {
        db = AppDatabase.getInstance(application);
        videoApi = new VideoApi(db.videoDao(),application.getApplicationContext());
        videoApi.getVideos();
        videos = db.videoDao().getAllVideosLive();
        context = application.getApplicationContext();
        mediaApi = new MediaApi(db.imgDao(), application.getApplicationContext());

    }
    public void getRecommendations(String currentVideoId,String userId, VideoApi.ApiCallback<String> callback) {
        videoApi.updateViews(currentVideoId, userId, callback); // Replace "userId" with actual user ID
    }
    public void insertVideo(Video newVideo) {
        db.videoDao().insert(newVideo);
        videoApi.createVideo(newVideo);
    }

    public void updateVideo(Video updatedVideo) {
        db.videoDao().update(updatedVideo);
        videoApi.updateVideo(updatedVideo.get_id(), updatedVideo);
    }

    public void deleteVideo(Video videoToDelete) {
        db.videoDao().delete(videoToDelete);
        videoApi.deleteVideo(videoToDelete.getUserDetails().get_id(),videoToDelete.get_id());
    }

    public void reloadVideos(){
        db.videoDao().clear();
        videoApi.getVideos();
    }

    public LiveData<List<Video>> getAllVideosLive() {
        return videos;
    }

    public void uploadVideoAndThumbnail(Uri videoUri, Uri thumbnailUri, VideoApi.ApiCallback<Map<String, String>> callback) {
        Map<String, String> urls = new HashMap<>();

        if (videoUri != null) {
            videoApi.uploadVideo(videoUri, context, new VideoApi.ApiCallback<String>() {
                @Override
                public void onSuccess(String videoUrl) {
                    urls.put("videoUrl", videoUrl);
                    if (thumbnailUri != null) {
                        mediaApi.uploadImageToServer(thumbnailUri, context, new MediaApi.ApiCallback<String>() {
                            @Override
                            public void onSuccess(String thumbnailUrl) {
                                urls.put("thumbnailUrl", thumbnailUrl);
                                callback.onSuccess(urls);
                            }

                            @Override
                            public void onError(String error) {
                                callback.onError(error);
                            }
                        });
                    } else {
                        callback.onSuccess(urls);
                    }
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } else if (thumbnailUri != null) {
            mediaApi.uploadImageToServer(thumbnailUri, context, new MediaApi.ApiCallback<String>() {
                @Override
                public void onSuccess(String thumbnailUrl) {
                    urls.put("thumbnailUrl", thumbnailUrl);
                    callback.onSuccess(urls);
                }

                @Override
                public void onError(String error) {
                    callback.onError(error);
                }
            });
        } else {
            callback.onSuccess(urls);
        }
    }

    public List<Video> getAllVideos() {
        return db.videoDao().getAllVideos();
    }

    public Video getVideoById(String id) {
        return db.videoDao().getVideoById(id);
    }





}