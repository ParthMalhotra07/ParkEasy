package com.parking.model;

import java.util.UUID;

public class Admin {
    private String adminId;
    private String username;
    private String password;
    private String email;

    public Admin() {
        this.adminId = UUID.randomUUID().toString();
    }

    // Getters & Setters
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
