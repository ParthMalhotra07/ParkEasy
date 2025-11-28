package com.parking.service;
import com.parking.model.Reservation;

public interface UserService {
    Reservation bookSlot(String vehicleNumber, String buildingName, String floor, String slotId);
    Reservation checkout(String vehicleNumber);
    Reservation confirmPayment(String reservationId);
}