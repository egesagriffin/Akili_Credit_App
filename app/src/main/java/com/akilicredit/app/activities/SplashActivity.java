package com.akilicredit.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;
import androidx.appcompat.app.AppCompatActivity;
import com.akilicredit.app.R;

/**
 * SplashActivity.java
 * ─────────────────────────────────────────────────────────────────
 * App entry point. Shows branding and animations.
 * Navigates to LoginActivity after a short delay.
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Simple fade-in animation for branding
        View appName = findViewById(R.id.tv_app_name);
        View tagline = findViewById(R.id.tv_tagline);
        View subTagline = findViewById(R.id.tv_sub);
        View sdgBadge = findViewById(R.id.tv_sdg_badge);

        animateFadeIn(appName, 500);
        animateFadeIn(tagline, 1000);
        animateFadeIn(subTagline, 1500);
        animateFadeIn(sdgBadge, 2000);

        // Transition to Login after 3 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }, 3000);
    }

    private void animateFadeIn(View view, int delay) {
        if (view == null) return;
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);
        fadeIn.setStartOffset(delay);
        fadeIn.setFillAfter(true);
        view.startAnimation(fadeIn);
    }
}
