// ── Applicant.java ────────────────────────────────────────────────
package com.akilicredit.app.models;

public class Applicant {
    private String applicantId, fullName, phoneNumber, idNumber, location, dateOfBirth;
    private String occupation, incomeRange, primaryPaymentMethod;
    private String mpesaFrequency, billHabits, savingsHabit, loanStatus;
    private String profileColor; // Hex color string
    private String repaymentModel;
    private String airtimePurchaseDay; // e.g., "Tuesday"
    private int billPaymentDate; // e.g., 5
    private double monthsActive, txnPerMonth, avgInflowKsh;
    private double inflowConsistency, savingsRatio, billPaymentRate;
    private double chamaMonths, airtimeRegularity;
    private int creditScore, estimatedLimitKsh;
    private double previousTxnFrequency, previousInflowConsistency;
    private boolean hasStoppedPayingBills;
    private String band, decision;

    public Applicant() {}

    // Getters and Setters
    public String getProfileColor() { return profileColor; }
    public void setProfileColor(String profileColor) { this.profileColor = profileColor; }
    public String getRepaymentModel() { return repaymentModel; }
    public void setRepaymentModel(String repaymentModel) { this.repaymentModel = repaymentModel; }

    public String getAirtimePurchaseDay() { return airtimePurchaseDay; }
    public void setAirtimePurchaseDay(String airtimePurchaseDay) { this.airtimePurchaseDay = airtimePurchaseDay; }

    public int getBillPaymentDate() { return billPaymentDate; }
    public void setBillPaymentDate(int billPaymentDate) { this.billPaymentDate = billPaymentDate; }

    // Getters and Setters for new fields
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getIncomeRange() { return incomeRange; }
    public void setIncomeRange(String incomeRange) { this.incomeRange = incomeRange; }
    public String getPrimaryPaymentMethod() { return primaryPaymentMethod; }
    public void setPrimaryPaymentMethod(String primaryPaymentMethod) { this.primaryPaymentMethod = primaryPaymentMethod; }
    public String getMpesaFrequency() { return mpesaFrequency; }
    public void setMpesaFrequency(String mpesaFrequency) { this.mpesaFrequency = mpesaFrequency; }
    public String getBillHabits() { return billHabits; }
    public void setBillHabits(String billHabits) { this.billHabits = billHabits; }
    public String getSavingsHabit() { return savingsHabit; }
    public void setSavingsHabit(String savingsHabit) { this.savingsHabit = savingsHabit; }
    public String getLoanStatus() { return loanStatus; }
    public void setLoanStatus(String loanStatus) { this.loanStatus = loanStatus; }

    public int calculateLocalScore() {
        double fTxn   = Math.min(100, (monthsActive/60)*40 + (txnPerMonth/120)*60);
        double fSave  = Math.min(100, (savingsRatio/30)*100);
        double fChama = Math.min(100, (chamaMonths/36)*100);
        double raw = fTxn*0.25 + inflowConsistency*0.25 + fSave*0.15
                + billPaymentRate*0.20 + fChama*0.10 + airtimeRegularity*0.05;
        return (int) Math.max(300, Math.min(850, Math.round(300 + (raw/100)*550)));
    }

    public String getBandForScore(int s) {
        if (s>=750) return "Excellent";
        if (s>=650) return "Good";
        if (s>=500) return "Fair";
        return "Poor";
    }

    public String getDecisionForScore(int s) {
        if (s>=650) return "APPROVED";
        if (s>=500) return "CONDITIONAL";
        return "DECLINE";
    }

    public String generateBehavioralRepaymentModel() {
        if (airtimePurchaseDay != null && billPaymentDate > 0) {
            return "Micro-deductions every " + airtimePurchaseDay + " + Lump sum on " + billPaymentDate + "th";
        } else if (airtimePurchaseDay != null) {
            return "Weekly micro-deductions on " + airtimePurchaseDay + "s";
        } else if (billPaymentDate > 0) {
            return "Monthly repayment on the " + billPaymentDate + "th";
        }
        return "Standard Monthly Repayment";
    }

    public int getEstimatedLimit() {
        // Capped at 3x monthly inflow scaled by score
        double scaledLimit = avgInflowKsh * (creditScore / 850.0) * 3;
        // Never lend more than 2x what someone moves through their phone (demonstrable monthly inflow)
        double absoluteCap = avgInflowKsh * 2;
        
        return (int) Math.min(scaledLimit, absoluteCap);
    }

    // Getters and setters
    public String getApplicantId()  { return applicantId; }
    public void setApplicantId(String v) { applicantId = v; }
    public String getFullName()     { return fullName; }
    public void setFullName(String v)    { fullName = v; }
    public String getPhoneNumber()  { return phoneNumber; }
    public void setPhoneNumber(String v) { phoneNumber = v; }
    public String getOccupation()   { return occupation; }
    public void setOccupation(String v)  { occupation = v; }
    public double getMonthsActive() { return monthsActive; }
    public void setMonthsActive(double v) { monthsActive = v; }
    public double getTxnPerMonth()  { return txnPerMonth; }
    public void setTxnPerMonth(double v)  { txnPerMonth = v; }
    public double getAvgInflowKsh() { return avgInflowKsh; }
    public void setAvgInflowKsh(double v) { avgInflowKsh = v; }
    public double getInflowConsistency() { return inflowConsistency; }
    public void setInflowConsistency(double v) { inflowConsistency = v; }
    public double getSavingsRatio() { return savingsRatio; }
    public void setSavingsRatio(double v) { savingsRatio = v; }
    public double getBillPaymentRate() { return billPaymentRate; }
    public void setBillPaymentRate(double v) { billPaymentRate = v; }
    public double getChamaMonths()  { return chamaMonths; }
    public void setChamaMonths(double v)  { chamaMonths = v; }
    public double getAirtimeRegularity() { return airtimeRegularity; }
    public void setAirtimeRegularity(double v) { airtimeRegularity = v; }
    public int getCreditScore()     { return creditScore; }
    public void setCreditScore(int v)    { creditScore = v; }
    public int getEstimatedLimitKsh()    { return estimatedLimitKsh; }
    public void setEstimatedLimitKsh(int v) { estimatedLimitKsh = v; }

    // Early Warning Score (EWS) Helpers
    public double getPreviousTxnFrequency() { return previousTxnFrequency; }
    public void setPreviousTxnFrequency(double v) { previousTxnFrequency = v; }

    public double getPreviousInflowConsistency() { return previousInflowConsistency; }
    public void setPreviousInflowConsistency(double v) { previousInflowConsistency = v; }

    public boolean isHasStoppedPayingBills() { return hasStoppedPayingBills; }
    public void setHasStoppedPayingBills(boolean v) { hasStoppedPayingBills = v; }

    public boolean hasSignificantBehavioralDrift() {
        if (previousTxnFrequency > 0) {
            double drop = (previousTxnFrequency - txnPerMonth) / previousTxnFrequency;
            if (drop >= 0.40) return true; // 40% drop in frequency
        }
        if (previousInflowConsistency > 0 && (previousInflowConsistency - inflowConsistency) > 20) {
            return true; // Significant drop in consistency
        }
        return hasStoppedPayingBills;
    }

    public String getBand()         { return band; }
    public void setBand(String v)   { band = v; }
    public String getDecision()     { return decision; }
    public void setDecision(String v)    { decision = v; }
}
