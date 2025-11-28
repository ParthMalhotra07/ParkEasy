package com.parking.service;

import com.parking.model.Building;
import java.util.List;

public interface AdminService {
    void addBuilding(Building building);
    List<Building> getBuildings();
}