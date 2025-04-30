package com.wardrobemanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClothingItem implements Serializable {
    private String id;
    private String name;
    private ClothingType type;
    private String color;
    private Pattern pattern;
    private List<Occasion> occasions;
    private List<LocalDate> wearDates;
    private String imagePath;

    public ClothingItem(String name, ClothingType type, String color, Pattern pattern) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = name;
        this.type = type;
        this.color = color;
        this.pattern = pattern;
        this.occasions = new ArrayList<>();
        this.wearDates = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ClothingType getType() { return type; }
    public void setType(ClothingType type) { this.type = type; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Pattern getPattern() { return pattern; }
    public void setPattern(Pattern pattern) { this.pattern = pattern; }
    public List<Occasion> getOccasions() { return occasions; }
    public void setOccasions(List<Occasion> occasions) { this.occasions = occasions; }
    public List<LocalDate> getWearDates() { return wearDates; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public void addWearDate(LocalDate date) {
        if (!wearDates.contains(date)) {
            wearDates.add(date);
        }
    }

    public void addOccasion(Occasion occasion) {
        if (!occasions.contains(occasion)) {
            occasions.add(occasion);
        }
    }

    @Override
    public String toString() {
        return name + " (" + type + " - " + pattern + ")";
    }
} 