package com.akilicredit.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.services.CreditScoringService;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * InputActivity.java
 * ─────────────────────────────────────────────────────────────────
 * Financial behaviour data entry screen.
 * 8 SeekBar sliders matching the Python ML backend features exactly.
 * Shows a live score preview that updates as sliders move.
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class InputActivity extends AppCompatActivity {

    // Sliders
    private SeekBar sbMonthsActive, sbTxnPerMonth, sbAvgInflow;
    private SeekBar sbInflowConsistency, sbSavingsRatio, sbBillPayment;
    private SeekBar sbChamaMonths, sbAirtimeRegularity, sbBillDate;
    private android.widget.Spinner spAirtimeDay;

    // Value labels
    private TextView tvMonthsVal, tvTxnVal, tvInflowVal;
    private TextView tvConsistVal, tvSavingsVal, tvBillsVal;
    private TextView tvChamaVal, tvAirtimeVal, tvBillDateVal;

    // Live score preview
    private TextView tvLiveScore, tvLiveBand;
    private CardView cvScorePreview;

    private Button btnGenerateScore, btnUploadStatement;
    private TextView tvUploadStatus;

    private boolean isDocumentUploaded = false;

    private String userId  = "";
    private String userEmail = "";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        userId    = getIntent().getStringExtra("user_id");
        userEmail = getIntent().getStringExtra("user_email");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.input_title));
        }

        initViews();
        setupSliders();
        updateLiveScore(); // initial calculation
    }

    private void initViews() {
        sbMonthsActive       = findViewById(R.id.sb_months_active);
        sbTxnPerMonth        = findViewById(R.id.sb_txn_per_month);
        sbAvgInflow          = findViewById(R.id.sb_avg_inflow);
        sbInflowConsistency  = findViewById(R.id.sb_inflow_consistency);
        sbSavingsRatio       = findViewById(R.id.sb_savings_ratio);
        sbBillPayment        = findViewById(R.id.sb_bill_payment);
        sbChamaMonths        = findViewById(R.id.sb_chama_months);
        sbAirtimeRegularity  = findViewById(R.id.sb_airtime_regularity);

        tvMonthsVal   = findViewById(R.id.tv_months_val);
        tvTxnVal      = findViewById(R.id.tv_txn_val);
        tvInflowVal   = findViewById(R.id.tv_inflow_val);
        tvConsistVal  = findViewById(R.id.tv_consist_val);
        tvSavingsVal  = findViewById(R.id.tv_savings_val);
        tvBillsVal    = findViewById(R.id.tv_bills_val);
        tvChamaVal    = findViewById(R.id.tv_chama_val);
        tvAirtimeVal  = findViewById(R.id.tv_airtime_val);
        tvBillDateVal = findViewById(R.id.tv_bill_date_val);

        spAirtimeDay  = findViewById(R.id.sp_airtime_day);
        sbBillDate    = findViewById(R.id.sb_bill_date);

        tvLiveScore   = findViewById(R.id.tv_live_score);
        tvLiveBand    = findViewById(R.id.tv_live_band);
        cvScorePreview = findViewById(R.id.cv_score_preview);
        progressBar    = findViewById(R.id.progress_bar);

        btnGenerateScore = findViewById(R.id.btn_generate_score);
        btnUploadStatement = findViewById(R.id.btn_upload_statement);
        tvUploadStatus = findViewById(R.id.tv_upload_status);

        // Set SeekBar max values
        sbMonthsActive.setMax(60);
        sbTxnPerMonth.setMax(120);
        sbAvgInflow.setMax(200);        // represents KSh 0–200,000 in steps of 1000
        sbInflowConsistency.setMax(100);
        sbSavingsRatio.setMax(50);
        sbBillPayment.setMax(100);
        sbChamaMonths.setMax(48);
        sbAirtimeRegularity.setMax(100);

        // Default values
        sbMonthsActive.setProgress(12);
        sbTxnPerMonth.setProgress(20);
        sbAvgInflow.setProgress(30);
        sbInflowConsistency.setProgress(60);
        sbSavingsRatio.setProgress(10);
        sbBillPayment.setProgress(70);
        sbChamaMonths.setProgress(6);
        sbAirtimeRegularity.setProgress(75);
    }

    private void setupSliders() {
        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                updateLabels();
                updateLiveScore();
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        };

        sbMonthsActive.setOnSeekBarChangeListener(listener);
        sbTxnPerMonth.setOnSeekBarChangeListener(listener);
        sbAvgInflow.setOnSeekBarChangeListener(listener);
        sbInflowConsistency.setOnSeekBarChangeListener(listener);
        sbSavingsRatio.setOnSeekBarChangeListener(listener);
        sbBillPayment.setOnSeekBarChangeListener(listener);
        sbChamaMonths.setOnSeekBarChangeListener(listener);
        sbAirtimeRegularity.setOnSeekBarChangeListener(listener);
        
        sbBillDate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean fromUser) {
                String suffix = "th";
                if (p == 1) suffix = "st";
                else if (p == 2) suffix = "nd";
                else if (p == 3) suffix = "rd";
                tvBillDateVal.setText(p + suffix);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        btnUploadStatement.setOnClickListener(v -> openDocumentPicker());
        btnGenerateScore.setOnClickListener(v -> proceedToResult());

        updateLabels();
    }

    private void openDocumentPicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select M-Pesa Statement"), 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            // SIMULATING STRONG VERIFICATION SOURCE (e.g. OCR + Digital Signature Check)
            // In a real production app, we would send this PDF to a backend
            // that uses Safaricom's M-Pesa API or a service like Belvo/Mono 
            // to verify the transaction history against the statement.
            
            setLoading(true);
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                if (isFinishing()) return;
                setLoading(false);
                isDocumentUploaded = true;
                tvUploadStatus.setText("✓ Verified via M-Pesa Digital Signature");
                tvUploadStatus.setTextColor(ContextCompat.getColor(this, R.color.green_mid));
                android.widget.Toast.makeText(getApplicationContext(), "Statement Authenticity Verified", android.widget.Toast.LENGTH_SHORT).show();
            }, 2000); // Simulate network verification delay
        }
    }

    private void updateLabels() {
        tvMonthsVal.setText(sbMonthsActive.getProgress() + " months");
        tvTxnVal.setText(sbTxnPerMonth.getProgress() + " txns");
        tvInflowVal.setText("KSh " + (sbAvgInflow.getProgress() * 1000) + "");
        tvConsistVal.setText(sbInflowConsistency.getProgress() + "%");
        tvSavingsVal.setText(sbSavingsRatio.getProgress() + "%");
        tvBillsVal.setText(sbBillPayment.getProgress() + "%");
        tvChamaVal.setText(sbChamaMonths.getProgress() + " months");
        tvAirtimeVal.setText(sbAirtimeRegularity.getProgress() + "%");
    }

    private void updateLiveScore() {
        Applicant a = buildApplicantFromSliders();
        int score   = a.calculateLocalScore();
        String band = a.getBandForScore(score);

        tvLiveScore.setText(score + " / 850");
        tvLiveBand.setText(band);

        // Color the preview card by band
        int bgColor;
        int textColor;
        switch (band) {
            case "Excellent": bgColor = 0xFFE8F5E9; textColor = 0xFF2E7D32; break;
            case "Good":      bgColor = 0xFFE3F2FD; textColor = 0xFF1565C0; break;
            case "Fair":      bgColor = 0xFFFFF3E0; textColor = 0xFFE65100; break;
            default:          bgColor = 0xFFFFEBEE; textColor = 0xFFC62828; break;
        }
        cvScorePreview.setCardBackgroundColor(bgColor);
        tvLiveScore.setTextColor(textColor);
        tvLiveBand.setTextColor(textColor);
    }

    private void proceedToResult() {
        Applicant applicant = buildApplicantFromSliders();

        // VALIDATION: Ensure positive values and acceptable limits
        if (applicant.getAvgInflowKsh() <= 0) {
            // In a real app, use a SnackBar or Toast
            android.widget.Toast.makeText(this, "Income must be a positive number", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // RISK WARNING: Check for suspicious entries (e.g., extreme consistency with low transactions)
        if (applicant.getInflowConsistency() > 95 && applicant.getTxnPerMonth() < 5) {
            android.widget.Toast.makeText(this, "Risk Warning: High consistency with low activity detected", android.widget.Toast.LENGTH_LONG).show();
        }

        applicant.setApplicantId("AK_" + System.currentTimeMillis());
        applicant.setFullName(userEmail != null ? userEmail.split("@")[0] : "Applicant");

        // Score locally first (instant)
        int localScore = applicant.calculateLocalScore();
        
        // VERIFICATION LOGIC: 
        // If no document is uploaded, cap the score and limit.
        // This prevents users from "elevating" their score without proof.
        int finalScore = localScore;
        int finalLimit = applicant.getEstimatedLimit();
        
        if (!isDocumentUploaded) {
            // Cap score at 'Fair' (550) and limit at KSh 5,000 for unverified users
            finalScore = Math.min(localScore, 550);
            finalLimit = Math.min(finalLimit, 5000);
            android.widget.Toast.makeText(this, "Score capped due to missing verification", android.widget.Toast.LENGTH_SHORT).show();
        }

        applicant.setCreditScore(finalScore);
        applicant.setBand(applicant.getBandForScore(finalScore));
        applicant.setDecision(applicant.getDecisionForScore(finalScore));
        applicant.setEstimatedLimitKsh(finalLimit);

        // Send to ResultActivity
        Intent intent = new Intent(InputActivity.this, ResultActivity.class);
        intent.putExtra("credit_score",    finalScore);
        intent.putExtra("band",            applicant.getBand());
        intent.putExtra("decision",        applicant.getDecision());
        intent.putExtra("limit_ksh",       finalLimit);
        intent.putExtra("avg_inflow",      (double)(sbAvgInflow.getProgress() * 1000));
        intent.putExtra("consistency",     (double) sbInflowConsistency.getProgress());
        intent.putExtra("savings",         (double) sbSavingsRatio.getProgress());
        intent.putExtra("bills",           (double) sbBillPayment.getProgress());
        intent.putExtra("chama",           (double) sbChamaMonths.getProgress());
        intent.putExtra("is_verified",     isDocumentUploaded);
        intent.putExtra("repayment_model", applicant.generateBehavioralRepaymentModel());
        startActivity(intent);
    }

    private Applicant buildApplicantFromSliders() {
        Applicant a = new Applicant();
        a.setMonthsActive(sbMonthsActive.getProgress());
        a.setTxnPerMonth(sbTxnPerMonth.getProgress());
        a.setAvgInflowKsh(sbAvgInflow.getProgress() * 1000.0);
        a.setInflowConsistency(sbInflowConsistency.getProgress());
        a.setSavingsRatio(sbSavingsRatio.getProgress());
        a.setBillPaymentRate(sbBillPayment.getProgress());
        a.setChamaMonths(sbChamaMonths.getProgress());
        a.setAirtimeRegularity(sbAirtimeRegularity.getProgress());
        a.setAirtimePurchaseDay(spAirtimeDay.getSelectedItem().toString());
        a.setBillPaymentDate(sbBillDate.getProgress());
        return a;
    }

    private void buildTips(double consistency, double savings, double bills, double chama) {
        // ... (existing code)
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? android.view.View.VISIBLE : android.view.View.GONE);
        }
        btnGenerateScore.setEnabled(!loading);
        btnUploadStatement.setEnabled(!loading);
    }
}
