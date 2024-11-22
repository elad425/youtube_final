package com.example.youtube.api;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface mediaWebServiceApi {

    @GET("uploads\\{path}")
    Call<ResponseBody> getProfileImage(@Path("path") String path);

    @GET("{path}")
    Call<ResponseBody> downloadFromPath(@Path(value = "path", encoded = true) String path);

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadProfileImage(@Part MultipartBody.Part file);
}
