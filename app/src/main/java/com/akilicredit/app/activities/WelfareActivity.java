package com.akilicredit.app.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.akilicredit.app.R;
import com.google.android.material.button.MaterialButton;

public class WelfareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welfare);

        MaterialButton btnRestructure = findViewById(R.id.btn_restructure);
        MaterialButton btnPause = findViewById(R.id.btn_pause);
        MaterialButton btnWellness = findViewById(R.id.btn_wellness);

        btnRestructure.setOnClickListener(v -> {
            Toast.makeText(this, "Repayment successfully restructured.", Toast.LENGTH_LONG).show();
            finish();
        });

        btnPause.setOnClickListener(v -> {
            Toast.makeText(this, "Repayment paused for 30 days. Stay safe.", Toast.LENGTH_LONG).show();
            finish();
        });

        btnWellness.setOnClickListener(v -> {
            Toast.makeText(this, "Connecting you to a financial coach...", Toast.LENGTH_LONG).show();
            // In a real app, this would open a chat or link to resources
        });
    }
}
