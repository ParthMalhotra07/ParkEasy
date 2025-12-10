package com.parking.model;

import java.time.LocalDateTime;

public class Reservation {
    private String reservationId;
    private String vehicleNumber; // <--- The Main Identity
    private String buildingName;
    private String floorLevel;
    private String slotId;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status; // "ACTIVE" or "PAID"
    private double billAmount;

    public Reservation() {}

    // Getters & Setters
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

    public String getFloorLevel() { return floorLevel; }
    public void setFloorLevel(String floorLevel) { this.floorLevel = floorLevel; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getBillAmount() { return billAmount; }
    public void setBillAmount(double billAmount) { this.billAmount = billAmount; }
}