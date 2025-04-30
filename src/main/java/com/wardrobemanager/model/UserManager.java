package com.wardrobemanager.model;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class UserManager {
    private static final String USERS_FILE = System.getProperty("user.home") + "/wardrobe-manager/users.dat";
    private Map<String, User> users;
    private User currentUser;

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    public boolean signup(String username, String password, String email, Gender preferredWardrobe) {
        // Validate input
        if (!isValidUsername(username) || !isValidPassword(password) || !isValidEmail(email)) {
            return false;
        }

        // Check if username already exists
        if (users.containsKey(username)) {
            return false;
        }

        // Create new user with hashed password
        User newUser = new User(username, hashPassword(password), email, preferredWardrobe);
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(hashPassword(password))) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void loadUsers() {
        if (Files.exists(Paths.get(USERS_FILE))) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
                users = (Map<String, User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                users = new HashMap<>();
            }
        }
    }

    private void saveUsers() {
        try {
            Files.createDirectories(Paths.get(USERS_FILE).getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
                oos.writeObject(users);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback to plain password if hashing fails
        }
    }

    private boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    private boolean isValidEmail(String email) {
        return email != null && Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(email).matches();
    }
} 