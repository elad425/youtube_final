package com.example.youtube.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.api.VideoApi;
import com.example.youtube.entities.Video;
import com.example.youtube.repositories.VideoRepository;

import java.util.Map;

public class AddVideoViewModel extends AndroidViewModel {
    private final VideoRepository videoRepository;
    private final MutableLiveData<Boolean> videoAddedSuccessfully = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> uploadUrls = new MutableLiveData<>();

    public AddVideoViewModel(@NonNull Application application) {
        super(application);
        videoRepository = new VideoRepository(application);
    }

    public LiveData<Boolean> getVideoAddedSuccessfully() {
        return videoAddedSuccessfully;
    }

    public LiveData<Map<String, String>> getUploadUrls() {
        return uploadUrls;
    }

    public void addVideo(Video newVideo) {
        videoRepository.insertVideo(newVideo);
        videoAddedSuccessfully.setValue(true);
    }

    public void reloadVideos(){
        videoRepository.reloadVideos();
    }

    public void uploadVideoAndThumbnail(Uri videoUri, Uri thumbnailUri) {
        videoRepository.uploadVideoAndThumbnail(videoUri, thumbnailUri, new VideoApi.ApiCallback<Map<String, String>>() {
            @Override
            public void onSuccess(Map<String, String> result) {
                uploadUrls.setValue(result);
            }

            @Override
            public void onError(String error) {
                // Handle error
            }
        });
    }
}