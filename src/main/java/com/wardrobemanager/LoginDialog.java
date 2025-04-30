package com.wardrobemanager;

import com.wardrobemanager.model.*;
import com.wardrobemanager.components.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginDialog extends JDialog {
    private UserManager userManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<Gender> genderCombo;
    private JTabbedPane tabbedPane;
    private boolean authenticated = false;
    private User authenticatedUser;

    public LoginDialog(Frame parent, UserManager userManager) {
        super(parent, "Login/Signup", true);
        this.userManager = userManager;
        setUndecorated(true); // Remove window decorations for modern look
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
    }

    private void initComponents() {
        // Main container with gradient background
        AnimatedPanel mainPanel = new AnimatedPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setGradientColors(new Color(135, 206, 235), new Color(70, 130, 180));
        setContentPane(mainPanel);

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Wardrobe Manager");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Close button
        AnimatedButton closeButton = new AnimatedButton("×");
        closeButton.setFont(new Font("Arial", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());
        titlePanel.add(closeButton);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Tabs panel
        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        
        // Login Panel
        AnimatedPanel loginPanel = new AnimatedPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(new Color(50, 50, 50));
        loginPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        usernameField = createStyledTextField();
        loginPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(new Color(50, 50, 50));
        loginPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        passwordField = createStyledPasswordField();
        loginPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        AnimatedButton loginButton = new AnimatedButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton, gbc);

        // Signup Panel
        AnimatedPanel signupPanel = new AnimatedPanel();
        signupPanel.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Signup fields
        addSignupField(signupPanel, "Username:", createStyledTextField(), gbc, 0);
        JTextField signupUsernameField = (JTextField) signupPanel.getComponent(1);
        
        addSignupField(signupPanel, "Password:", createStyledPasswordField(), gbc, 1);
        JPasswordField signupPasswordField = (JPasswordField) signupPanel.getComponent(3);
        
        addSignupField(signupPanel, "Email:", createStyledTextField(), gbc, 2);
        emailField = (JTextField) signupPanel.getComponent(5);

        // Gender combo
        addSignupField(signupPanel, "Preferred Wardrobe:", createStyledComboBox(), gbc, 3);
        genderCombo = (JComboBox<Gender>) signupPanel.getComponent(7);

        // Signup button
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        AnimatedButton signupButton = new AnimatedButton("Sign Up");
        signupButton.addActionListener(e -> handleSignup(
            signupUsernameField.getText(),
            new String(signupPasswordField.getPassword()),
            emailField.getText(),
            (Gender) genderCombo.getSelectedItem()
        ));
        signupPanel.add(signupButton, gbc);

        tabbedPane.addTab("Login", loginPanel);
        tabbedPane.addTab("Sign Up", signupPanel);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Make the dialog draggable
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                setLocation(getLocation().x + e.getX() - getWidth() / 2,
                          getLocation().y + e.getY() - 20);
            }
        });
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private JComboBox<Gender> createStyledComboBox() {
        JComboBox<Gender> combo = new JComboBox<>(Gender.values());
        combo.setFont(new Font("Arial", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private void addSignupField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setForeground(new Color(50, 50, 50));
        panel.add(label, gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = userManager.login(username, password);
        if (user != null) {
            authenticated = true;
            authenticatedUser = user;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSignup(String username, String password, String email, Gender preferredWardrobe) {
        if (userManager.signup(username, password, email, preferredWardrobe)) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully! Please login.",
                "Signup Successful",
                JOptionPane.INFORMATION_MESSAGE);
            tabbedPane.setSelectedIndex(0); // Switch to login tab
            usernameField.setText(username);
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to create account. Please check your input.",
                "Signup Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
} 