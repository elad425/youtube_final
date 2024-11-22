package com.example.youtube.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.entities.Video;
import com.example.youtube.repositories.VideoRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Video>> filteredVideos = new MutableLiveData<>();
    private final VideoRepository videoRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        videoRepository = new VideoRepository(application);
    }

    public LiveData<List<Video>> getFilteredVideos() {
        return filteredVideos;
    }

    public void filterVideos(String query) {
        List<Video> allVideos = videoRepository.getAllVideos();
        List<Video> filtered = new ArrayList<>();
        if (!query.isEmpty()) {
            for (Video video : allVideos) {
                if (video.getTitle().toLowerCase().startsWith(query.toLowerCase())) {
                    filtered.add(video);
                }
            }
        }
        filteredVideos.setValue(filtered);
    }
}