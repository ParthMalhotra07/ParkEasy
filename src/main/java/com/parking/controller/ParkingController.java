package com.parking.controller;

import com.parking.model.Building;
import com.parking.model.Reservation;
import com.parking.model.User;
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

    @Autowired private AdminService adminService;
    @Autowired private UserService userService;
    @Autowired private PaymentInterface paymentLogic;

    // --- ADMIN ---
    
    @PostMapping("/admin/login")
    public String adminLogin(@RequestBody User user) {
        boolean isValid = adminService.validateAdmin(user.getUsername(), user.getPassword());
        if(isValid) return "SUCCESS";
        throw new RuntimeException("Invalid Admin Credentials");
    }

    @PostMapping("/admin/create-building")
    public String createBuilding(@RequestBody Building building) {
        adminService.addBuilding(building);
        return "Building created successfully";
    }

    // --- USER (CAR) ---

    @GetMapping("/user/buildings")
    public List<Building> getAllBuildings() {
        return adminService.getBuildings();
    }

    @PostMapping("/user/book")
    public Reservation bookSlot(@RequestBody Map<String, String> data) {
        return userService.bookSlot(
            data.get("vehicleNumber"),
            data.get("buildingName"),
            data.get("floorLevel"),
            data.get("slotId")
        );
    }

    @PostMapping("/user/checkout")
    public Map<String, Object> checkout(@RequestBody Map<String, String> data) {
        // 1. Calculate Bill
        Reservation res = userService.checkout(data.get("vehicleNumber"));
        
        // 2. Generate UPI
        String upi = paymentLogic.generateUpiLink(res.getReservationId(), res.getBillAmount());

        return Map.of(
            "reservation", res,
            "upiLink", upi,
            "message", "Bill Generated. Scan to Pay."
        );
    }
    
    @PostMapping("/user/pay")
    public String confirmPayment(@RequestBody Map<String, String> data) {
        userService.confirmPayment(data.get("reservationId"));
        return "Payment Completed Successfully. Safe Travels!";
    }
}