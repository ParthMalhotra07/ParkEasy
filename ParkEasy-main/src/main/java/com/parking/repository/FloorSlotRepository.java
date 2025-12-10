package com.parking.repository;

import com.parking.model.FloorSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface FloorSlotRepository extends JpaRepository<FloorSlot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM FloorSlot s WHERE lower(s.floor.building.name) = lower(:buildingName) AND lower(s.floor.level) = lower(:level) AND s.id = :slotId")
    Optional<FloorSlot> findSlotForBooking(
        @Param("buildingName") String buildingName, 
        @Param("level") String level, 
        @Param("slotId") String slotId
    );
}
