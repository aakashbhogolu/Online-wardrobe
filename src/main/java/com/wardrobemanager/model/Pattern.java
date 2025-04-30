package com.wardrobemanager.model;

public enum Pattern {
    PLAIN("Plain"),
    STRIPED("Striped"),
    CHECKERED("Checkered"),
    FLORAL("Floral"),
    POLKA_DOT("Polka Dot"),
    TEXTURED("Textured"),
    GRAPHIC("Graphic"),
    PRINTED("Printed");

    private final String displayName;

    Pattern(String displayName) {
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