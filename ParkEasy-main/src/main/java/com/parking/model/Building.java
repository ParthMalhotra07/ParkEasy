package com.parking.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long pk;
    
    private String name;
    private String address;
    private String adminId; // Owner admin ID
    
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Floor> floors = new ArrayList<>();

    public Long getPk() { return pk; }
    public void setPk(Long pk) { this.pk = pk; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public List<Floor> getFloors() { return floors; }
    
    public void setFloors(List<Floor> floors) {
        this.floors = floors;
        if (floors != null) {
            for (Floor f : floors) {
                f.setBuilding(this);
            }
        }
    }
}