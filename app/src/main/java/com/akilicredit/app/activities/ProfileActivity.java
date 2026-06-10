package com.akilicredit.app.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.services.UserSessionManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvId, tvLocation, tvWork, tvIncome, tvScore, tvRepayment, tvLimit;
    private ImageView ivHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        tvName = findViewById(R.id.tv_profile_name);
        tvId = findViewById(R.id.tv_profile_id);
        tvLocation = findViewById(R.id.tv_profile_location);
        tvWork = findViewById(R.id.tv_profile_work);
        tvIncome = findViewById(R.id.tv_profile_income);
        tvScore = findViewById(R.id.tv_profile_score);
        tvRepayment = findViewById(R.id.tv_profile_repayment);
        tvLimit = findViewById(R.id.tv_profile_limit);
        ivHeader = findViewById(R.id.iv_profile_header);

        loadProfile();
    }

    private void loadProfile() {
        UserSessionManager sessionManager = new UserSessionManager(this);
        Applicant applicant = sessionManager.getApplicant();

        if (applicant != null) {
            tvName.setText("Name: " + (applicant.getFullName() != null ? applicant.getFullName() : "---"));
            tvId.setText("ID Number: " + (applicant.getIdNumber() != null ? applicant.getIdNumber() : "---"));
            tvLocation.setText("Location: " + (applicant.getLocation() != null ? applicant.getLocation() : "---"));
            tvWork.setText("Occupation: " + (applicant.getOccupation() != null ? applicant.getOccupation() : "---"));
            tvIncome.setText("Monthly Income: " + (applicant.getIncomeRange() != null ? applicant.getIncomeRange() : "---"));
            
            tvScore.setText(String.valueOf(applicant.getCreditScore()));
            tvRepayment.setText(applicant.getRepaymentModel() != null ? applicant.getRepaymentModel() : "Standard");
            tvLimit.setText("Current Limit: KSh " + java.text.NumberFormat.getInstance().format(applicant.getEstimatedLimitKsh()));
            
            if (applicant.getProfileColor() != null) {
                ivHeader.setBackgroundColor(Color.parseColor(applicant.getProfileColor()));
            }
        }
    }
}
