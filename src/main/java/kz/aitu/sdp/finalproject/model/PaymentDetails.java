package kz.aitu.sdp.finalproject.model;

import java.util.Objects;

/**
 * Immutable value object holding customer payment credentials.
 */
public record PaymentDetails(
        String cardHolderName,
        String cardNumber,
        String cvv,
        String expirationDate
) {
    public PaymentDetails {
        Objects.requireNonNull(cardHolderName, "Card holder name cannot be null");
        Objects.requireNonNull(cardNumber, "Card number cannot be null");
        Objects.requireNonNull(cvv, "CVV cannot be null");
        Objects.requireNonNull(expirationDate, "Expiration date cannot be null");
    }

    public String getMaskedCardNumber() {
        if (cardNumber.length() < 4) return "****";
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
