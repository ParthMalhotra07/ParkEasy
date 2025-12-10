package com.parking.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parking.model.Admin;
import com.parking.model.Building;
import com.parking.model.Floor;
import com.parking.model.FloorSlot;
import com.parking.model.Reservation;
import com.parking.repository.AdminRepository;
import com.parking.repository.BuildingRepository;
import com.parking.repository.ReservationRepository;
import com.parking.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired private AdminRepository adminRepository;
    @Autowired private BuildingRepository buildingRepository;
    @Autowired private ReservationRepository reservationRepository;

    @Override
    public Admin registerAdmin(String username, String password, String email) {
        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (password == null || password.length() < 4) {
            throw new RuntimeException("Password must be at least 4 characters");
        }
        
        Optional<Admin> existing = adminRepository.findByUsernameIgnoreCase(username.trim());
        if (existing.isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        Admin newAdmin = new Admin();
        newAdmin.setAdminId(UUID.randomUUID().toString());
        newAdmin.setUsername(username.trim());
        newAdmin.setPassword(password); // In production: hash this!
        newAdmin.setEmail(email != null ? email.trim() : "");
        
        return adminRepository.save(newAdmin);
    }

    @Override
    public Admin loginAdmin(String username, String password) {
        if (username == null || password == null) {
            throw new RuntimeException("Invalid credentials");
        }
        
        Optional<Admin> adminOpt = adminRepository.findByUsernameIgnoreCase(username.trim());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getPassword().equals(password)) {
                return admin;
            }
        }
        
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public void validateBuilding(Building building) {
        if (building.getName() == null || building.getName().trim().isEmpty()) {
            throw new RuntimeException("Building name cannot be empty");
        }
        
        if (building.getAddress() == null || building.getAddress().trim().isEmpty()) {
            throw new RuntimeException("Building address cannot be empty");
        }
        
        if (building.getFloors() == null || building.getFloors().isEmpty()) {
            throw new RuntimeException("Building must have at least one floor");
        }
        
        for (Floor floor : building.getFloors()) {
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
        validateBuilding(building);
        
        Optional<Building> existing = buildingRepository.findByNameIgnoreCase(building.getName().trim());
        if (existing.isPresent()) {
            throw new RuntimeException("A building with name '" + building.getName() + "' already exists. Please choose a different name.");
        }
        
        building.setAdminId(adminId);
        building.setName(building.getName().trim());
        
        // Ensure bidirectional relationships are set
        if (building.getFloors() != null) {
            for (Floor f : building.getFloors()) {
                f.setBuilding(building);
                if (f.getSlots() != null) {
                    for (FloorSlot s : f.getSlots()) {
                        s.setFloor(f);
                    }
                }
            }
        }
        
        buildingRepository.save(building);
    }

    @Override
    public List<Building> getBuildings() {
        return buildingRepository.findAll();
    }

    @Override
    public List<Building> getBuildingsByAdmin(String adminId) {
        return buildingRepository.findByAdminId(adminId);
    }
    
    @Override
    public List<Building> searchBuildings(String query) {
        if (query == null || query.trim().isEmpty()) {
            return buildingRepository.findAll();
        }
        String searchTerm = query.trim();
        return buildingRepository.findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(searchTerm, searchTerm);
    }
    
    @Override
    public boolean hasActiveReservations(String buildingName) {
        List<Reservation> activeReservations = reservationRepository.findByBuildingNameIgnoreCaseAndStatus(buildingName, "ACTIVE");
        return !activeReservations.isEmpty();
    }

    @Override
    public boolean deleteBuilding(String buildingName, String adminId) {
        if (hasActiveReservations(buildingName)) {
            throw new RuntimeException("Cannot delete building '" + buildingName + "' - there are active reservations. Please wait for all vehicles to checkout first.");
        }
        
        Optional<Building> buildingOpt = buildingRepository.findByNameIgnoreCase(buildingName);
        if (buildingOpt.isPresent()) {
            Building b = buildingOpt.get();
            if (adminId.equals(b.getAdminId())) {
                buildingRepository.delete(b);
                return true;
            }
        }
        
        throw new RuntimeException("Building not found or you don't have permission to delete it");
    }
}