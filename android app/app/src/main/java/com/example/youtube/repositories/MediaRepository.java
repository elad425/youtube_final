package com.example.youtube.repositories;

import static com.example.youtube.utils.imageConverter.toBitmap;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.api.MediaApi;
import com.example.youtube.data.AppDatabase;
import com.example.youtube.entities.Comment;
import com.example.youtube.entities.Image;
import com.example.youtube.entities.User;
import com.example.youtube.entities.Video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MediaRepository {
    private final MediaApi api;
    private final AppDatabase db;
    private final MutableLiveData<byte[]> video;
    private final Application app;



    public MediaRepository(Application application) {
        db = AppDatabase.getInstance(application);
        this.app = application;
        video = new MutableLiveData<>();
        api = new MediaApi(db.imgDao(), application.getApplicationContext());

    }

    public Bitmap getImage(String id){
        Image i = db.imgDao().getImageById(id);
        if (i == null){
            return null;
        }
        return toBitmap(i.getImg());
    }

    public void initVideoMedia(){
        List<Video> videos = db.videoDao().getAllVideos();
        for(Video v : videos){
            if (db.imgDao().getImageById(v.getThumbnail()) == null) {
                api.getThumbnail(v.getThumbnail());
            }
            if (db.imgDao().getImageById(v.getUserDetails().getIcon()) == null){
                api.getProfileImage(v.getUserDetails().getIcon());
            }
        }
    }

    public void initCommentMedia(List<Comment> comments){
        for(Comment c : comments){
            if (db.imgDao().getImageById(c.getUser_id().getIcon()) == null) {
                api.getProfileImage(c.getUser_id().getIcon());
            }
        }
    }

    public void initUserProfilePic(User user){
        api.getProfileImage(user.getIcon());
    }

    public LiveData<List<Image>> getAllImagesLive(){
        return db.imgDao().getAllImagesLive();
    }

    public void downloadVideo(String path){
        api.downloadVideo(path, new MediaApi.ApiCallback<byte[]>() {
            @Override
            public void onSuccess(byte[] result) {
                video.setValue(result);
            }

            @Override
            public void onError(String error) {
            }
        });
    }
    public void uploadProfileImg(Uri imageUri, MediaApi.ApiCallback<String> callback) {
        api.uploadImageToServer(imageUri, app.getApplicationContext(), callback);
    }
    public File byteArrayToFile() throws IOException {
        File temp = File.createTempFile("temp", ".mp4",app.getExternalFilesDir(Environment.DIRECTORY_MOVIES));
        try (FileOutputStream fos = new FileOutputStream(temp)) {
            fos.write(video.getValue());
            fos.flush();
        }
        return temp;
    }

    public LiveData<byte[]> getVideoLive(){
        return video;
    }

}
