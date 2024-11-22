package com.example.youtube;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.youtube.adapters.VideoListAdapter;
import com.example.youtube.data.UserSession;
import com.example.youtube.entities.User;
import com.example.youtube.screens.AddVideoActivity;
import com.example.youtube.screens.LogIn;
import com.example.youtube.screens.ProfilePage;
import com.example.youtube.screens.SearchVideo;
import com.example.youtube.viewmodels.LoginViewModel;
import com.example.youtube.viewmodels.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private User userId;
    private MainViewModel videoViewModel;
    private BottomNavigationView bottomNav;
    private LoginViewModel loginViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean isSwipe = false;
    private int receiveCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
        setupBottomNavigation();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        checkSavedToken();
        showLoadingIndicator();
        setupSwipeRefresh();

        if (checkPermissions()) {
            initializeData();
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomNavigationSelection();
        showLoadingIndicator();
        videoViewModel.reload();
        initializeData();
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::swipeToRefresh);
    }

    public void swipeToRefresh(){
        isSwipe = true;
        videoViewModel.reload();
        receiveCounter = 0;
        initializeData();
    }

    private void initializeData() {
        videoViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        observeData();
    }

    private void checkSavedToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            loginViewModel.validateToken(token);
            observeTokenValidation();
        }
    }

    private void observeTokenValidation() {
        loginViewModel.getLoginSuccessful().observe(this, isSuccessful -> {
            if (isSuccessful) {
                userId = UserSession.getInstance().getUser();
            }
        });
    }

    private void observeData() {
        AtomicBoolean videosLoaded = new AtomicBoolean(false);
        AtomicBoolean imagesLoaded = new AtomicBoolean(false);

        videoViewModel.getAllVideosLive().observe(this, videos -> {
            if (!videos.isEmpty()) {
                videosLoaded.set(true);
                receiveCounter += 1;
                checkDataAndSetupUI(videosLoaded.get(), imagesLoaded.get());
                videoViewModel.initImages();
            }else if(!isSwipe) {
                showLoadingIndicator();
            }
        });

        videoViewModel.getAllImagesLive().observe(this, images -> {
            if (!images.isEmpty()) {
                imagesLoaded.set(true);
                checkDataAndSetupUI(videosLoaded.get(), imagesLoaded.get());
            }
        });
    }

    private void checkDataAndSetupUI(boolean videosLoaded, boolean imagesLoaded) {
        if (videosLoaded && imagesLoaded && receiveCounter != 1) {
            isSwipe = false;
            receiveCounter = 0;
            swipeRefreshLayout.setRefreshing(false);
            hideLoadingIndicator();
            setupUI();
        }
    }

    private void showLoadingIndicator() {
        ProgressBar loadingIndicator;
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        RecyclerView lstVideos = findViewById(R.id.lstVideos);
        lstVideos.setVisibility(View.GONE);
    }

    private void hideLoadingIndicator() {
        ProgressBar loadingIndicator;
        loadingIndicator = findViewById(R.id.loadingIndicator);
        loadingIndicator.setVisibility(View.GONE);
        RecyclerView lstVideos = findViewById(R.id.lstVideos);
        lstVideos.setVisibility(View.VISIBLE);
    }

    private void setupUI() {
        RecyclerView lstVideos = findViewById(R.id.lstVideos);
        VideoListAdapter videoListAdapter = new VideoListAdapter(
                null, this, videoViewModel.getMediaRepository(), this);
        lstVideos.setAdapter(videoListAdapter);
        lstVideos.setLayoutManager(new LinearLayoutManager(this));

        videoViewModel.getAllVideosLive().observe(this, videoListAdapter::setVideos);

        ImageButton btnSearch = findViewById(R.id.search_button);
        btnSearch.setOnClickListener(v -> navigateToSearch());

        ImageButton btnCast = findViewById(R.id.cast_button);
        btnCast.setOnClickListener(v -> Toast.makeText(MainActivity.this,
                "The app doesn't support Chromecast yet", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                navigateToProfile();
                return true;
            } else if (itemId == R.id.navigation_add_video) {
                navigateToAddVideo();
                return true;
            } else return itemId == R.id.navigation_home;
        });
        updateBottomNavigationSelection();
    }

    private void updateBottomNavigationSelection() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
        }
    }

    private void navigateToSearch() {
        startActivity(new Intent(this, SearchVideo.class));
    }

    private void navigateToProfile() {
        startActivity(new Intent(MainActivity.this, ProfilePage.class));
    }

    private void navigateToAddVideo() {
        if (userId != null) {
            startActivity(new Intent(MainActivity.this, AddVideoActivity.class));
        } else {
            Toast.makeText(MainActivity.this, "Please log in to add a video", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LogIn.class));
        }
    }

    private boolean checkPermissions() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= 33) {
            int resultVideo = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO);
            int resultImages = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            return resultVideo == PackageManager.PERMISSION_GRANTED &&
                    resultImages == PackageManager.PERMISSION_GRANTED &&
                    resultCamera == PackageManager.PERMISSION_GRANTED;
        } else {
            int resultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return resultStorage == PackageManager.PERMISSION_GRANTED &&
                    resultCamera == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
            }, 1);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && Build.VERSION.SDK_INT >= 33) {
            boolean videoPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean imagesPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean cameraPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
            if (videoPermission && imagesPermission && cameraPermission) {
                initializeData();
            } else {
                Toast.makeText(MainActivity.this, "this app need permissions, please go to setting and grant them", Toast.LENGTH_SHORT).show();
            }
        } else if (grantResults.length > 0) {
            boolean storagePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            if (storagePermission && cameraPermission) {
                initializeData();
            } else {
                Toast.makeText(MainActivity.this, "this app need permissions, please go to setting and grant them", Toast.LENGTH_SHORT).show();
            }
        }
    }
}