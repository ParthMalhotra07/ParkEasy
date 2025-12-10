package com.parking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.parking.model.Admin;
import com.parking.model.Building;
import com.parking.model.Reservation;
import com.parking.model.User;
import com.parking.service.AdminService;
import com.parking.service.PaymentInterface;
import com.parking.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ParkingController {

    @Autowired private AdminService adminService;
    @Autowired private UserService userService;
    @Autowired private PaymentInterface paymentLogic;

    // --- ADMIN AUTHENTICATION ---
    
    @PostMapping("/admin/register")
    public Map<String, Object> adminRegister(@RequestBody Map<String, String> data) {
        Admin admin = adminService.registerAdmin(
            data.get("username"),
            data.get("password"),
            data.get("email")
        );
        return Map.of(
            "adminId", admin.getAdminId(),
            "username", admin.getUsername(),
            "message", "Registration successful"
        );
    }
    
    @PostMapping("/admin/login")
    public Map<String, Object> adminLogin(@RequestBody User user) {
        Admin admin = adminService.loginAdmin(user.getUsername(), user.getPassword());
        return Map.of(
            "adminId", admin.getAdminId(),
            "username", admin.getUsername(),
            "message", "Login successful"
        );
    }

    // --- ADMIN BUILDING MANAGEMENT ---

    @PostMapping("/admin/create-building")
    public String createBuilding(@RequestBody Map<String, Object> data) {
        // Extract adminId from request
        String adminId = (String) data.get("adminId");
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new RuntimeException("Admin authentication required");
        }
        
        // Build the Building object from request
        Building building = new Building();
        building.setName((String) data.get("name"));
        building.setAddress((String) data.get("address"));
        
        // Parse floors
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> floorsData = (List<Map<String, Object>>) data.get("floors");
        if (floorsData != null) {
            for (Map<String, Object> floorData : floorsData) {
                Building.Floor floor = new Building.Floor();
                floor.setLevel((String) floorData.get("level"));
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> slotsData = (List<Map<String, Object>>) floorData.get("slots");
                if (slotsData != null) {
                    for (Map<String, Object> slotData : slotsData) {
                        Building.FloorSlot slot = new Building.FloorSlot();
                        slot.setId((String) slotData.get("id"));
                        slot.setRow(((Number) slotData.get("row")).intValue());
                        slot.setCol(((Number) slotData.get("col")).intValue());
                        slot.setType((String) slotData.get("type"));
                        slot.setOccupied(Boolean.TRUE.equals(slotData.get("occupied")));
                        floor.getSlots().add(slot);
                    }
                }
                building.getFloors().add(floor);
            }
        }
        
        adminService.addBuilding(building, adminId);
        return "Building created successfully";
    }
    
    @GetMapping("/admin/my-buildings")
    public List<Building> getAdminBuildings(@RequestParam String adminId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new RuntimeException("Admin authentication required");
        }
        return adminService.getBuildingsByAdmin(adminId);
    }
    
    @DeleteMapping("/admin/delete-building")
    public String deleteBuilding(@RequestBody Map<String, String> data) {
        String adminId = data.get("adminId");
        String buildingName = data.get("buildingName");
        
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new RuntimeException("Admin authentication required");
        }
        if (buildingName == null || buildingName.trim().isEmpty()) {
            throw new RuntimeException("Building name required");
        }
        
        adminService.deleteBuilding(buildingName, adminId);
        return "Building deleted successfully";
    }

    // --- USER (CAR) ---

    @GetMapping("/user/buildings")
    public List<Building> getAllBuildings() {
        return adminService.getBuildings();
    }
    
    @GetMapping("/user/buildings/search")
    public List<Building> searchBuildings(@RequestParam(required = false) String query) {
        return adminService.searchBuildings(query);
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