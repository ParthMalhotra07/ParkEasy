package com.parking.repository;

import com.parking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> findByVehicleNumberIgnoreCaseAndStatus(String vehicleNumber, String status);
    List<Reservation> findByBuildingNameIgnoreCaseAndStatus(String buildingName, String status);
}
