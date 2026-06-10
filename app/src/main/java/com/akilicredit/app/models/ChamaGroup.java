package com.akilicredit.app.models;

import java.util.ArrayList;
import java.util.List;

/**
 * ChamaGroup.java
 * ─────────────────────────────────────────────────────────────────
 * Represents a group of borrowers from the same Chama.
 * Implements joint score averaging and social accountability logic.
 */
public class ChamaGroup {
    private String chamaId;
    private String chamaName;
    private List<Applicant> members;
    private double groupLimitKsh;
    private double groupScore;
    
    // Collective Financial Journey Metrics
    private double groupMonthlyInflow;
    private double groupInflowConsistency;
    private int groupTxnFrequency;

    public ChamaGroup(String name) {
        this.chamaName = name;
        this.members = new ArrayList<>();
    }

    public void setGroupFinancialMetrics(double inflow, double consistency, int frequency) {
        this.groupMonthlyInflow = inflow;
        this.groupInflowConsistency = consistency;
        this.groupTxnFrequency = frequency;
        calculateGroupMetrics();
    }

    public void addMember(Applicant member) {
        if (members.size() < 5) {
            members.add(member);
        }
    }

    private void calculateGroupMetrics() {
        // Evaluate collective journey as a single entity
        // 850 base logic similar to individual but weighted for group stability
        double fTxn = Math.min(100, (groupTxnFrequency / 50.0) * 100); // Expecting 50+ txns for a group
        double fConsistency = groupInflowConsistency;
        
        double raw = fTxn * 0.4 + fConsistency * 0.6;
        this.groupScore = Math.max(300, Math.min(850, 300 + (raw / 100) * 550));
        
        // Group limit is capped at 2x the collective monthly inflow
        double multiplier = groupScore / 850.0 * 3;
        double scaledLimit = groupMonthlyInflow * multiplier;
        double absoluteCap = groupMonthlyInflow * 2;
        
        this.groupLimitKsh = Math.min(scaledLimit, absoluteCap);
    }

    // Getters
    public String getChamaName() { return chamaName; }
    public List<Applicant> getMembers() { return members; }
    public double getGroupScore() { return groupScore; }
    public double getGroupLimitKsh() { return groupLimitKsh; }
    
    public String getSocialContractSummary() {
        return "Collective Group Score: " + String.format("%.0f", groupScore) + "/850" +
               "\nTotal Group Limit: KSh " + String.format("%.0f", groupLimitKsh) + 
               "\nMembers Enrolled: " + members.size() + "/5" +
               "\n\nAccountability: Decision based on collective 2-month group history. All members share joint liability.";
    }
}
