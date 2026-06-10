package com.akilicredit.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.services.AuthService;
import com.akilicredit.app.services.UserSessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * RegisterActivity.java
 * ─────────────────────────────────────────────────────────────────
 * New user registration screen.
 * Collects name, M-Pesa phone, occupation, email, and password.
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText   etFullName, etPhone, etEmail, etPassword, etIdNumber;
    private TextInputLayout     tilFullName, tilPhone, tilEmail, tilPassword, tilOccupation, tilIdNumber;
    private AutoCompleteTextView spOccupation;
    private Button              btnCreateAccount;
    private ProgressBar         progressBar;

    private static final String[] OCCUPATIONS = {
            "Informal trader",
            "Bodaboda rider",
            "Market vendor",
            "Artisan / Fundi",
            "Farmer",
            "Hawker",
            "Small business owner",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Toolbar back navigation
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Create Account");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        initViews();
        setupOccupationDropdown();
        setClickListeners();
    }

    private void initViews() {
        tilFullName    = findViewById(R.id.til_full_name);
        tilPhone       = findViewById(R.id.til_phone);
        tilEmail       = findViewById(R.id.til_email);
        tilOccupation  = findViewById(R.id.til_occupation);
        tilPassword    = findViewById(R.id.til_password);
        tilIdNumber    = findViewById(R.id.til_id_number);
        etFullName     = findViewById(R.id.et_full_name);
        etPhone        = findViewById(R.id.et_phone);
        etEmail        = findViewById(R.id.et_email);
        etPassword     = findViewById(R.id.et_password);
        etIdNumber     = findViewById(R.id.et_id_number);
        spOccupation   = findViewById(R.id.sp_occupation);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        progressBar    = findViewById(R.id.progress_bar);
    }

    private void setupOccupationDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                OCCUPATIONS
        );
        spOccupation.setAdapter(adapter);
        spOccupation.setText(OCCUPATIONS[0], false);
    }

    private void setClickListeners() {
        btnCreateAccount.setOnClickListener(v -> attemptRegister());
    }

    private void attemptRegister() {
        // Clear errors
        tilFullName.setError(null);
        tilIdNumber.setError(null);
        tilPhone.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        String fullName    = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String idNumber    = etIdNumber.getText() != null ? etIdNumber.getText().toString().trim() : "";
        String phone       = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email       = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password    = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String occupation  = spOccupation.getText().toString();

        // Validate
        if (TextUtils.isEmpty(fullName)) {
            tilFullName.setError("Enter your full name"); etFullName.requestFocus(); return;
        }
        if (idNumber.length() < 7 || idNumber.length() > 8 || !idNumber.matches("\\d+")) {
            tilIdNumber.setError("Enter a valid 7-8 digit National ID"); etIdNumber.requestFocus(); return;
        }
        if (TextUtils.isEmpty(phone) || (!phone.startsWith("07") && !phone.startsWith("01") && !phone.startsWith("+254"))) {
            tilPhone.setError("Enter a valid Kenyan M-Pesa number"); etPhone.requestFocus(); return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address"); etEmail.requestFocus(); return;
        }
        if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters"); etPassword.requestFocus(); return;
        }

        setLoading(true);

        AuthService.register(email, password, fullName, phone, occupation, idNumber,
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        runOnUiThread(() -> {
                            setLoading(false);
                            Toast.makeText(RegisterActivity.this,
                                    "Account created! Welcome to Akili Credit.", Toast.LENGTH_SHORT).show();
                            
                            // Automatic sign-in: Save session and move to Onboarding
                            Applicant newApplicant = new Applicant();
                            newApplicant.setApplicantId(userId);
                            newApplicant.setFullName(fullName);
                            newApplicant.setPhoneNumber(phone);
                            newApplicant.setOccupation(occupation);
                            newApplicant.setIdNumber(idNumber);
                            
                            new UserSessionManager(RegisterActivity.this).saveApplicant(newApplicant);

                            Intent intent = new Intent(RegisterActivity.this, OnboardingActivity.class);
                            intent.putExtra("user_id", userId);
                            intent.putExtra("user_email", email);
                            startActivity(intent);
                            finishAffinity();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            setLoading(false);
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnCreateAccount.setEnabled(!loading);
        btnCreateAccount.setText(loading ? "Creating account..." : getString(R.string.create_account));
    }
}
