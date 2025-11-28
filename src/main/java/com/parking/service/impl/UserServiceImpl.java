package com.parking.service.impl;

import com.parking.model.Building;
import com. parking.model. Reservation;
import com.parking.service.PaymentInterface;
import com. parking.service.UserService;
import com.parking.util.JsonStore;
import org.springframework.beans.factory.annotation. Autowired;
import org.springframework. stereotype.Service;

import java.time. LocalDateTime;
import java.util.List;
import java. util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired private JsonStore jsonStore;
    @Autowired private PaymentInterface paymentLogic;

    private static final String BUILDINGS_FILE = "buildings.json";
    private static final String RESERVATIONS_FILE = "reservations.json";

    @Override
    public Reservation bookSlot(String vehicleNumber, String buildingName, String floorLevel, String slotId) {
        // CLEANUP INPUT: Remove spaces and make uppercase
        String cleanVehicle = vehicleNumber.trim().toUpperCase();

        // ═══════════════════════════════════════════════════════════════
        // NEW: Check if vehicle already has an active reservation
        // ═══════════════════════════════════════════════════════════════
        List<Reservation> reservations = jsonStore. read(RESERVATIONS_FILE, Reservation.class);

        for (Reservation r : reservations) {
            if (r.getVehicleNumber().equalsIgnoreCase(cleanVehicle) && "ACTIVE".equals(r.getStatus())) {
                throw new RuntimeException("Vehicle " + cleanVehicle + " already has an active booking!");
            }
        }
        // ═══════════════════════════════════════════════════════════════

        // 1. Lock Slot in Building File
        List<Building> buildings = jsonStore.read(BUILDINGS_FILE, Building.class);
        boolean slotFound = false;

        for (Building b : buildings) {
            if (b. getName().equalsIgnoreCase(buildingName)) {
                for (Building.Floor floor : b.getFloors()) {
                    if(floor.getLevel().equalsIgnoreCase(floorLevel)) {
                        for (Building.FloorSlot slot : floor.getSlots()) {
                            if (slot.getId().equals(slotId)) {
                                if (slot.isOccupied()) throw new RuntimeException("Slot Occupied");
                                slot. setOccupied(true);
                                slotFound = true;
                            }
                        }
                    }
                }
            }
        }
        if (!slotFound) throw new RuntimeException("Slot not found");
        jsonStore.write(BUILDINGS_FILE, buildings);

        // 2. Create Reservation
        Reservation res = new Reservation();
        res.setReservationId(UUID.randomUUID().toString());
        res.setVehicleNumber(cleanVehicle);
        res.setBuildingName(buildingName);
        res. setFloorLevel(floorLevel);
        res.setSlotId(slotId);

        // FIXED: Record the exact start time of the server
        res.setStartTime(LocalDateTime.now());

        res.setStatus("ACTIVE");

        reservations. add(res);
        jsonStore.write(RESERVATIONS_FILE, reservations);

        return res;
    }

    @Override
    public Reservation checkout(String vehicleNumber) {
        // CLEANUP INPUT
        String cleanVehicle = vehicleNumber.trim().toUpperCase();

        List<Reservation> reservations = jsonStore.read(RESERVATIONS_FILE, Reservation.class);
        Reservation targetRes = null;

        // Find ACTIVE reservation for this Vehicle
        for (Reservation r : reservations) {
            if (r.getVehicleNumber().equalsIgnoreCase(cleanVehicle) && "ACTIVE".equals(r.getStatus())) {
                targetRes = r;
                break;
            }
        }
        if (targetRes == null) throw new RuntimeException("No active booking found for vehicle: " + cleanVehicle);

        // Calculate Bill
        LocalDateTime endTime = LocalDateTime.now();
        targetRes. setEndTime(endTime);
        targetRes.setBillAmount(paymentLogic.calculateFee(targetRes.getStartTime(), endTime));

        jsonStore.write(RESERVATIONS_FILE, reservations);
        return targetRes;
    }

    @Override
    public Reservation confirmPayment(String reservationId) {
        List<Reservation> reservations = jsonStore.read(RESERVATIONS_FILE, Reservation.class);
        Reservation targetRes = null;

        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                targetRes = r;
                break;
            }
        }
        if(targetRes == null) throw new RuntimeException("Res not found");

        targetRes.setStatus("PAID");
        unlockSlot(targetRes. getBuildingName(), targetRes.getSlotId());

        jsonStore. write(RESERVATIONS_FILE, reservations);
        return targetRes;
    }

    private void unlockSlot(String buildingName, String slotId) {
        List<Building> buildings = jsonStore.read(BUILDINGS_FILE, Building. class);
        for (Building b : buildings) {
            if (b.getName(). equalsIgnoreCase(buildingName)) {
                for (Building.Floor floor : b.getFloors()) {
                    for (Building.FloorSlot slot : floor.getSlots()) {
                        if (slot.getId().equals(slotId)) slot.setOccupied(false);
                    }
                }
            }
        }
        jsonStore. write(BUILDINGS_FILE, buildings);
    }
}