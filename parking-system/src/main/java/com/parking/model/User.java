package com.parking.model;

public class User {
    private String userId;
    private String username;
    private String email;
    private String role;
    private String vehicleNumber;

    public User() {}

    public User(String userId, String username, String email, String role, String vehicleNumber) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.vehicleNumber = vehicleNumber;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    @Override
    public String toString() {
        return "User{" + "userId='" + userId + '\'' + ", username='" + username + '\'' + '}';
    }
}