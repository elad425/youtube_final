package com.example.youtube.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.api.MediaApi;
import com.example.youtube.api.UserApi;
import com.example.youtube.entities.User;
import com.example.youtube.repositories.MediaRepository;
import com.example.youtube.repositories.UserRepository;

public class SignUpViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final MutableLiveData<Boolean> signUpSuccessful = new MutableLiveData<>();
    private final UserApi userApi;


    public SignUpViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        mediaRepository = new MediaRepository(application);
        userApi = new UserApi(application.getApplicationContext());
    }

    public LiveData<Boolean> getSignUpSuccessful() {
        return signUpSuccessful;
    }

    public void signUp(User newUser) {
        userRepository.insertUser(newUser);
        signUpSuccessful.setValue(true);
    }

    public void checkEmailExists(String email, UserApi.ApiCallback<Boolean> callback) {
        userApi.checkEmailExists(email, callback);
    }

    public void uploadImageToServer(Uri imageUri, UserApi.ApiCallback<String> callback) {
        mediaRepository.uploadProfileImg(imageUri, new MediaApi.ApiCallback<String>() {
            @Override
            public void onSuccess(String imageUrl) {
                callback.onSuccess(imageUrl);
            }
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}