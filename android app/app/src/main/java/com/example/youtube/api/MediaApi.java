package com.example.youtube.api;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.youtube.Daos.imgDao;
import com.example.youtube.R;
import com.example.youtube.entities.Image;
import com.example.youtube.utils.GeneralUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MediaApi {
    Retrofit retrofit;
    static mediaWebServiceApi mediaWebServiceApi;
    private final imgDao dao;

    public MediaApi(imgDao imgDao, Context context) {
        dao = imgDao;
        retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create()).build();
        mediaWebServiceApi = retrofit.create(mediaWebServiceApi.class);
    }

    public void getProfileImage(String path) {
        Call<ResponseBody> call = mediaWebServiceApi.getProfileImage(GeneralUtils.getStringAfterBackslash(path));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    assert response.body() != null;
                    Image i = new Image(response.body().bytes(),path);
                    dao.insert(i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    public void getThumbnail(String path) {
        String correctedPath = path.replace("\\", "/");
        Call<ResponseBody> call = mediaWebServiceApi.downloadFromPath(correctedPath);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    assert response.body() != null;
                    Image i = new Image(response.body().bytes(),path);
                    dao.insert(i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    public void downloadVideo(String path, final MediaApi.ApiCallback<byte[]> callback) {
        String correctedPath = path.replace("\\", "/");

        Call<ResponseBody> call = mediaWebServiceApi.downloadFromPath(correctedPath);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        byte[] videoBytes;
                        try {
                            videoBytes = body.bytes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if (response.isSuccessful()) {
                            callback.onSuccess(videoBytes);
                        } else {
                            callback.onError("Failed to get image: " + response.message());
                        }
                    }
                } else {
                    // Handle error
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                // Handle network error
                Log.e("API", "Network error: " + t.getMessage());
            }
        });
    }

    public void uploadImageToServer(Uri imageUri, Context context, final ApiCallback<String> callback) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            assert inputStream != null;
            byte[] imageData = GeneralUtils.getBytes(inputStream);

            String fileName = GeneralUtils.getFileName(context, imageUri);
            String fileExtension = GeneralUtils.getFileExtension(context, imageUri);
            if (fileExtension == null || fileExtension.isEmpty()) {
                fileExtension = "jpg"; // default to jpg if no extension
            }

            if (!fileName.endsWith("." + fileExtension)) {
                fileName += "." + fileExtension;
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageData);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", fileName, requestFile);

            Call<ResponseBody> call = mediaWebServiceApi.uploadProfileImage(body);
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
                        } catch (IOException e) {
                            callback.onError("Failed to read response: " + e.getMessage());
                        } catch (JSONException e) {
                            callback.onError("Failed to parse response: " + e.getMessage());
                        }
                    } else {
                        callback.onError("Failed to upload image: " + response.message());
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

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

}
