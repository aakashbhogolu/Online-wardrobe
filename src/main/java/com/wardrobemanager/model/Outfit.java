package com.wardrobemanager.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Outfit implements Serializable {
    private String id;
    private String name;
    private List<ClothingItem> items;
    private List<Occasion> occasions;
    private Season season;
    private List<LocalDate> wearDates;

    public Outfit(String name, Season season) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.name = name;
        this.season = season;
        this.items = new ArrayList<>();
        this.occasions = new ArrayList<>();
        this.wearDates = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<ClothingItem> getItems() { return items; }
    public void setItems(List<ClothingItem> items) { this.items = items; }
    public List<Occasion> getOccasions() { return occasions; }
    public void setOccasions(List<Occasion> occasions) { this.occasions = occasions; }
    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }
    public List<LocalDate> getWearDates() { return wearDates; }

    public void addItem(ClothingItem item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }

    public void removeItem(ClothingItem item) {
        items.remove(item);
    }

    public void addOccasion(Occasion occasion) {
        if (!occasions.contains(occasion)) {
            occasions.add(occasion);
        }
    }

    public void addWearDate(LocalDate date) {
        if (!wearDates.contains(date)) {
            wearDates.add(date);
            // Update wear dates for all items in the outfit
            for (ClothingItem item : items) {
                item.addWearDate(date);
            }
        }
    }

    @Override
    public String toString() {
        return name + " (" + season + ")";
    }
} 