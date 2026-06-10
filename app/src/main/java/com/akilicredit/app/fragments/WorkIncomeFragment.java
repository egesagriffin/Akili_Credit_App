package com.akilicredit.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.akilicredit.app.R;
import com.akilicredit.app.models.Applicant;
import com.akilicredit.app.viewmodels.OnboardingViewModel;

public class WorkIncomeFragment extends Fragment {

    private OnboardingViewModel viewModel;
    private AutoCompleteTextView spinnerWork, spinnerIncome, spinnerPayment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step_work_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(OnboardingViewModel.class);

        spinnerWork = view.findViewById(R.id.spinner_work);
        spinnerIncome = view.findViewById(R.id.spinner_income);
        spinnerPayment = view.findViewById(R.id.spinner_payment);

        setupSpinners();
        restoreData();
    }

    private void setupSpinners() {
        String[] workOptions = {"Boda Boda", "Mama Mboga", "Fundi", "Small Business", "Farmer", "Employed", "Student", "Other"};
        String[] incomeOptions = {getString(R.string.income_range_1), getString(R.string.income_range_2), getString(R.string.income_range_3), getString(R.string.income_range_4)};
        String[] paymentOptions = {"M-Pesa", "Bank", "Cash"};

        spinnerWork.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, workOptions));
        spinnerIncome.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, incomeOptions));
        spinnerPayment.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, paymentOptions));
    }

    private void restoreData() {
        Applicant a = viewModel.getApplicant().getValue();
        if (a != null) {
            spinnerWork.setText(a.getOccupation(), false);
            spinnerIncome.setText(a.getIncomeRange(), false);
            spinnerPayment.setText(a.getPrimaryPaymentMethod(), false);
        }
    }

    private void saveData() {
        Applicant a = viewModel.getApplicant().getValue();
        if (a != null) {
            a.setOccupation(spinnerWork.getText().toString());
            a.setIncomeRange(spinnerIncome.getText().toString());
            a.setPrimaryPaymentMethod(spinnerPayment.getText().toString());
            viewModel.updateApplicant(a);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }
}