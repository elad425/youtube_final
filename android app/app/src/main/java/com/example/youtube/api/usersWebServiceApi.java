package com.example.youtube.api;

import com.example.youtube.entities.EmailCheckRequest;
import com.example.youtube.entities.EmailCheckResponse;
import com.example.youtube.entities.LoginRequest;
import com.example.youtube.entities.LoginResponse;
import com.example.youtube.entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface usersWebServiceApi {

    @POST("api/users")
    Call<Void> createUser(@Body User user);

    @POST("api/users/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("api/tokens")
    Call<User> validateToken(@Header("Authorization") String token);

    @POST("api/users/check-email")
    Call<EmailCheckResponse> checkEmailExists(@Body EmailCheckRequest email);
}

