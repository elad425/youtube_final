package com.example.youtube.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.youtube.MainActivity;
import com.example.youtube.R;
import com.example.youtube.entities.User;
import com.example.youtube.entities.Video;
import com.example.youtube.data.UserSession;
import com.example.youtube.viewmodels.AddVideoViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Map;

public class AddVideoActivity extends AppCompatActivity {

    private static final int PICK_VIDEO_REQUEST = 1;
    private static final int PICK_THUMBNAIL_REQUEST = 2;
    private static final int REQUEST_VIDEO_CAPTURE = 3;

    private EditText videoNameEditText;
    private ImageView thumbnailImageView;
    private TextView thumbnailPlaceholder;
    private VideoView videoVideoView;
    private TextView videoPlaceholder;
    private Uri videoUri, thumbnailUri;
    private AddVideoViewModel viewModel;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        viewModel = new ViewModelProvider(this).get(AddVideoViewModel.class);

        setupWindow();
        initializeUI();
        setupBottomNavigation();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBottomNavigationSelection();
    }

    private void setupWindow() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
    }

    private void initializeUI() {
        videoNameEditText = findViewById(R.id.et_video_title);
        thumbnailImageView = findViewById(R.id.iv_thumbnail);
        videoVideoView = findViewById(R.id.vv_video);
        videoPlaceholder = findViewById(R.id.tv_add_video);
        thumbnailPlaceholder = findViewById(R.id.tv_add_thumbnail);

        videoVideoView.setOnClickListener(v -> selectVideo());
        thumbnailImageView.setOnClickListener(v -> selectThumbnail());

        Button addVideoButton = findViewById(R.id.btn_add_video);
        addVideoButton.setOnClickListener(v -> {
            try {
                addVideo();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ImageButton recordVideoButton = findViewById(R.id.record_video_button);
        recordVideoButton.setOnClickListener(v -> openCamera());
    }

    private void observeViewModel() {
        viewModel.getVideoAddedSuccessfully().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Video added successfully", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
                viewModel.reloadVideos();
                finish();
            }
        });
        viewModel.getUploadUrls().observe(this, urls -> {
            if (urls != null && !urls.isEmpty()) {
                uploadVideoData(urls);
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_profile) {
                navigateToProfilePage();
                return true;
            }
            else
                if (itemId == R.id.navigation_home) {
                navigateToMainActivity();
                return true;
            }
            else return itemId == R.id.navigation_add_video;
        });

        updateBottomNavigationSelection();
    }

    private void updateBottomNavigationSelection() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.navigation_add_video);
        }
    }

    private void navigateToProfilePage() {
        finish();
        Intent intent = new Intent(AddVideoActivity.this, ProfilePage.class);
        startActivity(intent);
    }

    private void navigateToMainActivity() {
        finish();
        Intent intent = new Intent(AddVideoActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    private void selectThumbnail() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_THUMBNAIL_REQUEST);
    }

    private void openCamera() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = selectedUri;
                playVideoSample();
            } else if (requestCode == PICK_THUMBNAIL_REQUEST) {
                thumbnailUri = selectedUri;
                displayThumbnailSample();
            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                videoUri = selectedUri;
                playVideoSample();
            }
        }
    }

    private void playVideoSample() {
        videoVideoView.setVideoURI(videoUri);
        videoPlaceholder.setText("");
        videoPlaceholder.setBackgroundColor(0x00FFFFFF);
        videoVideoView.start();
    }

    private void displayThumbnailSample() {
        thumbnailImageView.setImageURI(thumbnailUri);
        thumbnailPlaceholder.setText("");
        thumbnailPlaceholder.setBackgroundColor(0x00FFFFFF);
    }

    private void addVideo() throws IOException {
        String videoName = videoNameEditText.getText().toString().trim();
        if (videoName.isEmpty() || thumbnailUri == null || videoUri == null) {
            Toast.makeText(this, "Please fill all fields and choose a video and a thumbnail", Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.uploadVideoAndThumbnail(videoUri, thumbnailUri);
    }

    private void uploadVideoData(Map<String, String> urls) {
        String videoUrl = urls.get("videoUrl");
        String thumbnailUrl = urls.get("thumbnailUrl");
        String videoName = videoNameEditText.getText().toString().trim();
        User user = UserSession.getInstance().getUser();
        assert videoUrl != null;
        Video newVideo = new Video(videoName, "", videoUrl, thumbnailUrl, user);
        viewModel.addVideo(newVideo);
    }
}