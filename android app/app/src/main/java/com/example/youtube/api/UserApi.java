package com.example.youtube.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.youtube.R;
import com.example.youtube.entities.EmailCheckRequest;
import com.example.youtube.entities.EmailCheckResponse;
import com.example.youtube.entities.LoginRequest;
import com.example.youtube.entities.LoginResponse;
import com.example.youtube.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserApi {
    Retrofit retrofit;
    usersWebServiceApi usersWebServiceApi;

    public UserApi(Context context) {
        retrofit = new Retrofit.Builder().baseUrl(context.getString(R.string.baseUrl))
                .addConverterFactory(GsonConverterFactory.create()).build();
        usersWebServiceApi = retrofit.create(usersWebServiceApi.class);
    }

    public void createUser(User newUser) {
        Call<Void> call = usersWebServiceApi.createUser(newUser);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
            }
        });
    }

    public void checkEmailExists(String email, final ApiCallback<Boolean> callback) {
        EmailCheckRequest emailCheckRequest = new EmailCheckRequest(email);
        Call<EmailCheckResponse> call = usersWebServiceApi.checkEmailExists(emailCheckRequest);
        call.enqueue(new Callback<EmailCheckResponse>() {
            @Override
            public void onResponse(@NonNull Call<EmailCheckResponse> call, @NonNull Response<EmailCheckResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().isExists());
                } else {
                    callback.onSuccess(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<EmailCheckResponse> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
    public void login(String email, String password, final ApiCallback<LoginResponse> callback) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = usersWebServiceApi.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                callback.onError("Login failed: " + t.getMessage());
            }
        });
    }
    public void validateToken(String token, final ApiCallback<User> callback) {
        Call<User> call = usersWebServiceApi.validateToken("Bearer " + token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Token validation failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                callback.onError("Token validation failed: " + t.getMessage());
            }
        });
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

}