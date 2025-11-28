package com.parking.service.impl;

import com.parking.model.Building;
import com.parking.service.AdminService;
import com.parking.util.JsonStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private JsonStore jsonStore;

    private static final String FILE_NAME = "buildings.json";

    @Override
    public void addBuilding(Building building) {
        List<Building> buildings = jsonStore.read(FILE_NAME, Building.class);
        buildings.add(building);
        jsonStore.write(FILE_NAME, buildings);
        System.out.println("Building '" + building.getName() + "' saved to " + FILE_NAME);
    }

    @Override
    public List<Building> getBuildings() {
        return jsonStore.read(FILE_NAME, Building.class);
    }
}