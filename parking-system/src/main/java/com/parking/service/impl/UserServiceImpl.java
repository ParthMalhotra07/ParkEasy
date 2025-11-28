package com.parking.service.impl;

import com.parking.model.Building;
import com.parking.model.Reservation;
import com.parking.service.PaymentInterface;
import com.parking.service.UserService;
import com.parking.util.JsonStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID; // <--- ADDED IMPORT

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private JsonStore jsonStore;

    @Autowired
    private PaymentInterface paymentLogic;

    private static final String BUILDINGS_FILE = "buildings.json";
    private static final String RESERVATIONS_FILE = "reservations.json";

    @Override
    public Reservation reserveSlot(Reservation res) {
        // 1. Load all buildings to find and lock the slot
        List<Building> buildings = jsonStore.read(BUILDINGS_FILE, Building.class);
        boolean slotFound = false;

        // Nested loop to find the specific slot in the specific building
        for (Building b : buildings) {
            if (b.getName().equalsIgnoreCase(res.getBuildingName())) {
                for (Building.Floor floor : b.getFloors()) {
                    for (Building.FloorSlot slot : floor.getSlots()) {
                        if (slot.getId().equals(res.getSlotId())) {
                            if (slot.isOccupied()) {
                                throw new RuntimeException("Slot is already occupied!");
                            }
                            slot.setOccupied(true); // LOCK THE SLOT
                            slotFound = true;
                        }
                    }
                }
            }
        }

        if (!slotFound) {
            throw new RuntimeException("Slot not found: " + res.getSlotId());
        }

        // 2. Save the updated buildings (with the locked slot) back to file
        jsonStore.write(BUILDINGS_FILE, buildings);

        // 3. Save the new reservation
        List<Reservation> reservations = jsonStore.read(RESERVATIONS_FILE, Reservation.class);

        // --- NEW CODE: GENERATE ID ---
        if (res.getReservationId() == null) {
            res.setReservationId(UUID.randomUUID().toString());
        }
        // -----------------------------

        // Ensure start time is set
        if (res.getStartTime() == null) {
            res.setStartTime(LocalDateTime.now());
        }
        res.setStatus("ACTIVE");

        reservations.add(res);
        jsonStore.write(RESERVATIONS_FILE, reservations);

        return res;
    }

    @Override
    public Reservation checkout(String reservationId) {
        // 1. Find the reservation
        List<Reservation> reservations = jsonStore.read(RESERVATIONS_FILE, Reservation.class);
        Reservation targetRes = null;

        for (Reservation r : reservations) {
            if (r.getReservationId().equals(reservationId)) {
                targetRes = r;
                break;
            }
        }

        if (targetRes == null) {
            throw new RuntimeException("Reservation not found: " + reservationId);
        }

        if ("COMPLETED".equals(targetRes.getStatus())) {
            return targetRes; // Already checked out
        }

        // 2. Calculate Bill
        LocalDateTime endTime = LocalDateTime.now();
        targetRes.setEndTime(endTime);

        double amount = paymentLogic.calculateFee(targetRes.getStartTime(), endTime);
        targetRes.setBillAmount(amount);
        targetRes.setStatus("COMPLETED");

        // 3. Unlock the parking slot
        unlockSlot(targetRes.getBuildingName(), targetRes.getSlotId());

        // 4. Save updated reservations
        jsonStore.write(RESERVATIONS_FILE, reservations);

        return targetRes;
    }

    // Helper method to free up a slot in buildings.json
    private void unlockSlot(String buildingName, String slotId) {
        List<Building> buildings = jsonStore.read(BUILDINGS_FILE, Building.class);

        for (Building b : buildings) {
            if (b.getName().equalsIgnoreCase(buildingName)) {
                for (Building.Floor floor : b.getFloors()) {
                    for (Building.FloorSlot slot : floor.getSlots()) {
                        if (slot.getId().equals(slotId)) {
                            slot.setOccupied(false); // UNLOCK
                        }
                    }
                }
            }
        }
        jsonStore.write(BUILDINGS_FILE, buildings);
    }
}