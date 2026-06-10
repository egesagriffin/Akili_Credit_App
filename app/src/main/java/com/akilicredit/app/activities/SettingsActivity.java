package com.akilicredit.app.activities;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.services.UserSessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity {

    private TextInputEditText etName, etOccupation, etLocation;
    private SwitchCompat switchDarkMode;
    private LinearLayout layoutColorPicker;
    private UserSessionManager sessionManager;
    private Applicant currentApplicant;
    private String selectedColor = "#2E7D32"; // Default green

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new UserSessionManager(this);
        currentApplicant = sessionManager.getApplicant();

        initViews();
        loadData();
        setupColorPicker();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        etName = findViewById(R.id.et_settings_name);
        etOccupation = findViewById(R.id.et_settings_occupation);
        etLocation = findViewById(R.id.et_settings_location);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        layoutColorPicker = findViewById(R.id.layout_color_picker);
        Button btnSave = findViewById(R.id.btn_save_settings);

        btnSave.setOnClickListener(v -> saveChanges());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void setupColorPicker() {
        String[] colors = {"#2E7D32", "#1565C0", "#E65100", "#C62828", "#6A1B9A", "#00838F", "#37474F"};
        for (String colorHex : colors) {
            View colorView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 100);
            params.setMargins(16, 0, 16, 0);
            colorView.setLayoutParams(params);
            
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(Color.parseColor(colorHex));
            colorView.setBackground(shape);
            
            colorView.setOnClickListener(v -> {
                selectedColor = colorHex;
                Toast.makeText(this, "Color selected", Toast.LENGTH_SHORT).show();
                // Optionally highlight the selected one
            });
            layoutColorPicker.addView(colorView);
        }
    }

    private void loadData() {
        if (currentApplicant != null) {
            etName.setText(currentApplicant.getFullName());
            etOccupation.setText(currentApplicant.getOccupation());
            etLocation.setText(currentApplicant.getLocation());
            selectedColor = currentApplicant.getProfileColor() != null ? currentApplicant.getProfileColor() : "#2E7D32";
            
            boolean isNight = (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
            switchDarkMode.setChecked(isNight);
        }
    }

    private void saveChanges() {
        if (currentApplicant == null) {
            currentApplicant = new Applicant();
        }

        currentApplicant.setFullName(etName.getText() != null ? etName.getText().toString() : "");
        currentApplicant.setOccupation(etOccupation.getText() != null ? etOccupation.getText().toString() : "");
        currentApplicant.setLocation(etLocation.getText() != null ? etLocation.getText().toString() : "");
        currentApplicant.setProfileColor(selectedColor);

        sessionManager.saveApplicant(currentApplicant);
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
