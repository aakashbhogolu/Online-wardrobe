package com.wardrobemanager.model;

public enum Gender {
    MEN("Men's Wardrobe"),
    WOMEN("Women's Wardrobe");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 