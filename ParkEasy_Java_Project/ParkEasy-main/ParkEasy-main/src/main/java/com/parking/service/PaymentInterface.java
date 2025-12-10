package com.parking.service;
import java.time.LocalDateTime;

public interface PaymentInterface {
    double calculateFee(LocalDateTime start, LocalDateTime end);
    String generateUpiLink(String reservationId, double amount);
    boolean processPayment(double amount);
}