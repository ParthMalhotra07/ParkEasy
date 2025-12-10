package com.parking.service;
import java.util.List;

import com.parking.model.Admin;
import com.parking.model.Building;

public interface AdminService {
    // Admin Authentication
    Admin registerAdmin(String username, String password, String email);
    Admin loginAdmin(String username, String password);
    
    // Building Management
    void addBuilding(Building building, String adminId);
    List<Building> getBuildings();
    List<Building> getBuildingsByAdmin(String adminId);
    boolean deleteBuilding(String buildingName, String adminId);
    List<Building> searchBuildings(String query);
    
    // Validation
    void validateBuilding(Building building);
    boolean hasActiveReservations(String buildingName);
}