package com.example.youtube.viewmodels;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.youtube.entities.User;
import com.example.youtube.data.UserSession;
import com.example.youtube.repositories.MediaRepository;

import java.util.Objects;

public class ProfilePageViewModel extends AndroidViewModel {
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUserLoggedIn = new MutableLiveData<>();
    private final MediaRepository mediaRepository;

    public ProfilePageViewModel(@NonNull Application application) {
        super(application);
        checkLoginStatus();
        mediaRepository = new MediaRepository(application);
    }

    public void checkLoginStatus() {
        User user = UserSession.getInstance().getUser();
        isUserLoggedIn.setValue(user != null);
        if (user != null) {
            loadUserData(user);
        }
    }

    private void loadUserData(User user) {
        currentUser.setValue(user);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void logOut() {
        UserSession.getInstance().clearUserSession(getApplication());
        isUserLoggedIn.setValue(false);
        currentUser.setValue(null);
    }

    public Bitmap getBitmap() {
        return mediaRepository.getImage(Objects.requireNonNull(currentUser.getValue()).getIcon());
    }

}