package kz.aitu.sdp.finalproject.facade.subsystems;

import kz.aitu.sdp.finalproject.model.PaymentDetails;

import java.util.Objects;

/**
 * Complex subsystem 3: Interacts with external banking gateways for card authorizations.
 */
public class PaymentSubsystem {

    public boolean processPayment(PaymentDetails payment, double amount) {
        Objects.requireNonNull(payment, "Payment details required");
        if (amount < 0) {
            return false;
        }
        // Simulated validation: card number must have at least 12 digits and CVV 3-4 digits
        if (payment.cardNumber().replaceAll("\\s+", "").length() < 12 || payment.cvv().length() < 3) {
            System.err.println("  [PaymentSubsystem] Payment declined: Invalid card credentials.");
            return false;
        }
        System.out.printf("  [PaymentSubsystem] Charged $%.2f to card %s (%s)\n",
                amount, payment.getMaskedCardNumber(), payment.cardHolderName());
        return true;
    }

    public boolean refundPayment(PaymentDetails payment, double amount) {
        System.out.printf("  [PaymentSubsystem] Refunded $%.2f to card %s\n",
                amount, payment.getMaskedCardNumber());
        return true;
    }
}
