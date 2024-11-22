package com.example.youtube.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.youtube.R;
import com.example.youtube.data.UserSession;
import com.example.youtube.entities.Comment;
import com.example.youtube.entities.Comment2;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommentApi {
    Retrofit retrofit;
    commentWebServiceApi commentWebServiceApi;

    public CommentApi(Context context) {
        retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create()).build();
        commentWebServiceApi = retrofit.create(commentWebServiceApi.class);
    }

    public void getCommentsById(String id, final VideoApi.ApiCallback<List<Comment>> callback){
        Call<List<Comment>> call = commentWebServiceApi.getCommentById(id);
        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Comment>> call, @NonNull Response<List<Comment>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Comment>> call, @NonNull Throwable t) {
            }
        });
    }

    public void createComment(Comment comment, final VideoApi.ApiCallback<Comment2> callback) {
        String token = "Bearer " + UserSession.getInstance().getToken();
        Call<Comment2> call = commentWebServiceApi.createComment(comment,token);
        call.enqueue(new Callback<Comment2>() {
            @Override
            public void onResponse(@NonNull Call<Comment2> call, @NonNull Response<Comment2> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Comment2> call, @NonNull Throwable t) {
            }
        });
    }

    public void deleteComment(String commentId) {
        String token = "Bearer " + UserSession.getInstance().getToken();
        Call<Void> call = commentWebServiceApi.deleteComment(commentId,token);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }

    public void updateComment(Comment comment) {
        String token = "Bearer " + UserSession.getInstance().getToken();
        Call<Void> call = commentWebServiceApi.updateComment(comment.get_id(),comment,token);
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
