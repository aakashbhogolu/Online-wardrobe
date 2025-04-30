package com.wardrobemanager.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import com.wardrobemanager.model.ClothingType;

public class AnimatedImageViewer extends JPanel {
    private BufferedImage image;
    private float scale = 1.0f;
    private float targetScale = 1.0f;
    private float rotation = 0.0f;
    private Timer animationTimer;
    private boolean isHovered = false;
    private float alpha = 1.0f;
    private ClothingType clothingType;
    private String clothingColor;

    public AnimatedImageViewer() {
        setPreferredSize(new Dimension(200, 200));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                targetScale = 1.1f;
                startAnimation();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                targetScale = 1.0f;
                startAnimation();
            }
        });
    }

    public void setImage(String imagePath) {
        try {
            this.image = ImageIO.read(new File(imagePath));
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
            // If image loading fails, we'll use the generated image if type and color are set
            if (clothingType != null && clothingColor != null) {
                this.image = ClothingImageGenerator.generateClothingImage(
                    clothingType, clothingColor, getWidth(), getHeight());
            }
        }
    }

    public void setClothingDetails(ClothingType type, String color) {
        this.clothingType = type;
        this.clothingColor = color;
        if (image == null) {
            // Use default size if component size is not yet set
            int width = getWidth() > 0 ? getWidth() : 200;
            int height = getHeight() > 0 ? getHeight() : 200;
            this.image = ClothingImageGenerator.generateClothingImage(
                type, color, width, height);
            repaint();
        }
    }

    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(16, e -> {
            // Update scale with smooth animation
            float scaleDiff = targetScale - scale;
            if (Math.abs(scaleDiff) > 0.001f) {
                scale += scaleDiff * 0.1f;
            }

            // Update rotation
            if (isHovered) {
                rotation += 0.02f;
            } else {
                rotation = 0.0f;
            }

            // Update alpha for fade effect
            if (isHovered) {
                alpha = Math.min(1.0f, alpha + 0.1f);
            } else {
                alpha = Math.max(0.7f, alpha - 0.1f);
            }

            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // If no image is set but we have clothing details, generate one
        if (image == null && clothingType != null && clothingColor != null) {
            // Use default size if component size is not yet set
            int width = getWidth() > 0 ? getWidth() : 200;
            int height = getHeight() > 0 ? getHeight() : 200;
            image = ClothingImageGenerator.generateClothingImage(
                clothingType, clothingColor, width, height);
        }
        
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Calculate scaled dimensions while maintaining aspect ratio
        double imgAspect = (double) image.getWidth() / image.getHeight();
        double panelAspect = (double) getWidth() / getHeight();
        int scaledWidth, scaledHeight;

        if (imgAspect > panelAspect) {
            scaledWidth = getWidth();
            scaledHeight = (int) (scaledWidth / imgAspect);
        } else {
            scaledHeight = getHeight();
            scaledWidth = (int) (scaledHeight * imgAspect);
        }

        // Create transform for rotation and scaling
        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(rotation);
        transform.scale(scale, scale);
        transform.translate(-scaledWidth/2, -scaledHeight/2);

        // Set transparency
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Draw image
        g2.drawImage(image, transform, null);

        // Add hover effect
        if (isHovered) {
            g2.setColor(new Color(70, 130, 180, 50));
            g2.fill(new Rectangle(0, 0, getWidth(), getHeight()));
        }

        g2.dispose();
    }
} 