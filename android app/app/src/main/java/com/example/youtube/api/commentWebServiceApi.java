package com.example.youtube.api;

import com.example.youtube.entities.Comment;
import com.example.youtube.entities.Comment2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface commentWebServiceApi {

    @GET("api/videos/comment/{id}")
    Call<List<Comment>> getCommentById(@Path("id") String id);

    @POST("api/videos/comment")
    Call<Comment2> createComment(@Body Comment comment, @Header("Authorization") String token);

    @DELETE("api/videos/comment/{id}")
    Call<Void> deleteComment(@Path("id") String id,@Header("Authorization") String token);

    @PATCH("api/videos/comment/{id}")
    Call<Void> updateComment(@Path("id") String id,@Body Comment comment,@Header("Authorization") String token);
}
