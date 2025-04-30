package com.wardrobemanager;

import com.wardrobemanager.model.*;
import com.wardrobemanager.components.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class WardrobeManagerGUI extends JFrame {
    private String DATA_FILE;
    private UserManager userManager;
    private User currentUser;
    private String uploadDir = System.getProperty("user.home") + "/wardrobe-manager/uploads";
    
    private Gender selectedGender;
    private List<ClothingItem> clothingItems = new ArrayList<>();
    private List<Outfit> outfits = new ArrayList<>();
    
    private JPanel mainPanel;
    private JTabbedPane tabbedPane;
    private JPanel wardrobePanel;
    private JPanel suggestionsPanel;
    
    private JTextField nameField;
    private JComboBox<ClothingType> categoryComboBox;
    private JComboBox<Occasion> occasionComboBox;
    private JTextArea descriptionArea;
    private DefaultListModel<String> clothingListModel;
    private JList<String> clothingList;
    private Map<String, ClothingItem> clothingItemsMap;
    private JLabel imagePreview;
    private String currentImagePath;

    public WardrobeManagerGUI() {
        // Initialize UserManager
        userManager = new UserManager();
        
        // Show login dialog first
        showLoginDialog();
        
        // If not authenticated, close the application
        if (!isUserAuthenticated()) {
            dispose();
            System.exit(0);
            return;
        }
        
        // Initialize the main window
        initializeMainWindow();
    }
    
    private boolean isUserAuthenticated() {
        return currentUser != null;
    }
    
    private void initializeMainWindow() {
        setTitle("Wardrobe Manager - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Initialize default values
        selectedGender = currentUser.getPreferredWardrobe();
        clothingItems = new ArrayList<>();
        outfits = new ArrayList<>();
        DATA_FILE = System.getProperty("user.home") + "/wardrobe-manager/" + 
                   currentUser.getUsername() + "_wardrobe.dat";
        
        createMenuBar();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Add Item", createAddItemPanel());
        tabbedPane.addTab("View Wardrobe", createViewWardrobePanel());
        tabbedPane.addTab("Outfit Suggestions", createSuggestionsPanel());
        add(tabbedPane);
        
        // Add window closing listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
        
        // Load wardrobe data
        loadWardrobe();
    }

    private void showLoginDialog() {
        LoginDialog loginDialog = new LoginDialog(this, userManager);
        loginDialog.setVisible(true);
        
        if (loginDialog.isAuthenticated()) {
            currentUser = loginDialog.getAuthenticatedUser();
        }
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Would you like to save your wardrobe before logging out?",
            "Save Wardrobe",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (response == JOptionPane.CANCEL_OPTION) {
            return;
        }
        
        if (response == JOptionPane.YES_OPTION) {
            saveWardrobe();
        }
        
        // Clear current user data
        currentUser = null;
        clothingItems.clear();
        outfits.clear();
        
        // Dispose current window and show login
        dispose();
        WardrobeManagerGUI newGui = new WardrobeManagerGUI();
        if (newGui.isUserAuthenticated()) {
            newGui.setVisible(true);
        }
    }

    private void initializeMainInterface() {
        getContentPane().removeAll();
        
        loadData();

        mainPanel = new AnimatedPanel();
        mainPanel.setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(new Color(240, 240, 240));

        createWardrobePanel();
        createSuggestionsPanel();

        tabbedPane.addTab("My Wardrobe", wardrobePanel);
        tabbedPane.addTab("Outfit Suggestions", suggestionsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);

        revalidate();
        repaint();
    }

    private void createWardrobePanel() {
        wardrobePanel = new AnimatedPanel();
        wardrobePanel.setLayout(new BorderLayout());
        
        // Create toolbar with modern styling
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(70, 130, 180));
        
        AnimatedButton addButton = new AnimatedButton("Add Clothing Item");
        addButton.addActionListener(e -> showAddClothingDialog());
        toolBar.add(addButton);

        // Create main content panel with animation
        AnimatedPanel contentPanel = new AnimatedPanel();
        contentPanel.setLayout(new BorderLayout());
        
        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        itemsPanel.setOpaque(false);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Group items by type with animated panels
        Map<ClothingType, List<ClothingItem>> itemsByType = new HashMap<>();
        for (ClothingItem item : clothingItems) {
            if (item.getType().getGender() == selectedGender) {
                itemsByType.computeIfAbsent(item.getType(), k -> new ArrayList<>()).add(item);
            }
        }

        for (ClothingType type : ClothingType.getTypesForGender(selectedGender)) {
            List<ClothingItem> items = itemsByType.getOrDefault(type, new ArrayList<>());
            if (!items.isEmpty()) {
                AnimatedPanel typePanel = new AnimatedPanel();
                typePanel.setLayout(new BorderLayout());
                typePanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(70, 130, 180)),
                    type.getDisplayName(),
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 14),
                    new Color(70, 130, 180)
                ));
                
                JPanel itemsList = new JPanel(new GridLayout(0, 1, 5, 5));
                itemsList.setOpaque(false);
                
                for (ClothingItem item : items) {
                    AnimatedPanel itemRow = new AnimatedPanel();
                    itemRow.setLayout(new FlowLayout(FlowLayout.LEFT));
                    JLabel itemLabel = new JLabel(item.getName() + " (" + item.getColor() + ")");
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    itemRow.add(itemLabel);
                    itemsList.add(itemRow);
                }
                
                typePanel.add(itemsList, BorderLayout.CENTER);
                itemsPanel.add(typePanel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(240, 240, 240));
        
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        wardrobePanel.add(toolBar, BorderLayout.NORTH);
        wardrobePanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSuggestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create control panel at the top
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setBackground(new Color(240, 240, 240));
        
        controlPanel.add(new JLabel("Occasion: "));
        JComboBox<Occasion> occasionCombo = new JComboBox<>(Occasion.values());
        controlPanel.add(occasionCombo);
        
        AnimatedButton suggestButton = new AnimatedButton("Suggest Outfits");
        suggestButton.addActionListener(e -> suggestOutfits(occasionCombo.getSelectedItem()));
        controlPanel.add(suggestButton);
        
        // Create content panel for suggestions
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void showAddClothingDialog() {
        JDialog dialog = new JDialog(this, "Add Clothing Item", true);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Add form fields
        JTextField nameField = new JTextField();
        JComboBox<ClothingType> typeCombo = new JComboBox<>(ClothingType.getTypesForGender(selectedGender));
        JComboBox<String> colorCombo = new JComboBox<>(ColorMatcher.getAllColors().toArray(new String[0]));
        JComboBox<Pattern> patternCombo = new JComboBox<>(Pattern.values());
        
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeCombo);
        formPanel.add(new JLabel("Color:"));
        formPanel.add(colorCombo);
        formPanel.add(new JLabel("Pattern:"));
        formPanel.add(patternCombo);

        // Add occasions checkboxes
        JPanel occasionsPanel = new JPanel();
        occasionsPanel.setBorder(BorderFactory.createTitledBorder("Occasions"));
        List<JCheckBox> occasionBoxes = new ArrayList<>();
        for (Occasion occasion : Occasion.values()) {
            JCheckBox box = new JCheckBox(occasion.toString());
            occasionBoxes.add(box);
            occasionsPanel.add(box);
        }

        // Add buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a name");
                return;
            }

            // Create clothing item
            ClothingItem item = new ClothingItem(
                nameField.getText(),
                (ClothingType) typeCombo.getSelectedItem(),
                (String) colorCombo.getSelectedItem(),
                (Pattern) patternCombo.getSelectedItem()
            );

            // Add selected occasions
            for (int i = 0; i < occasionBoxes.size(); i++) {
                if (occasionBoxes.get(i).isSelected()) {
                    item.addOccasion(Occasion.values()[i]);
                }
            }

            // Add item and refresh display
            clothingItems.add(item);
            createWardrobePanel();
            tabbedPane.setComponentAt(0, wardrobePanel);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add all panels to dialog
        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.add(occasionsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void suggestOutfits(Object selectedOccasion) {
        if (!(selectedOccasion instanceof Occasion)) return;
        Occasion occasion = (Occasion) selectedOccasion;
        
        // Get the content panel from the suggestions tab
        JScrollPane scrollPane = (JScrollPane) ((JPanel) tabbedPane.getComponentAt(2)).getComponent(1);
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
        contentPanel.removeAll();
        
        // Get all items for the selected occasion
        List<ClothingItem> topItems = new ArrayList<>();
        List<ClothingItem> bottomItems = new ArrayList<>();
        
        for (ClothingItem item : clothingItems) {
            if (item.getType().getGender() == selectedGender && 
                item.getOccasions().contains(occasion)) {
                if (isTopClothing(item.getType())) {
                    topItems.add(item);
                } else if (isBottomClothing(item.getType())) {
                    bottomItems.add(item);
                }
            }
        }
        
        if (topItems.isEmpty() || bottomItems.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No matching items found for this occasion.");
            noItemsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(noItemsLabel);
            contentPanel.revalidate();
            contentPanel.repaint();
            return;
        }
        
        // Create outfit suggestions
        for (ClothingItem top : topItems) {
            for (ClothingItem bottom : bottomItems) {
                if (ColorMatcher.getMatchingColors(top.getColor()).contains(bottom.getColor())) {
                    // Create outfit panel
                    AnimatedPanel outfitPanel = new AnimatedPanel();
                    outfitPanel.setLayout(new BorderLayout());
                    outfitPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    outfitPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
                    
                    // Create items panel
                    JPanel itemsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
                    itemsPanel.setOpaque(false);
                    
                    // Add top item
                    AnimatedPanel topPanel = createOutfitItemPanel(top);
                    itemsPanel.add(topPanel);
                    
                    // Add bottom item
                    AnimatedPanel bottomPanel = createOutfitItemPanel(bottom);
                    itemsPanel.add(bottomPanel);
                    
                    outfitPanel.add(itemsPanel, BorderLayout.CENTER);
                    
                    // Add outfit description
                    JPanel descPanel = new JPanel();
                    descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
                    descPanel.setOpaque(false);
                    
                    JLabel titleLabel = new JLabel("Suggested Outfit");
                    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    JLabel descLabel = new JLabel(String.format("%s with %s", 
                        top.getName(), bottom.getName()));
                    descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    
                    descPanel.add(titleLabel);
                    descPanel.add(Box.createVerticalStrut(5));
                    descPanel.add(descLabel);
                    
                    outfitPanel.add(descPanel, BorderLayout.SOUTH);
                    
                    // Add to content panel with some spacing
                    contentPanel.add(outfitPanel);
                    contentPanel.add(Box.createVerticalStrut(10));
                }
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private AnimatedPanel createOutfitItemPanel(ClothingItem item) {
        AnimatedPanel panel = new AnimatedPanel();
        panel.setLayout(new BorderLayout());
        
        // Create image viewer
        AnimatedImageViewer imageViewer = new AnimatedImageViewer();
        imageViewer.setClothingDetails(item.getType(), item.getColor());
        if (item.getImagePath() != null) {
            imageViewer.setImage(item.getImagePath());
        }
        
        // Create details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(item.getType().getDisplayName());
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel colorLabel = new JLabel("Color: " + item.getColor());
        colorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        detailsPanel.add(nameLabel);
        detailsPanel.add(typeLabel);
        detailsPanel.add(colorLabel);
        
        panel.add(imageViewer, BorderLayout.CENTER);
        panel.add(detailsPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private boolean isTopClothing(ClothingType type) {
        String typeName = type.toString().toUpperCase();
        return typeName.contains("SHIRT") || typeName.contains("TOP") || typeName.contains("T-SHIRT");
    }

    private boolean isBottomClothing(ClothingType type) {
        String typeName = type.toString().toUpperCase();
        return typeName.contains("PANTS") || typeName.contains("JEANS") || typeName.contains("SKIRT");
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(clothingItems);
            oos.writeObject(outfits);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        if (Files.exists(Paths.get(DATA_FILE))) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                clothingItems = (List<ClothingItem>) ois.readObject();
                outfits = (List<Outfit>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createAddItemPanel() {
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        addItemPanel.add(nameField, gbc);

        // Category dropdown
        gbc.gridx = 0; gbc.gridy = 1;
        addItemPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryComboBox = new JComboBox<>(ClothingType.getTypesForGender(selectedGender));
        addItemPanel.add(categoryComboBox, gbc);

        // Pattern dropdown
        gbc.gridx = 0; gbc.gridy = 2;
        addItemPanel.add(new JLabel("Pattern:"), gbc);
        gbc.gridx = 1;
        JComboBox<Pattern> patternComboBox = new JComboBox<>(Pattern.values());
        addItemPanel.add(patternComboBox, gbc);

        // Color field
        gbc.gridx = 0; gbc.gridy = 3;
        addItemPanel.add(new JLabel("Color:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> colorComboBox = new JComboBox<>(ColorMatcher.getAllColors().toArray(new String[0]));
        addItemPanel.add(colorComboBox, gbc);

        // Occasions
        gbc.gridx = 0; gbc.gridy = 4;
        addItemPanel.add(new JLabel("Occasions:"), gbc);
        gbc.gridx = 1;
        JPanel occasionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        List<JCheckBox> occasionCheckboxes = new ArrayList<>();
        for (Occasion occasion : Occasion.values()) {
            JCheckBox checkbox = new JCheckBox(occasion.toString());
            occasionCheckboxes.add(checkbox);
            occasionsPanel.add(checkbox);
        }
        addItemPanel.add(occasionsPanel, gbc);

        // Image preview
        gbc.gridx = 0; gbc.gridy = 5;
        addItemPanel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        AnimatedImageViewer imageViewer = new AnimatedImageViewer();
        addItemPanel.add(imageViewer, gbc);

        // Image selection button
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        AnimatedButton selectImageButton = new AnimatedButton("Select Image");
        selectImageButton.addActionListener(e -> {
            String imagePath = selectImage();
            if (imagePath != null) {
                imageViewer.setImage(imagePath);
                currentImagePath = imagePath;
            }
        });
        addItemPanel.add(selectImageButton, gbc);

        // Add item button
        gbc.gridx = 0; gbc.gridy = 7;
        AnimatedButton addButton = new AnimatedButton("Add Item");
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            ClothingType type = (ClothingType) categoryComboBox.getSelectedItem();
            String color = (String) colorComboBox.getSelectedItem();
            Pattern pattern = (Pattern) patternComboBox.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name for the item.");
                return;
            }

            ClothingItem item = new ClothingItem(name, type, color, pattern);
            item.setImagePath(currentImagePath);

            // Add selected occasions
            for (int i = 0; i < occasionCheckboxes.size(); i++) {
                if (occasionCheckboxes.get(i).isSelected()) {
                    item.addOccasion(Occasion.values()[i]);
                }
            }

            clothingItems.add(item);
            refreshWardrobeDisplay();
            clearForm(nameField, occasionCheckboxes, imageViewer);
        });
        addItemPanel.add(addButton, gbc);

        return addItemPanel;
    }

    private void clearForm(JTextField nameField, List<JCheckBox> checkboxes, AnimatedImageViewer imageViewer) {
        nameField.setText("");
        checkboxes.forEach(cb -> cb.setSelected(false));
        imageViewer.setImage(null);
        currentImagePath = null;
    }

    private String selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path destination = Paths.get(uploadDir, fileName);
                Files.createDirectories(destination.getParent());
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                return destination.toString();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error handling image: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private void displayClothingItem(ClothingItem item) {
        JDialog dialog = new JDialog(this, "View Item", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        AnimatedPanel contentPanel = new AnimatedPanel();
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Image viewer
        AnimatedImageViewer imageViewer = new AnimatedImageViewer();
        if (item.getImagePath() != null) {
            imageViewer.setImage(item.getImagePath());
        }
        contentPanel.add(imageViewer, BorderLayout.CENTER);

        // Details panel
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setOpaque(false);
        detailsPanel.add(new JLabel("Name: " + item.getName()));
        detailsPanel.add(new JLabel("Type: " + item.getType().getDisplayName()));
        detailsPanel.add(new JLabel("Color: " + item.getColor()));
        detailsPanel.add(new JLabel("Pattern: " + item.getPattern()));
        detailsPanel.add(new JLabel("Occasions: " + String.join(", ", 
            item.getOccasions().stream()
                .map(Occasion::getDisplayName)
                .toArray(String[]::new))));

        contentPanel.add(detailsPanel, BorderLayout.SOUTH);
        dialog.add(contentPanel);

        // Close button
        AnimatedButton closeButton = new AnimatedButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void refreshWardrobeDisplay() {
        if (tabbedPane != null) {
            createWardrobePanel();
            tabbedPane.setComponentAt(0, wardrobePanel);
            revalidate();
            repaint();
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(240, 240, 240));
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JMenuItem saveItem = new JMenuItem("Save Wardrobe");
        saveItem.addActionListener(e -> saveWardrobe());
        
        JMenuItem loadItem = new JMenuItem("Load Wardrobe");
        loadItem.addActionListener(e -> loadWardrobe());
        
        // Account Menu
        JMenu accountMenu = new JMenu("Account");
        accountMenu.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> handleLogout());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> handleExit());
        
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        accountMenu.add(logoutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(accountMenu);
        setJMenuBar(menuBar);
    }
    
    private void handleExit() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Would you like to save your wardrobe before exiting?",
            "Save Wardrobe",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
            saveWardrobe();
            System.exit(0);
        } else if (response == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
        // If CANCEL_OPTION, do nothing
    }

    private void loadWardrobe() {
        File dataFile = new File(System.getProperty("user.home") + "/wardrobe-manager/wardrobe.dat");
        if (dataFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                clothingItems = (List<ClothingItem>) ois.readObject();
                outfits = (List<Outfit>) ois.readObject();
                refreshWardrobeDisplay();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading wardrobe: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveWardrobe() {
        File dataDir = new File(System.getProperty("user.home") + "/wardrobe-manager");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        File dataFile = new File(dataDir, "wardrobe.dat");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(clothingItems);
            oos.writeObject(outfits);
            JOptionPane.showMessageDialog(this,
                "Wardrobe saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving wardrobe: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createViewWardrobePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(70, 130, 180));
        
        // Add filter components
        toolBar.add(new JLabel("Filter by: "));
        
        JComboBox<ClothingType> typeFilter = new JComboBox<>(ClothingType.getTypesForGender(selectedGender));
        typeFilter.insertItemAt(null, 0);
        typeFilter.setSelectedIndex(0);
        typeFilter.setPreferredSize(new Dimension(150, 25));
        toolBar.add(typeFilter);
        
        toolBar.addSeparator();
        
        JComboBox<String> colorFilter = new JComboBox<>(ColorMatcher.getAllColors().toArray(new String[0]));
        colorFilter.insertItemAt("All Colors", 0);
        colorFilter.setSelectedIndex(0);
        colorFilter.setPreferredSize(new Dimension(150, 25));
        toolBar.add(colorFilter);
        
        AnimatedButton applyFilter = new AnimatedButton("Apply Filter");
        applyFilter.addActionListener(e -> refreshWardrobeView(
            (ClothingType) typeFilter.getSelectedItem(),
            colorFilter.getSelectedIndex() == 0 ? null : (String) colorFilter.getSelectedItem()
        ));
        toolBar.add(applyFilter);
        
        panel.add(toolBar, BorderLayout.NORTH);
        
        // Create scrollable grid for clothing items
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add clothing items
        for (ClothingItem item : clothingItems) {
            if (item.getType().getGender() == selectedGender) {
                gridPanel.add(createClothingItemPanel(item));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createClothingItemPanel(ClothingItem item) {
        AnimatedPanel itemPanel = new AnimatedPanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        itemPanel.setPreferredSize(new Dimension(200, 250));
        
        // Image viewer
        AnimatedImageViewer imageViewer = new AnimatedImageViewer();
        imageViewer.setClothingDetails(item.getType(), item.getColor()); // Set type and color first
        if (item.getImagePath() != null) {
            imageViewer.setImage(item.getImagePath());
        }
        itemPanel.add(imageViewer, BorderLayout.CENTER);
        
        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        detailsPanel.add(nameLabel);
        
        detailsPanel.add(new JLabel(item.getType().getDisplayName()));
        detailsPanel.add(new JLabel("Color: " + item.getColor()));
        
        itemPanel.add(detailsPanel, BorderLayout.SOUTH);
        
        // Add hover effect
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                displayClothingItem(item);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                itemPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            }
        });
        
        return itemPanel;
    }
    
    private void refreshWardrobeView(ClothingType typeFilter, String colorFilter) {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (ClothingItem item : clothingItems) {
            if (item.getType().getGender() == selectedGender) {
                if ((typeFilter == null || item.getType() == typeFilter) &&
                    (colorFilter == null || item.getColor().equals(colorFilter))) {
                    gridPanel.add(createClothingItemPanel(item));
                }
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Update the view wardrobe tab
        if (tabbedPane != null) {
            JPanel viewPanel = new JPanel(new BorderLayout());
            viewPanel.add(((JPanel)tabbedPane.getComponentAt(1)).getComponent(0), BorderLayout.NORTH); // Keep the toolbar
            viewPanel.add(scrollPane, BorderLayout.CENTER);
            tabbedPane.setComponentAt(1, viewPanel);
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set better rendering hints
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            WardrobeManagerGUI gui = new WardrobeManagerGUI();
            if (gui.isUserAuthenticated()) {
                gui.setVisible(true);
            }
        });
    }
} 