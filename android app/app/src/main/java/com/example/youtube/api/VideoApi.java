package com.example.youtube.api;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.youtube.R;
import com.example.youtube.data.UserSession;
import com.example.youtube.Daos.videoDao;
import com.example.youtube.entities.Video;
import com.example.youtube.utils.GeneralUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoApi {
    private final videoDao dao;
    Retrofit retrofit;
    videoWebServiceApi videoWebServiceApi;

    public VideoApi(videoDao dao, Context context) {
        this.dao = dao;
        retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create()).build();
        videoWebServiceApi = retrofit.create(videoWebServiceApi.class);
    }

    public void getVideos() {
        Call<List<Video>> call = videoWebServiceApi.getVideos();
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(@NonNull Call<List<Video>> call, @NonNull Response<List<Video>> response) {
                dao.clear();
                dao.insertList(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<List<Video>> call, @NonNull Throwable t) {}
        });
    }

    public void createVideo(Video newvideo) {
        String token = "Bearer " + UserSession.getInstance().getToken();
        Call<Void> call = videoWebServiceApi.createVideo(newvideo.getUserDetails().get_id(),newvideo,token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }

    public void updateVideo(String videoId, Video updatedvideo) {
        String token = "Bearer " + UserSession.getInstance().getToken();
        Call<Void> call = videoWebServiceApi.updateVideo(updatedvideo.getUserDetails().get_id(),videoId,updatedvideo,token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }

    public void updateViews(String videoId, String userId, final ApiCallback<String> callback) {
        Call<ResponseBody> call = videoWebServiceApi.updateViews(videoId, userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        // Extract only the recommendations part
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String recommendations = jsonResponse.getString("recommendations");
                        callback.onSuccess(recommendations);
                    } catch (IOException | JSONException e) {
                        callback.onError("Failed to parse response: " + e.getMessage());
                    }
                } else {
                    callback.onError("Failed to get recommendations: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    public void uploadVideo(Uri videoUri,Context context, final ApiCallback<String> callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(videoUri);
            assert inputStream != null;
            byte[] videoData = GeneralUtils.getBytes(inputStream);

            String fileName = GeneralUtils.getFileName(context, videoUri);
            String fileExtension = GeneralUtils.getFileExtension(context, videoUri);
            if (fileExtension == null || fileExtension.isEmpty()) {
                fileExtension = "mp4"; // default to mp4 if no extension
            }

            if (!fileName.endsWith("." + fileExtension)) {
                fileName += "." + fileExtension;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("video/*"), videoData);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);

            String token = "Bearer " + UserSession.getInstance().getToken();
            Call<ResponseBody> call = videoWebServiceApi.uploadVideo(body, token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            assert response.body() != null;
                            String responseBody = response.body().string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String filePath = jsonObject.getString("path");
                            callback.onSuccess(filePath);
                        } catch (IOException | JSONException e) {
                            callback.onError("Failed to process response: " + e.getMessage());
                        }
                    } else {
                        callback.onError("Failed to upload video: " + response.message());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (IOException e) {
            callback.onError("Failed to open input stream: " + e.getMessage());
        }
    }

    public void deleteVideo(String userId,String videoId) {
        String token = "Bearer " + UserSession.getInstance().getToken();
        Call<Void> call = videoWebServiceApi.deleteVideo(userId,videoId,token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }



    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}
