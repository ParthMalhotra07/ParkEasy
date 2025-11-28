package com.parking.model;

import java.util.List;
import java.util.ArrayList;

public class Building {
    private String name;
    private String address;
    private List<Floor> floors = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Floor> getFloors() { return floors; }
    public void setFloors(List<Floor> floors) { this.floors = floors; }

    public static class Floor {
        private String level; // e.g., "B1"
        private List<FloorSlot> slots = new ArrayList<>();
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public List<FloorSlot> getSlots() { return slots; }
        public void setSlots(List<FloorSlot> slots) { this.slots = slots; }
    }

    public static class FloorSlot {
        private String id;
        private int row;
        private int col;
        private String type;
        private boolean isOccupied;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public int getRow() { return row; }
        public void setRow(int row) { this.row = row; }
        public int getCol() { return col; }
        public void setCol(int col) { this.col = col; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public boolean isOccupied() { return isOccupied; }
        public void setOccupied(boolean occupied) { isOccupied = occupied; }
    }
}