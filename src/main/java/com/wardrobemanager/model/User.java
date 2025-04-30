package com.wardrobemanager.model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String password;
    private String email;
    private Gender preferredWardrobe;

    public User(String username, String password, String email, Gender preferredWardrobe) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.preferredWardrobe = preferredWardrobe;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public Gender getPreferredWardrobe() { return preferredWardrobe; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setPreferredWardrobe(Gender preferredWardrobe) { this.preferredWardrobe = preferredWardrobe; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
} 