package com.parking.repository;

import com.parking.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByNameIgnoreCase(String name);
    List<Building> findByAdminId(String adminId);
    List<Building> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String address);
}
