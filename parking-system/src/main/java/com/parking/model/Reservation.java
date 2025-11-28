package com.parking.model;

import java.time.LocalDateTime;

public class Reservation {

    private String reservationId;
    private String userId;
    private String buildingName;
    private String slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private double billAmount;

    public Reservation() {}

    public Reservation(String reservationId, String userId, String buildingName, String slotId, LocalDateTime startTime) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.buildingName = buildingName;
        this.slotId = slotId;
        this.startTime = startTime;
        this.status = "ACTIVE";
        this.billAmount = 0.0;
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }

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