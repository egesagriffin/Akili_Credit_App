package com.akilicredit.app.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.core.content.ContextCompat;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.viewmodels.OnboardingViewModel;

public class VerificationFragment extends Fragment {

    private OnboardingViewModel viewModel;
    private Button btnUpload, btnManual;
    private ProgressBar progressBar;
    private TextView tvStatus;

    private static final int PICK_PDF_REQUEST = 1001;
    private static final int SMS_PERMISSION_CODE = 2002;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_verification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        btnUpload = view.findViewById(R.id.btn_upload);
        btnManual = view.findViewById(R.id.btn_manual_verify);
        progressBar = view.findViewById(R.id.progress_verify);
        tvStatus = view.findViewById(R.id.tv_status);

        Applicant applicant = viewModel.getApplicant().getValue();
        if (applicant != null && "Student".equals(applicant.getOccupation())) {
            TextView tvSubtitle = view.findViewById(R.id.tv_verify_subtitle);
            if (tvSubtitle != null) {
                tvSubtitle.setText("Students: Please upload at least 2 months of M-Pesa history.");
            }
        }

        btnUpload.setOnClickListener(v -> openFilePicker());
        btnManual.setOnClickListener(v -> checkSmsPermission());

        viewModel.getIsVerified().observe(getViewLifecycleOwner(), verified -> {
            if (verified) {
                showVerifiedState();
            } else {
                showPendingState();
            }
        });
    }

    private void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            simulateLiteVerification();
        } else {
            requestPermissions(new String[]{android.Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                simulateLiteVerification();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Manual verification unavailable.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select M-Pesa Statement"), PICK_PDF_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            simulateVerification();
        }
    }

    private void simulateVerification() {
        setLoading(true);
        tvStatus.setText(R.string.verifying);

        Applicant applicant = viewModel.getApplicant().getValue();
        boolean isStudent = applicant != null && "Student".equals(applicant.getOccupation());
        
        // Multi-stage simulation: 1. Verify Signature, 2. Check Date Range
        Handler handler = new Handler(Looper.getMainLooper());
        
        handler.postDelayed(() -> {
            if (!isAdded()) return;
            tvStatus.setText(R.string.checking_duration);
            
            handler.postDelayed(() -> {
                if (!isAdded()) return;
                setLoading(false);
                
                if (isStudent) {
                    // Simulation: check for 60 days for students
                    Toast.makeText(getContext(), "Student Validation: 60 days of history found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Statement Validated: 30+ days of history found", Toast.LENGTH_SHORT).show();
                }
                
                viewModel.setIsVerified(true);
            }, 1500);
            
        }, 1500);
    }

    private void simulateLiteVerification() {
        setLoading(true);
        tvStatus.setText("Scanning recent SMS...");
        
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;
            setLoading(false);
            viewModel.setIsVerified(true);
            // Lite verification flag - in a real app, we'd store the verification level
            Toast.makeText(getContext(), "Quick Scan Complete: 7 days of activity found", Toast.LENGTH_SHORT).show();
            showLiteVerifiedState();
        }, 2000);
    }

    private void showLiteVerifiedState() {
        if (!isAdded()) return;
        tvStatus.setText("✓ Lite Verified (Limited History)");
        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_primary));
        btnManual.setText("Scan Complete ✓");
        btnManual.setEnabled(false);
        btnUpload.setEnabled(false);
    }

    private void showVerifiedState() {
        if (!isAdded()) return;
        tvStatus.setText(R.string.status_verified);
        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_mid));
        btnUpload.setText("Statement Uploaded ✓");
        btnUpload.setEnabled(false);
        btnManual.setEnabled(false);
    }

    private void showPendingState() {
        if (!isAdded()) return;
        tvStatus.setText(R.string.status_pending);
        tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_dark));
        btnUpload.setEnabled(true);
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnUpload.setEnabled(!loading);
    }
}