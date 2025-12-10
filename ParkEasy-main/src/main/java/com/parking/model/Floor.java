package com.parking.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Floor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long pk;
    
    private String level; // e.g., "B1"
    
    @ManyToOne
    @JoinColumn(name = "building_pk")
    @JsonIgnore
    private Building building;
    
    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FloorSlot> slots = new ArrayList<>();
    
    public Long getPk() { return pk; }
    public void setPk(Long pk) { this.pk = pk; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
    
    public List<FloorSlot> getSlots() { return slots; }
    
    public void setSlots(List<FloorSlot> slots) {
        this.slots = slots;
        if (slots != null) {
            for (FloorSlot s : slots) {
                s.setFloor(this);
            }
        }
    }
}
