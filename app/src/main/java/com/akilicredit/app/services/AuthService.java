package com.akilicredit.app.services;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AuthService.java
 * ─────────────────────────────────────────────────────────────────
 * Handles user authentication.
 * Currently uses a development mock — ready for Firebase integration.
 *
 * To add Firebase:
 *   1. Add Firebase to your project via Tools > Firebase in Android Studio
 *   2. Enable Email/Password authentication in Firebase Console
 *   3. Uncomment the Firebase code blocks below
 *   4. Remove the mock implementation
 *
 * Week 2 — Akili Credit | Alternative Credit Scoring System
 */
public class AuthService {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler         mainHandler = new Handler(Looper.getMainLooper());

    public interface AuthCallback {
        void onSuccess(String userId);
        void onError(String errorMessage);
    }

    // ── Sign In ───────────────────────────────────────────────────
    public static void signIn(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // FIREBASE PRODUCTION CODE — uncomment when ready:
                // FirebaseAuth.getInstance()
                //     .signInWithEmailAndPassword(email, password)
                //     .addOnSuccessListener(result ->
                //         callback.onSuccess(result.getUser().getUid()))
                //     .addOnFailureListener(e ->
                //         callback.onError(e.getMessage()));

                // DEVELOPMENT MOCK
                Thread.sleep(800);
                if (email.contains("@") && password.length() >= 6) {
                    String mockId = "AK_USER_" + email.hashCode();
                    mainHandler.post(() -> callback.onSuccess(mockId));
                } else {
                    mainHandler.post(() -> callback.onError("Invalid credentials"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // ── Register ──────────────────────────────────────────────────
    public static void register(String email, String password, String fullName,
                                String phone, String occupation, String idNumber, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // SERVER-SIDE VALIDATION SIMULATION
                if (idNumber == null || idNumber.length() < 7) {
                    mainHandler.post(() -> callback.onError("Security violation: Invalid ID format"));
                    return;
                }

                // DUPLICATE CHECK SIMULATION
                // In a real app, you would query Firestore/DB here:
                // if (database.checkIfIdExists(idNumber)) { ... }
                if (idNumber.equals("12345678")) { // Mock duplicate
                    Thread.sleep(500);
                    mainHandler.post(() -> callback.onError("An account with this ID already exists"));
                    return;
                }
                // FIREBASE PRODUCTION CODE — uncomment when ready:
                // FirebaseAuth.getInstance()
                //     .createUserWithEmailAndPassword(email, password)
                //     .addOnSuccessListener(result -> {
                //         String uid = result.getUser().getUid();
                //         Map<String, Object> userData = new HashMap<>();
                //         userData.put("fullName",    fullName);
                //         userData.put("phone",       phone);
                //         userData.put("occupation",  occupation);
                //         userData.put("email",       email);
                //         userData.put("createdAt",   FieldValue.serverTimestamp());
                //         FirebaseFirestore.getInstance()
                //             .collection("applicants").document(uid)
                //             .set(userData)
                //             .addOnSuccessListener(v -> callback.onSuccess(uid));
                //     })
                //     .addOnFailureListener(e -> callback.onError(e.getMessage()));

                // DEVELOPMENT MOCK
                Thread.sleep(800);
                String mockId = "AK_USER_" + email.hashCode();
                mainHandler.post(() -> callback.onSuccess(mockId));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    // ── Sign Out ──────────────────────────────────────────────────
    public static void signOut() {
        // FirebaseAuth.getInstance().signOut(); // uncomment for Firebase
    }
}
