package kz.aitu.sdp.finalproject.facade.subsystems;

import kz.aitu.sdp.finalproject.model.Traveler;

import java.util.UUID;

/**
 * Complex subsystem 4: Underwrites travel medical, cancellation, and baggage insurance.
 */
public class InsuranceSubsystem {

    public String issuePolicy(Traveler traveler, long tripDurationDays) {
        String policyId = "INS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.printf("  [InsuranceSubsystem] Issued comprehensive coverage #%s for %s (%d days duration)\n",
                policyId, traveler.name(), tripDurationDays);
        return policyId;
    }

    public boolean cancelPolicy(String policyId) {
        System.out.printf("  [InsuranceSubsystem] Cancelled insurance policy %s\n", policyId);
        return true;
    }
}
