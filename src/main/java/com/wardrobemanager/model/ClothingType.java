package com.wardrobemanager.model;

public enum ClothingType {
    // Men's clothing
    MENS_TSHIRT("T-Shirt", Gender.MEN),
    MENS_SHIRT("Shirt", Gender.MEN),
    MENS_PANTS("Pants", Gender.MEN),
    MENS_JEANS("Jeans", Gender.MEN),
    MENS_SHORTS("Shorts", Gender.MEN),
    MENS_TRACKPANTS("Track Pants", Gender.MEN),
    MENS_FORMAL_PANTS("Formal Pants", Gender.MEN),
    
    // Women's clothing
    WOMENS_TOP("Top", Gender.WOMEN),
    WOMENS_TSHIRT("T-Shirt", Gender.WOMEN),
    WOMENS_SHIRT("Shirt", Gender.WOMEN),
    WOMENS_PANTS("Pants", Gender.WOMEN),
    WOMENS_JEANS("Jeans", Gender.WOMEN),
    WOMENS_SKIRT("Skirt", Gender.WOMEN),
    WOMENS_DRESS("Dress", Gender.WOMEN),
    WOMENS_FORMAL_PANTS("Formal Pants", Gender.WOMEN);

    private final String displayName;
    private final Gender gender;

    ClothingType(String displayName, Gender gender) {
        this.displayName = displayName;
        this.gender = gender;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static ClothingType[] getTypesForGender(Gender gender) {
        return java.util.Arrays.stream(values())
            .filter(type -> type.gender == gender)
            .toArray(ClothingType[]::new);
    }
} 