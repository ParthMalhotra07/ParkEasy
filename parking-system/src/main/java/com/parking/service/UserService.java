package com.parking.service;

import com.parking.model.Reservation;

public interface UserService {
    Reservation reserveSlot(Reservation reservation);
    Reservation checkout(String reservationId);
}