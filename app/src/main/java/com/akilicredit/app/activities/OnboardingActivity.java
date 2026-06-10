package com.akilicredit.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.akilicredit.app.R;
import com.akilicredit.app.fragments.BasicInfoFragment;
import com.akilicredit.app.fragments.FinancialHabitsFragment;
import com.akilicredit.app.fragments.VerificationFragment;
import com.akilicredit.app.fragments.WorkIncomeFragment;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.services.UserSessionManager;
import com.akilicredit.app.viewmodels.OnboardingViewModel;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingViewModel viewModel;
    private UserSessionManager sessionManager;
    private LinearProgressIndicator progressIndicator;
    private TextView tvStepIndicator;
    private Button btnNext, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);
        sessionManager = new UserSessionManager(this);

        initViews();
        observeViewModel();
    }

    private void initViews() {
        progressIndicator = findViewById(R.id.progress_indicator);
        tvStepIndicator = findViewById(R.id.tv_step_indicator);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);

        btnNext.setOnClickListener(v -> {
            if (viewModel.getCurrentStep().getValue() == 4) {
                finishOnboarding();
            } else {
                viewModel.nextStep();
            }
        });

        btnBack.setOnClickListener(v -> viewModel.previousStep());
    }

    private void observeViewModel() {
        viewModel.getCurrentStep().observe(this, step -> {
            updateStep(step);
        });
    }

    private void updateStep(int step) {
        Fragment fragment;
        switch (step) {
            case 1:
                fragment = new BasicInfoFragment();
                btnBack.setVisibility(View.GONE);
                btnNext.setText(R.string.next);
                progressIndicator.setProgress(25);
                break;
            case 2:
                fragment = new WorkIncomeFragment();
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText(R.string.next);
                progressIndicator.setProgress(50);
                break;
            case 3:
                fragment = new FinancialHabitsFragment();
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText(R.string.next);
                progressIndicator.setProgress(75);
                break;
            case 4:
                fragment = new VerificationFragment();
                btnBack.setVisibility(View.VISIBLE);
                btnNext.setText(R.string.finish);
                progressIndicator.setProgress(100);
                break;
            default:
                return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        tvStepIndicator.setText(getString(R.string.step_format, step, 4));
    }

    private void finishOnboarding() {
        // SAVE DATA TO PERSISTENCE
        Applicant applicant = viewModel.getApplicant().getValue();
        if (applicant == null) return;

        boolean isVerified = Boolean.TRUE.equals(viewModel.getIsVerified().getValue());
        boolean isStudent = "Student".equals(applicant.getOccupation());

        // We'll simulate a score based on some inputs
        int simulatedScore = isVerified ? 720 : 580; 
        String band = isVerified ? "Excellent" : "Growing";
        String decision = isVerified ? "APPROVED" : "CONDITIONAL";

        // BASE LIMITS
        int limit = isVerified ? 15000 : 2500;

        // STUDENT REDUCTION & REPAYMENT MODEL
        if (isStudent) {
            limit = (int) (limit * 0.4); // 60% reduction for students
            applicant.setRepaymentModel("Flexible Student Plan (Weekly Small Bites)");
        } else {
            applicant.setRepaymentModel("Standard Monthly Repayment");
        }

        applicant.setCreditScore(simulatedScore);
        applicant.setBand(band);
        applicant.setDecision(decision);
        applicant.setEstimatedLimitKsh(limit);

        sessionManager.saveApplicant(applicant);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("credit_score",    simulatedScore);
        intent.putExtra("band",            band);
        intent.putExtra("decision",        decision);
        intent.putExtra("limit_ksh",       limit);
        intent.putExtra("repayment_model", applicant.getRepaymentModel());
        startActivity(intent);
        finish();
    }
}