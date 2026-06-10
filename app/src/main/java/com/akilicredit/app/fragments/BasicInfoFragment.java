package com.akilicredit.app.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.viewmodels.OnboardingViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class BasicInfoFragment extends Fragment {

    private OnboardingViewModel viewModel;
    private TextInputEditText etFullName, etIdNumber, etLocation, etDob;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_basic_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        etFullName = view.findViewById(R.id.et_full_name);
        etIdNumber = view.findViewById(R.id.et_id_number);
        etLocation = view.findViewById(R.id.et_location);
        etDob = view.findViewById(R.id.et_dob);

        setupDatePicker();
        restoreData();
    }

    private void setupDatePicker() {
        etDob.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                etDob.setText(date);
                saveData();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void restoreData() {
        Applicant a = viewModel.getApplicant().getValue();
        if (a != null) {
            etFullName.setText(a.getFullName());
            etIdNumber.setText(a.getIdNumber());
            etLocation.setText(a.getLocation());
            etDob.setText(a.getDateOfBirth());
        }
    }

    private void saveData() {
        Applicant a = viewModel.getApplicant().getValue();
        if (a != null) {
            a.setFullName(etFullName.getText().toString());
            a.setIdNumber(etIdNumber.getText().toString());
            a.setLocation(etLocation.getText().toString());
            a.setDateOfBirth(etDob.getText().toString());
            viewModel.updateApplicant(a);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }
}