package com.parking.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.parking.model.FloorSlot;
import com.parking.model.Reservation;
import com.parking.repository.FloorSlotRepository;
import com.parking.repository.ReservationRepository;
import com.parking.service.PaymentInterface;
import com.parking.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private FloorSlotRepository floorSlotRepository;
    @Autowired private PaymentInterface paymentLogic;

    @Override
    @Transactional
    public Reservation bookSlot(String vehicleNumber, String buildingName, String floorLevel, String slotId) {
        // CLEANUP INPUT: Remove spaces and make uppercase
        String cleanVehicle = vehicleNumber.trim().toUpperCase();

        // Check if vehicle already has an active reservation
        List<Reservation> activeReservations = reservationRepository.findByVehicleNumberIgnoreCaseAndStatus(cleanVehicle, "ACTIVE");
        if (!activeReservations.isEmpty()) {
            throw new RuntimeException("Vehicle " + cleanVehicle + " already has an active booking!");
        }

        // 1. Lock Slot in Database (Pessimistic Write Lock)
        Optional<FloorSlot> slotOpt = floorSlotRepository.findSlotForBooking(buildingName, floorLevel, slotId);
        
        if (slotOpt.isEmpty()) {
            throw new RuntimeException("Slot not found");
        }
        
        FloorSlot slot = slotOpt.get();
        if (slot.isOccupied()) {
            throw new RuntimeException("Slot already occupied! Please select another slot.");
        }
        
        slot.setOccupied(true);
        floorSlotRepository.save(slot);

        // 2. Create Reservation
        Reservation res = new Reservation();
        res.setReservationId(UUID.randomUUID().toString());
        res.setVehicleNumber(cleanVehicle);
        res.setBuildingName(buildingName);
        res.setFloorLevel(floorLevel);
        res.setSlotId(slotId);

        // Record the exact start time of the server
        res.setStartTime(LocalDateTime.now());
        res.setStatus("ACTIVE");

        return reservationRepository.save(res);
    }

    @Override
    public Reservation checkout(String vehicleNumber) {
        // CLEANUP INPUT
        String cleanVehicle = vehicleNumber.trim().toUpperCase();

        List<Reservation> activeReservations = reservationRepository.findByVehicleNumberIgnoreCaseAndStatus(cleanVehicle, "ACTIVE");
        
        if (activeReservations.isEmpty()) {
            throw new RuntimeException("No active booking found for vehicle: " + cleanVehicle);
        }
        
        Reservation targetRes = activeReservations.get(0);

        // Calculate Bill
        LocalDateTime endTime = LocalDateTime.now();
        targetRes.setEndTime(endTime);
        targetRes.setBillAmount(paymentLogic.calculateFee(targetRes.getStartTime(), endTime));

        return reservationRepository.save(targetRes);
    }

    @Override
    @Transactional
    public Reservation confirmPayment(String reservationId) {
        Optional<Reservation> resOpt = reservationRepository.findById(reservationId);
        
        if (resOpt.isEmpty()) {
            throw new RuntimeException("Reservation not found");
        }
        
        Reservation targetRes = resOpt.get();
        targetRes.setStatus("PAID");
        
        unlockSlot(targetRes.getBuildingName(), targetRes.getFloorLevel(), targetRes.getSlotId());

        return reservationRepository.save(targetRes);
    }

    private void unlockSlot(String buildingName, String floorLevel, String slotId) {
        // Find slot without pessimistic lock for unlocking
        Optional<FloorSlot> slotOpt = floorSlotRepository.findSlotForBooking(buildingName, floorLevel, slotId);
        if (slotOpt.isPresent()) {
            FloorSlot slot = slotOpt.get();
            slot.setOccupied(false);
            floorSlotRepository.save(slot);
        }
    }
}