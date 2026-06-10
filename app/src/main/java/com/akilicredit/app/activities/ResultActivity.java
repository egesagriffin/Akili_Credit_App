package com.akilicredit.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.akilicredit.app.R;
import com.akilicredit.app.services.UserSessionManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

/**
 * ResultActivity.java
 * ─────────────────────────────────────────────────────────────────
 * Displays the final credit score and loan decision.
 * Includes:
 * - Animated score display
 * - Decision (Approved/Decline) with reasoning
 * - Estimated credit limit and interest rate
 * - Personalised improvement tips
 * - Profile updates via Settings
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class ResultActivity extends AppCompatActivity {

    private TextView   tvScore, tvOutOf, tvBand, tvDecision, tvDecisionDetail;
    private TextView   tvCreditLimit, tvAnnualRate, tvRiskTier, tvVerificationBadge, tvRepayment, tvRepaymentDetail;
    private TextView   tvTipsTitle;
    private LinearLayout llTips;
    private CardView   cvResult, cvDecision;
    private Button     btnRecalculate, btnSignOut, btnViewProfile;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        sessionManager = new UserSessionManager(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.result_title));
        }

        initViews();
        loadResult();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_welfare_test) {
            startActivity(new Intent(this, WelfareActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_chama_loan) {
            startActivity(new Intent(this, ChamaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        tvScore             = findViewById(R.id.tv_score);
        tvOutOf             = findViewById(R.id.tv_out_of);
        tvBand              = findViewById(R.id.tv_band);
        tvDecision          = findViewById(R.id.tv_decision);
        tvDecisionDetail    = findViewById(R.id.tv_decision_detail);
        tvCreditLimit       = findViewById(R.id.tv_credit_limit);
        tvAnnualRate        = findViewById(R.id.tv_annual_rate);
        tvRiskTier          = findViewById(R.id.tv_risk_tier);
        tvVerificationBadge = findViewById(R.id.tv_verification_badge);
        tvRepayment         = findViewById(R.id.tv_repayment_model);
        tvRepaymentDetail   = findViewById(R.id.tv_repayment_behavior_detail);
        tvTipsTitle         = findViewById(R.id.tv_tips_title);
        llTips              = findViewById(R.id.ll_tips);
        cvResult            = findViewById(R.id.cv_result);
        cvDecision          = findViewById(R.id.cv_decision);
        btnRecalculate      = findViewById(R.id.btn_recalculate);
        btnSignOut          = findViewById(R.id.btn_sign_out);
        btnViewProfile      = findViewById(R.id.btn_view_profile);

        btnSignOut.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnViewProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        btnRecalculate.setOnClickListener(v -> finish());
    }

    private void loadResult() {
        Intent intent = getIntent();
        int score     = intent.getIntExtra("credit_score", 300);
        String band   = intent.getStringExtra("band");
        String dec    = intent.getStringExtra("decision");
        int limit     = intent.getIntExtra("limit_ksh", 0);
        String repay  = intent.getStringExtra("repayment_model");
        boolean isVer = intent.getBooleanExtra("is_verified", false);

        // UI Updates
        animateScore(score);
        tvBand.setText(band != null ? band.toUpperCase() : "POOR");
        tvDecision.setText(dec != null ? dec : "DECLINE");
        tvCreditLimit.setText(formatKsh(limit));
        tvRepayment.setText(repay != null ? repay : "Standard Monthly");
        if (repay != null && !Objects.equals(repay, "Standard Monthly Repayment")) {
            tvRepaymentDetail.setVisibility(View.VISIBLE);
        }

        if (isVer) {
            tvVerificationBadge.setText("VERIFIED");
            tvVerificationBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.green_mid)));
        } else {
            tvVerificationBadge.setText("UNVERIFIED");
            tvVerificationBadge.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(this, R.color.band_poor)));
        }

        // Color coding by score
        int mainColor = ContextCompat.getColor(this, R.color.green_mid);
        int bgColor   = ContextCompat.getColor(this, R.color.green_light);

        if (score < 500) {
            mainColor = ContextCompat.getColor(this, R.color.band_poor);
            bgColor   = ContextCompat.getColor(this, R.color.band_poor_bg);
        } else if (score < 650) {
            mainColor = ContextCompat.getColor(this, R.color.band_fair);
            bgColor   = ContextCompat.getColor(this, R.color.band_fair_bg);
        }

        tvScore.setTextColor(mainColor);
        tvBand.setTextColor(mainColor);
        cvResult.setCardBackgroundColor(bgColor);

        // Decision detail
        if ("APPROVED".equals(dec)) {
            tvDecisionDetail.setText("Strong profile. Approved with standard terms.");
            tvDecision.setTextColor(ContextCompat.getColor(this, R.color.green_mid));
        } else if ("CONDITIONAL".equals(dec)) {
            tvDecisionDetail.setText("Approved with a lower limit. Build history to increase.");
            tvDecision.setTextColor(ContextCompat.getColor(this, R.color.band_fair));
        } else {
            tvDecisionDetail.setText("Low activity detected. Try verifying your M-Pesa history.");
            tvDecision.setTextColor(ContextCompat.getColor(this, R.color.band_poor));
        }

        // Personalised Tips
        double consistency = intent.getDoubleExtra("consistency", 0);
        double savings     = intent.getDoubleExtra("savings", 0);
        double bills       = intent.getDoubleExtra("bills", 0);
        double chama       = intent.getDoubleExtra("chama", 0);

        buildTips(consistency, savings, bills, chama);
    }

    private void animateScore(int targetScore) {
        android.view.animation.Animation anim = new android.view.animation.Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {
                int current = (int) (300 + (targetScore - 300) * interpolatedTime);
                tvScore.setText(String.valueOf(current));
            }
        };
        anim.setDuration(1200);
        anim.setInterpolator(new DecelerateInterpolator());
        tvScore.startAnimation(anim);
    }

    private void buildTips(double consistency, double savings, double bills, double chama) {
        llTips.removeAllViews();
        boolean hasTips = false;

        if (consistency < 70) {
            addTip("Try to keep a regular monthly balance to improve consistency.");
            hasTips = true;
        }
        if (savings < 15) {
            addTip("Increasing your savings-to-income ratio will boost your score.");
            hasTips = true;
        }
        if (bills < 80) {
            addTip("Pay your utility bills on time to demonstrate reliability.");
            hasTips = true;
        }
        if (chama < 12) {
            addTip("Longer participation in Chama groups builds social credit.");
            hasTips = true;
        }

        if (hasTips) {
            tvTipsTitle.setVisibility(View.VISIBLE);
            llTips.setVisibility(View.VISIBLE);
        }
    }

    private void addTip(String text) {
        TextView tip = new TextView(this);
        tip.setText("• " + text);
        tip.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
        tip.setTextColor(ContextCompat.getColor(this, R.color.gray_700));
        tip.setPadding(0, 4, 0, 4);
        llTips.addView(tip);
    }

    private String formatKsh(int amount) {
        return "KSh " + java.text.NumberFormat.getInstance().format(amount);
    }
}