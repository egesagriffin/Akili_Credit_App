package com.akilicredit.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.viewmodels.OnboardingViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class FinancialHabitsFragment extends Fragment {

    private OnboardingViewModel viewModel;
    private ChipGroup groupMpesa;
    private RadioGroup groupBills, groupSavings, groupLoans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_habits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        groupMpesa = view.findViewById(R.id.group_mpesa);
        groupBills = view.findViewById(R.id.group_bills);
        groupSavings = view.findViewById(R.id.group_savings);
        groupLoans = view.findViewById(R.id.group_loans);

        restoreData();
    }

    private void restoreData() {
        Applicant a = viewModel.getApplicant().getValue();
        if (a == null) return;

        // Restore M-Pesa frequency
        if (a.getMpesaFrequency() != null) {
            for (int i = 0; i < groupMpesa.getChildCount(); i++) {
                Chip chip = (Chip) groupMpesa.getChildAt(i);
                if (chip.getText().toString().equals(a.getMpesaFrequency())) {
                    chip.setChecked(true);
                    break;
                }
            }
        }

        // Restore RadioGroups
        restoreRadioGroup(groupBills, a.getBillHabits());
        restoreRadioGroup(groupSavings, a.getSavingsHabit());
        restoreRadioGroup(groupLoans, a.getLoanStatus());
    }

    private void restoreRadioGroup(RadioGroup group, String value) {
        if (value == null) return;
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton rb = (RadioButton) group.getChildAt(i);
            if (rb.getText().toString().equals(value)) {
                rb.setChecked(true);
                break;
            }
        }
    }

    private void saveData() {
        Applicant a = viewModel.getApplicant().getValue();
        if (a == null) return;

        // Save M-Pesa frequency
        int checkedChipId = groupMpesa.getCheckedChipId();
        if (checkedChipId != View.NO_ID) {
            Chip chip = groupMpesa.findViewById(checkedChipId);
            a.setMpesaFrequency(chip.getText().toString());
        }

        // Save RadioGroups
        a.setBillHabits(getRadioValue(groupBills));
        a.setSavingsHabit(getRadioValue(groupSavings));
        a.setLoanStatus(getRadioValue(groupLoans));

        viewModel.updateApplicant(a);
    }

    private String getRadioValue(RadioGroup group) {
        int id = group.getCheckedRadioButtonId();
        if (id != -1) {
            RadioButton rb = group.findViewById(id);
            return rb.getText().toString();
        }
        return null;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }
}