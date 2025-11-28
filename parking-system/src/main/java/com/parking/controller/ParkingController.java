package com.parking.controller;

import com.parking.model.Building;
import com.parking.model.Reservation;
import com.parking.service.AdminService;
import com.parking.service.PaymentInterface;
import com.parking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParkingController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentInterface paymentLogic;

    @PostMapping("/admin/create-building")
    public String createBuilding(@RequestBody Building building) {
        adminService.addBuilding(building);
        return "Building created successfully: " + building.getName();
    }

    @GetMapping("/user/buildings")
    public List<Building> getAllBuildings() {
        return adminService.getBuildings();
    }

    @PostMapping("/user/reserve")
    public Reservation reserveSlot(@RequestBody Reservation reservation) {
        return userService.reserveSlot(reservation);
    }

    @PostMapping("/user/checkout")
    public Map<String, Object> checkout(@RequestParam String reservationId) {
        Reservation res = userService.checkout(reservationId);

        String upiLink = paymentLogic.generateUpiLink(res.getReservationId(), res.getBillAmount());

        return Map.of(
                "reservation", res,
                "upiLink", upiLink,
                "message", "Checkout Successful. Scan QR to Pay."
        );
    }
}