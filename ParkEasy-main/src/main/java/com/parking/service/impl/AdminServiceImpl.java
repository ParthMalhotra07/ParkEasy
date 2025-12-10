package com.parking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parking.model.Admin;
import com.parking.model.Building;
import com.parking.model.Reservation;
import com.parking.service.AdminService;
import com.parking.util.JsonStore;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired private JsonStore jsonStore;
    
    private static final String BUILDINGS_FILE = "buildings.json";
    private static final String ADMINS_FILE = "admins.json";
    private static final String RESERVATIONS_FILE = "reservations.json";

    @Override
    public Admin registerAdmin(String username, String password, String email) {
        // Validate input
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (password == null || password.length() < 4) {
            throw new RuntimeException("Password must be at least 4 characters");
        }
        
        List<Admin> admins = jsonStore.read(ADMINS_FILE, Admin.class);
        
        // Check if username already exists
        for (Admin admin : admins) {
            if (admin.getUsername().equalsIgnoreCase(username.trim())) {
                throw new RuntimeException("Username already exists");
            }
        }
        
        // Create new admin
        Admin newAdmin = new Admin();
        newAdmin.setAdminId(UUID.randomUUID().toString());
        newAdmin.setUsername(username.trim());
        newAdmin.setPassword(password); // In production: hash this!
        newAdmin.setEmail(email != null ? email.trim() : "");
        
        admins.add(newAdmin);
        jsonStore.write(ADMINS_FILE, admins);
        
        return newAdmin;
    }

    @Override
    public Admin loginAdmin(String username, String password) {
        if (username == null || password == null) {
            throw new RuntimeException("Invalid credentials");
        }
        
        List<Admin> admins = jsonStore.read(ADMINS_FILE, Admin.class);
        
        for (Admin admin : admins) {
            if (admin.getUsername().equalsIgnoreCase(username.trim()) 
                && admin.getPassword().equals(password)) {
                return admin;
            }
        }
        
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public void validateBuilding(Building building) {
        // Validate building name
        if (building.getName() == null || building.getName().trim().isEmpty()) {
            throw new RuntimeException("Building name cannot be empty");
        }
        
        // Validate address
        if (building.getAddress() == null || building.getAddress().trim().isEmpty()) {
            throw new RuntimeException("Building address cannot be empty");
        }
        
        // Validate floors
        if (building.getFloors() == null || building.getFloors().isEmpty()) {
            throw new RuntimeException("Building must have at least one floor");
        }
        
        // Validate each floor has slots
        for (Building.Floor floor : building.getFloors()) {
            if (floor.getLevel() == null || floor.getLevel().trim().isEmpty()) {
                throw new RuntimeException("Floor level cannot be empty");
            }
            if (floor.getSlots() == null || floor.getSlots().isEmpty()) {
                throw new RuntimeException("Floor '" + floor.getLevel() + "' must have at least one slot");
            }
        }
    }

    @Override
    public void addBuilding(Building building, String adminId) {
        // Validate building first
        validateBuilding(building);
        
        // Check for duplicate building name
        List<Building> buildings = jsonStore.read(BUILDINGS_FILE, Building.class);
        for (Building b : buildings) {
            if (b.getName().equalsIgnoreCase(building.getName().trim())) {
                throw new RuntimeException("A building with name '" + building.getName() + "' already exists. Please choose a different name.");
            }
        }
        
        // Set the admin owner
        building.setAdminId(adminId);
        building.setName(building.getName().trim());
        
        buildings.add(building);
        jsonStore.write(BUILDINGS_FILE, buildings);
    }

    @Override
    public List<Building> getBuildings() {
        return jsonStore.read(BUILDINGS_FILE, Building.class);
    }

    @Override
    public List<Building> getBuildingsByAdmin(String adminId) {
        List<Building> allBuildings = jsonStore.read(BUILDINGS_FILE, Building.class);
        List<Building> adminBuildings = new ArrayList<>();
        
        for (Building b : allBuildings) {
            if (adminId.equals(b.getAdminId())) {
                adminBuildings.add(b);
            }
        }
        
        return adminBuildings;
    }
    
    @Override
    public List<Building> searchBuildings(String query) {
        List<Building> allBuildings = jsonStore.read(BUILDINGS_FILE, Building.class);
        
        if (query == null || query.trim().isEmpty()) {
            return allBuildings;
        }
        
        String searchTerm = query.trim().toLowerCase();
        List<Building> results = new ArrayList<>();
        
        for (Building b : allBuildings) {
            // Search by name or address
            if ((b.getName() != null && b.getName().toLowerCase().contains(searchTerm)) ||
                (b.getAddress() != null && b.getAddress().toLowerCase().contains(searchTerm))) {
                results.add(b);
            }
        }
        
        return results;
    }
    
    @Override
    public boolean hasActiveReservations(String buildingName) {
        List<Reservation> reservations = jsonStore.read(RESERVATIONS_FILE, Reservation.class);
        
        for (Reservation r : reservations) {
            if (r.getBuildingName().equalsIgnoreCase(buildingName) && "ACTIVE".equals(r.getStatus())) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean deleteBuilding(String buildingName, String adminId) {
        // Check for active reservations first
        if (hasActiveReservations(buildingName)) {
            throw new RuntimeException("Cannot delete building '" + buildingName + "' - there are active reservations. Please wait for all vehicles to checkout first.");
        }
        
        List<Building> buildings = jsonStore.read(BUILDINGS_FILE, Building.class);
        
        // Find and remove building only if admin owns it
        boolean removed = buildings.removeIf(b -> 
            b.getName().equalsIgnoreCase(buildingName) && adminId.equals(b.getAdminId())
        );
        
        if (!removed) {
            throw new RuntimeException("Building not found or you don't have permission to delete it");
        }
        
        jsonStore.write(BUILDINGS_FILE, buildings);
        return true;
    }
}