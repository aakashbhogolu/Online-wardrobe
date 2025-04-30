package com.wardrobemanager.model;

import java.util.*;

public class ColorMatcher {
    private static final Map<String, List<String>> colorCombinations = new HashMap<>();
    
    static {
        // Initialize color combinations
        colorCombinations.put("Black", Arrays.asList("White", "Gray", "Red", "Blue", "Green"));
        colorCombinations.put("White", Arrays.asList("Black", "Navy", "Red", "Blue", "Brown"));
        colorCombinations.put("Blue", Arrays.asList("White", "Gray", "Brown", "Black", "Beige"));
        colorCombinations.put("Red", Arrays.asList("Black", "White", "Gray", "Navy", "Beige"));
        colorCombinations.put("Gray", Arrays.asList("Black", "White", "Blue", "Red", "Navy"));
        colorCombinations.put("Navy", Arrays.asList("White", "Beige", "Gray", "Red", "Brown"));
        colorCombinations.put("Brown", Arrays.asList("Blue", "White", "Beige", "Green", "Black"));
        colorCombinations.put("Beige", Arrays.asList("Brown", "Navy", "Black", "Red", "White"));
        colorCombinations.put("Green", Arrays.asList("Brown", "White", "Black", "Beige", "Gray"));
    }

    public static List<String> getMatchingColors(String color) {
        return colorCombinations.getOrDefault(
            color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase(),
            new ArrayList<>()
        );
    }

    public static List<String> getAllColors() {
        return new ArrayList<>(colorCombinations.keySet());
    }

    public static boolean isValidColor(String color) {
        return colorCombinations.containsKey(
            color.substring(0, 1).toUpperCase() + color.substring(1).toLowerCase()
        );
    }
} 