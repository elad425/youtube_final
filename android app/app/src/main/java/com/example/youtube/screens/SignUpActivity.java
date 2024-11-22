package com.example.youtube.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.youtube.R;
import com.example.youtube.api.UserApi;
import com.example.youtube.entities.User;
import com.example.youtube.viewmodels.SignUpViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private TextInputLayout usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Uri imageUri;
    private SignUpViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        setupUI();
        observeViewModel();
    }

    private void setupUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));

        usernameEditText = findViewById(R.id.sign_up_username);
        emailEditText = findViewById(R.id.sign_up_email);
        passwordEditText = findViewById(R.id.sign_up_password);
        confirmPasswordEditText = findViewById(R.id.sign_up_confirm_password);
        Button uploadButton = findViewById(R.id.sign_up_upload_button);
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button loginButton = findViewById(R.id.sign_up_login_button);

        uploadButton.setOnClickListener(v -> openFileChooser());
        signUpButton.setOnClickListener(v -> signUp());
        loginButton.setOnClickListener(v -> login());

        clearErrorOnTyping(usernameEditText);
        clearErrorOnTyping(emailEditText);
        clearErrorOnTyping(passwordEditText);
        clearErrorOnTyping(confirmPasswordEditText);
    }

    private void observeViewModel() {
        viewModel.getSignUpSuccessful().observe(this, isSuccessful -> {
            if (isSuccessful) {
                Toast.makeText(this, "Sign-up successful", Toast.LENGTH_SHORT).show();
                login();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void login() {
        resetFields();
        finish();
        Intent intent = new Intent(SignUpActivity.this, LogIn.class);
        startActivity(intent);
    }

    private void resetFields() {
        Objects.requireNonNull(usernameEditText.getEditText()).setText("");
        Objects.requireNonNull(emailEditText.getEditText()).setText("");
        Objects.requireNonNull(passwordEditText.getEditText()).setText("");
        Objects.requireNonNull(confirmPasswordEditText.getEditText()).setText("");
        imageUri = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Profile picture added", Toast.LENGTH_SHORT).show();
        }
    }



    private void signUp() {
        String username = Objects.requireNonNull(usernameEditText.getEditText()).getText().toString().trim();
        String email = Objects.requireNonNull(emailEditText.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getEditText()).getText().toString().trim();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getEditText()).getText().toString().trim();

        if (username.isEmpty()){
            usernameEditText.setError("You need to enter a username");
        }

        if (email.isEmpty()){
            emailEditText.setError("Please enter an email");
        }

        if (password.isEmpty()){
            passwordEditText.setError("Please enter a password");

        }

        if(confirmPassword.isEmpty()){
            confirmPasswordEditText.setError("You need to enter a password confirmation");
        }

        if(username.length()>=15){
            usernameEditText.setError("username too long, must be under 15 letters");
            return;
        }

        if (username.isEmpty()||email.isEmpty()||password.isEmpty()||confirmPassword.isEmpty()){
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        if (password.length() < 8 || !password.matches(".*\\d.*")) {
            passwordEditText.setError("Password must be at least 8 characters long and contain at least one number");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload an image", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.checkEmailExists(email, new UserApi.ApiCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean exists) {
                if (exists) {
                    emailEditText.setError("This email already exists");
                } else {
                    viewModel.uploadImageToServer(imageUri, new UserApi.ApiCallback<String>() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            User newUser = new User(username, email, password, imageUrl);
                            viewModel.signUp(newUser);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(SignUpActivity.this, "Error uploading image: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SignUpActivity.this, "Error checking email: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearErrorOnTyping(TextInputLayout textInputLayout) {
        TextInputEditText editText = (TextInputEditText) textInputLayout.getEditText();
        assert editText != null;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}