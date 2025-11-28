package com.parking.service.impl;

import com.parking.service.PaymentInterface;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PaymentLogic implements PaymentInterface {

    private static final double BASE_FEE = 40.0;
    private static final double EXTRA_RATE_PER_10_MINS = 5.0;

    private static final String MERCHANT_UPI_ID = "parking.admin@okaxis";
    private static final String MERCHANT_NAME = "Parking_Mgmt_System";

    @Override
    public double calculateFee(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0.0;

        long totalMinutes = Duration.between(start, end).toMinutes();

        if (totalMinutes <= 60) {
            return BASE_FEE;
        }

        long extraMinutes = totalMinutes - 60;
        long extraBlocks = (long) Math.ceil(extraMinutes / 10.0);

        return BASE_FEE + (extraBlocks * EXTRA_RATE_PER_10_MINS);
    }

    @Override
    public String generateUpiLink(String reservationId, double amount) {
        return String.format(
                "upi://pay?pa=%s&pn=%s&mc=1234&tid=%s&tr=%s&am=%.2f&cu=INR",
                MERCHANT_UPI_ID,
                MERCHANT_NAME,
                reservationId,
                reservationId,
                amount
        );
    }

    @Override
    public boolean processPayment(double amount) {
        return amount > 0;
    }
}