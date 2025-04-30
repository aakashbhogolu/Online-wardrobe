package com.wardrobemanager.model;

public enum Occasion {
    CASUAL("Casual"),
    FORMAL("Formal"),
    BUSINESS("Business"),
    PARTY("Party"),
    SPORTS("Sports"),
    OUTDOOR("Outdoor");

    private final String displayName;

    Occasion(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 