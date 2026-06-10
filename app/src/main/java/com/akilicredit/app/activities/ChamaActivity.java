package com.akilicredit.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.models.ChamaGroup;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class ChamaActivity extends AppCompatActivity {

    private ChamaGroup currentGroup;
    private TextView tvSummary, tvStatementStatus;
    private LinearLayout llMembersList;
    private MaterialButton btnApply, btnUpload;
    private boolean isStatementUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chama);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        currentGroup = new ChamaGroup("Unity Chama");
        tvSummary = findViewById(R.id.tv_chama_summary);
        tvStatementStatus = findViewById(R.id.tv_chama_statement_status);
        llMembersList = findViewById(R.id.ll_members_list);
        btnApply = findViewById(R.id.btn_apply_group);
        btnUpload = findViewById(R.id.btn_upload_chama_statement);

        findViewById(R.id.btn_add_member).setOnClickListener(v -> showAddMemberDialog());
        btnUpload.setOnClickListener(v -> simulateStatementUpload());
        
        btnApply.setOnClickListener(v -> {
            Toast.makeText(this, "Group loan application submitted! Joint accountability active.", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void showAddMemberDialog() {
        if (currentGroup.getMembers().size() >= 5) {
            Toast.makeText(this, "Group is full (Max 5 members)", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_member, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_member_name);
        TextInputEditText etPhone = dialogView.findViewById(R.id.et_member_phone);
        TextInputEditText etInflow = dialogView.findViewById(R.id.et_member_inflow);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    if (etName.getText() == null || etPhone.getText() == null || etInflow.getText() == null) return;
                    
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String inflowStr = etInflow.getText().toString().trim();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(inflowStr)) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double inflow = Double.parseDouble(inflowStr);
                        
                        Applicant member = new Applicant();
                        member.setFullName(name);
                        member.setPhoneNumber(phone);
                        // Collective journey evaluates the group, but we store individual basic info
                        member.setAvgInflowKsh(inflow); 

                        currentGroup.addMember(member);
                        updateUI();
                        Toast.makeText(this, name + " added to group.", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid inflow amount", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void simulateStatementUpload() {
        Toast.makeText(this, "Analyzing collective group journey (2+ months)...", Toast.LENGTH_SHORT).show();
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            isStatementUploaded = true;
            
            // Simulating extraction of COLLECTIVE metrics from a group statement
            Random r = new Random();
            double groupInflow = 50000 + r.nextInt(150000);
            double groupConsistency = 70 + r.nextInt(30);
            int groupFrequency = 40 + r.nextInt(60);
            
            currentGroup.setGroupFinancialMetrics(groupInflow, groupConsistency, groupFrequency);
            
            tvStatementStatus.setText(R.string.status_collective_verified);
            tvStatementStatus.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.green_mid));
            updateUI();
        }, 2000);
    }

    private void updateUI() {
        tvSummary.setText(currentGroup.getSocialContractSummary());
        
        // Update members list
        llMembersList.removeAllViews();
        for (Applicant a : currentGroup.getMembers()) {
            TextView tv = new TextView(this);
            tv.setText(String.format("• %s (%s)", a.getFullName(), a.getPhoneNumber()));
            tv.setPadding(0, 8, 0, 8);
            llMembersList.addView(tv);
        }

        btnApply.setEnabled(currentGroup.getMembers().size() >= 3 && isStatementUploaded);
    }
}
