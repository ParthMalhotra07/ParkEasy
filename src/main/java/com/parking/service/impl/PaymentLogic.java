package com.parking.service.impl;

import com.parking.service.PaymentInterface;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class PaymentLogic implements PaymentInterface {
    private static final double BASE_FEE = 40.0;
    private static final double EXTRA_RATE = 5.0; // Per 10 mins

    @Override
    public double calculateFee(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0.0;
        long mins = Duration.between(start, end).toMinutes();
        if (mins <= 60) return BASE_FEE;
        
        long extraMins = mins - 60;
        long blocks = (long) Math.ceil(extraMins / 10.0);
        return BASE_FEE + (blocks * EXTRA_RATE);
    }

    @Override
    public String generateUpiLink(String reservationId, double amount) {
        return String.format("upi://pay?pa=admin@parking&am=%.2f&pn=ParkSys&tr=%s", amount, reservationId);
    }

    @Override
    public boolean processPayment(double amount) { return true; }
}