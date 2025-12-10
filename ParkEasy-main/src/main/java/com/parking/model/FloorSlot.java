package com.parking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class FloorSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long pk;
    
    private String id;
    
    @Column(name = "slot_row")
    private int row;
    
    @Column(name = "slot_col")
    private int col;
    
    private String type;
    private boolean isOccupied;
    
    @ManyToOne
    @JoinColumn(name = "floor_pk")
    @JsonIgnore
    private Floor floor;

    public Long getPk() { return pk; }
    public void setPk(Long pk) { this.pk = pk; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
    
    public Floor getFloor() { return floor; }
    public void setFloor(Floor floor) { this.floor = floor; }
}
