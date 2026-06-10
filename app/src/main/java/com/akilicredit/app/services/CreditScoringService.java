package com.akilicredit.app.services;

import android.os.Handler;
import android.os.Looper;
import com.akilicredit.app.models.Applicant;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

/**
 * CreditScoringService.java
 * ─────────────────────────────────────────────────────────────────
 * Sends applicant data to Python Flask backend for ML scoring.
 * Falls back gracefully to local calculation if backend is offline.
 *
 * SETUP: Replace BASE_URL with your laptop's local IP address.
 *   - On emulator: http://10.0.2.2:5000
 *   - On real device (same WiFi): http://192.168.x.x:5000
 *     (find your IP with: ipconfig on Windows, ifconfig on Mac/Linux)
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class CreditScoringService {

    // ── Change this to your Flask server IP ───────────────────────
    private static final String BASE_URL = "http://10.0.2.2:5000";

    private static final MediaType JSON = MediaType.get("application/json");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(8, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    private static final ExecutorService executor    = Executors.newSingleThreadExecutor();
    private static final Handler         mainHandler = new Handler(Looper.getMainLooper());

    public interface ScoringCallback {
        void onSuccess(int score, String band, String decision,
                       int limitKsh, double rate, String source);
        void onError(String message);
    }

    // ── Score applicant via API ───────────────────────────────────
    public static void scoreApplicant(Applicant a, ScoringCallback callback) {
        executor.execute(() -> {
            try {
                JSONObject payload = new JSONObject();
                payload.put("applicant_id",       a.getApplicantId());
                payload.put("months_active",      a.getMonthsActive());
                payload.put("txn_per_month",      a.getTxnPerMonth());
                payload.put("avg_inflow_ksh",     a.getAvgInflowKsh());
                payload.put("inflow_consistency", a.getInflowConsistency());
                payload.put("savings_ratio",      a.getSavingsRatio());
                payload.put("bill_payment_rate",  a.getBillPaymentRate());
                payload.put("chama_months",       a.getChamaMonths());
                payload.put("airtime_regularity", a.getAirtimeRegularity());

                Request request = new Request.Builder()
                        .url(BASE_URL + "/score")
                        .post(RequestBody.create(payload.toString(), JSON))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject result = new JSONObject(response.body().string());
                        int    score    = result.getInt("credit_score");
                        String band     = result.getString("band");
                        String decision = result.getString("decision");
                        int    limit    = result.getInt("estimated_limit_ksh");
                        double rate     = result.optDouble("recommended_rate_pct", 0);

                        mainHandler.post(() ->
                                callback.onSuccess(score, band, decision, limit, rate, "API"));
                    } else {
                        fallbackLocal(a, callback);
                    }
                }
            } catch (IOException e) {
                // Backend unreachable — compute locally
                fallbackLocal(a, callback);
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // ── Local fallback ────────────────────────────────────────────
    private static void fallbackLocal(Applicant a, ScoringCallback callback) {
        int    score    = a.calculateLocalScore();
        String band     = a.getBandForScore(score);
        String decision = a.getDecisionForScore(score);
        
        // Capped at 3x monthly inflow scaled by score
        double scaledLimit = a.getAvgInflowKsh() * (score / 850.0) * 3;
        // Never lend more than 2x what someone moves through their phone (demonstrable monthly inflow)
        double absoluteCap = a.getAvgInflowKsh() * 2;
        
        int limit = (int) Math.min(scaledLimit, absoluteCap);

        double rate     = score >= 750 ? 12 : score >= 650 ? 18 : score >= 500 ? 24 : 0;

        mainHandler.post(() ->
                callback.onSuccess(score, band, decision, limit, rate, "LOCAL"));
    }
}
