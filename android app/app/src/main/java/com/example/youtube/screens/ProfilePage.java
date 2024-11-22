package com.example.youtube.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youtube.MainActivity;
import com.example.youtube.R;
import com.example.youtube.entities.User;
import com.example.youtube.viewmodels.ProfilePageViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfilePage extends AppCompatActivity {
    private ProfilePageViewModel viewModel;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        viewModel = new ViewModelProvider(this).get(ProfilePageViewModel.class);

        setupWindow();
        setupUI();
        setupBottomNavigation();
        observeViewModel();
    }

    private void setupWindow() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));
    }

    private void setupUI() {
        Button btnLogIn = findViewById(R.id.btn_logIn);
        TextView username = findViewById(R.id.username);
        TextView userEmail = findViewById(R.id.user_email);
        ShapeableImageView userPic = findViewById(R.id.user_pic);
        ImageButton btnSettings = findViewById(R.id.settings);
        btnSettings.setOnClickListener(this::displaySettings);

        viewModel.isUserLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                btnLogIn.setText(R.string.logOut);
                btnLogIn.setOnClickListener(v -> onConfirmClick());
            } else {
                displayGuestInfo(username, userEmail, userPic, btnLogIn);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCurrentUser().observe(this, this::displayUserInfo);
    }

    private void displayUserInfo(User currentUser) {
        if (currentUser != null) {
            TextView username = findViewById(R.id.username);
            TextView userEmail = findViewById(R.id.user_email);
            ShapeableImageView userPic = findViewById(R.id.user_pic);

            username.setText(currentUser.getUsername());
            userEmail.setText(currentUser.getEmail());
            userPic.setImageBitmap(viewModel.getBitmap());
        }
    }

    private void displayGuestInfo(TextView username, TextView userEmail, ShapeableImageView userPic, Button btnLogIn) {
        username.setText(getString(R.string.guest));
        userEmail.setText(R.string.guest);
        userPic.setImageResource(R.drawable.ic_account);

        btnLogIn.setText(R.string.login);
        btnLogIn.setOnClickListener(v -> goToLogIn());
    }

    private void displaySettings(View v) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.settings_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.about) {
                showAboutDialog();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_about, null);
        builder.setView(dialogLayout);
        builder.show();
    }

    protected void onResume() {
        super.onResume();
        updateBottomNavigationSelection();
    }

    private void setupBottomNavigation() {
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                navigateToHome();
                return true;
            } else
                if (itemId == R.id.navigation_add_video) {
                navigateToAddVideo();
                return true;
            } else return itemId == R.id.navigation_profile;
        });

        updateBottomNavigationSelection();
    }

    private void updateBottomNavigationSelection() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.navigation_profile);
        }
    }

    private void navigateToHome() {
        finish();
        Intent intent = new Intent(ProfilePage.this, MainActivity.class);
        startActivity(intent);
    }

    private void navigateToAddVideo() {
        finish();
        viewModel.isUserLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                Intent intent = new Intent(ProfilePage.this, AddVideoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ProfilePage.this, "Please log in to add a video", Toast.LENGTH_SHORT).show();
                goToLogIn();
            }
        });
    }

    public void onConfirmClick() {
        viewModel.logOut();
        Toast.makeText(this, "You logged out", Toast.LENGTH_SHORT).show();
    }

    private void goToLogIn() {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }
}