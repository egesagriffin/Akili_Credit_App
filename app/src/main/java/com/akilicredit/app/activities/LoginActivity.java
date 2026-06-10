package com.akilicredit.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.services.AuthService;
import com.akilicredit.app.services.UserSessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * LoginActivity.java
 * ─────────────────────────────────────────────────────────────────
 * Handles user authentication with email and password.
 * Validates input, shows loading state, navigates to InputActivity.
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class LoginActivity extends AppCompatActivity {

    private TextInputLayout    tilEmail, tilPassword;
    private TextInputEditText  etEmail, etPassword;
    private Button             btnSignIn, btnCreateAccount;
    private TextView           tvForgotPassword;
    private ProgressBar        progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setClickListeners();
    }

    private void initViews() {
        tilEmail         = findViewById(R.id.til_email);
        tilPassword      = findViewById(R.id.til_password);
        etEmail          = findViewById(R.id.et_email);
        etPassword       = findViewById(R.id.et_password);
        btnSignIn        = findViewById(R.id.btn_sign_in);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar      = findViewById(R.id.progress_bar);
    }

    private void setClickListeners() {

        btnSignIn.setOnClickListener(v -> attemptLogin());

        btnCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Password reset — coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void attemptLogin() {
        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email    = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // Validate
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Enter your email address");
            etEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Enter your password");
            etPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Show loading
        setLoading(true);

        // Authenticate via AuthService
        AuthService.signIn(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                runOnUiThread(() -> {
                    setLoading(false);
                    
                    // On success, we should ensure the session is updated or initialized
                    UserSessionManager sessionManager = new UserSessionManager(LoginActivity.this);
                    Applicant applicant = sessionManager.getApplicant();
                    if (applicant == null) {
                        applicant = new Applicant();
                        applicant.setApplicantId(userId);
                        sessionManager.saveApplicant(applicant);
                    }

                    // Redirect to Onboarding instead of InputActivity for new profile flow
                    Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
                    intent.putExtra("user_id", userId);
                    intent.putExtra("user_email", email);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    setLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignIn.setEnabled(!loading);
        btnCreateAccount.setEnabled(!loading);
        btnSignIn.setText(loading ? "Signing in..." : getString(R.string.sign_in));
    }
}
