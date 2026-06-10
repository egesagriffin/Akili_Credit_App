package com.akilicredit.app.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.akilicredit.app.models.Applicant;

/**
 * Shared ViewModel for onboarding steps.
 * Holds the current Applicant data being built.
 */
public class OnboardingViewModel extends ViewModel {
    private final MutableLiveData<Applicant> applicant = new MutableLiveData<>(new Applicant());
    private final MutableLiveData<Integer> currentStep = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> isVerified = new MutableLiveData<>(false);

    public LiveData<Applicant> getApplicant() {
        return applicant;
    }

    public void updateApplicant(Applicant updatedApplicant) {
        applicant.setValue(updatedApplicant);
    }

    public LiveData<Integer> getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int step) {
        currentStep.setValue(step);
    }

    public LiveData<Boolean> getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean verified) {
        isVerified.setValue(verified);
    }

    public void nextStep() {
        Integer current = currentStep.getValue();
        if (current != null && current < 4) {
            currentStep.setValue(current + 1);
        }
    }

    public void previousStep() {
        Integer current = currentStep.getValue();
        if (current != null && current > 1) {
            currentStep.setValue(current - 1);
        }
    }
}