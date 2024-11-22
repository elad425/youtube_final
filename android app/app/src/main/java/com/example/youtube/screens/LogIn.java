package com.example.youtube.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.youtube.MainActivity;
import com.example.youtube.R;
import com.example.youtube.viewmodels.LoginViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LogIn extends AppCompatActivity {

    private TextInputLayout emailEditText, passwordEditText;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupUI();
        observeViewModel();
        checkSavedToken();
    }

    private void setupUI() {
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent));

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.login_login_button);
        Button signUpButton = findViewById(R.id.login_to_signup_button);

        setupTextWatchers();
        loginButton.setOnClickListener(v -> login());
        signUpButton.setOnClickListener(v -> signUp());
    }

    private void checkSavedToken() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token != null) {
            Log.d("LoginActivity", "Token saved: " + token);
        } else {
            Log.d("LoginActivity", "No token found");
        }
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearErrors();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        Objects.requireNonNull(emailEditText.getEditText()).addTextChangedListener(textWatcher);
        Objects.requireNonNull(passwordEditText.getEditText()).addTextChangedListener(textWatcher);
    }

    private void clearErrors() {
        emailEditText.setError(null);
        emailEditText.setErrorEnabled(false);
        passwordEditText.setError(null);
        passwordEditText.setErrorEnabled(false);
    }

    private void observeViewModel() {
        viewModel.getLoginSuccessful().observe(this, isSuccessful -> {
            if (isSuccessful) {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                emailEditText.setError("Invalid email or password");
                passwordEditText.setError("Invalid email or password");
            }
        });
    }

    private void signUp() {
        Intent intent = new Intent(LogIn.this, SignUpActivity.class);
        resetFields();
        startActivity(intent);
    }

    private void login() {
        String email = Objects.requireNonNull(emailEditText.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(passwordEditText.getEditText()).getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }
        viewModel.login(email, password);
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Please enter an email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email address");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Please enter a password");
            return false;
        }
        return true;
    }

    private void navigateToMain() {
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        resetFields();
        startActivity(intent);
        finish();
    }

    private void resetFields() {
        Objects.requireNonNull(emailEditText.getEditText()).setText("");
        Objects.requireNonNull(passwordEditText.getEditText()).setText("");
    }
}