package com.wardrobemanager.components;

import com.wardrobemanager.model.ClothingType;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class ClothingImageGenerator {
    
    public static BufferedImage generateClothingImage(ClothingType type, String color, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Convert color string to Color object
        Color itemColor = parseColor(color);
        
        // Draw based on clothing type
        switch (type.toString().toUpperCase()) {
            case "SHIRT":
            case "T_SHIRT":
                drawShirt(g2d, width, height, itemColor);
                break;
            case "PANTS":
            case "JEANS":
                drawPants(g2d, width, height, itemColor);
                break;
            case "DRESS":
                drawDress(g2d, width, height, itemColor);
                break;
            case "SKIRT":
                drawSkirt(g2d, width, height, itemColor);
                break;
            case "JACKET":
            case "COAT":
                drawJacket(g2d, width, height, itemColor);
                break;
            default:
                drawGenericItem(g2d, width, height, itemColor);
        }
        
        g2d.dispose();
        return image;
    }
    
    private static void drawShirt(Graphics2D g2d, int width, int height, Color color) {
        // Draw collar
        g2d.setColor(color);
        int collarWidth = width / 3;
        int collarHeight = height / 6;
        g2d.fillArc(width/2 - collarWidth/2, height/6, collarWidth, collarHeight, 0, 180);
        
        // Draw body
        int bodyWidth = width * 2/3;
        int bodyHeight = height * 2/3;
        g2d.fillRoundRect(width/2 - bodyWidth/2, height/4, bodyWidth, bodyHeight, 20, 20);
        
        // Draw sleeves
        int sleeveWidth = width/4;
        int sleeveHeight = height/3;
        g2d.fillRoundRect(width/2 - bodyWidth/2 - sleeveWidth/2, height/3, sleeveWidth, sleeveHeight, 15, 15);
        g2d.fillRoundRect(width/2 + bodyWidth/2 - sleeveWidth/2, height/3, sleeveWidth, sleeveHeight, 15, 15);
        
        // Add details
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(width/2, height/4 + 10, width/2, height/2);
    }
    
    private static void drawPants(Graphics2D g2d, int width, int height, Color color) {
        g2d.setColor(color);
        
        // Draw waistband
        g2d.fillRect(width/4, height/6, width/2, height/10);
        
        // Draw legs
        int legWidth = width/4;
        int legHeight = height * 2/3;
        g2d.fillRoundRect(width/3 - legWidth/2, height/4, legWidth, legHeight, 10, 10);
        g2d.fillRoundRect(2*width/3 - legWidth/2, height/4, legWidth, legHeight, 10, 10);
        
        // Add details
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(width/2, height/6, width/2, height/4);
    }
    
    private static void drawDress(Graphics2D g2d, int width, int height, Color color) {
        g2d.setColor(color);
        
        // Draw top part
        int topWidth = width/2;
        g2d.fillRoundRect(width/2 - topWidth/2, height/6, topWidth, height/3, 20, 20);
        
        // Draw skirt part
        Path2D.Double skirt = new Path2D.Double();
        skirt.moveTo(width/2 - topWidth/2, height/2);
        skirt.lineTo(width/4, height);
        skirt.lineTo(3*width/4, height);
        skirt.lineTo(width/2 + topWidth/2, height/2);
        skirt.closePath();
        g2d.fill(skirt);
        
        // Add details
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(width/2, height/6, width/2, height/2);
    }
    
    private static void drawSkirt(Graphics2D g2d, int width, int height, Color color) {
        g2d.setColor(color);
        
        // Draw waistband
        g2d.fillRect(width/4, height/4, width/2, height/10);
        
        // Draw skirt
        Path2D.Double skirt = new Path2D.Double();
        skirt.moveTo(width/4, height/3);
        skirt.lineTo(width/6, height);
        skirt.lineTo(5*width/6, height);
        skirt.lineTo(3*width/4, height/3);
        skirt.closePath();
        g2d.fill(skirt);
        
        // Add pleats
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(1));
        for (int i = 1; i < 8; i++) {
            int x = width/6 + (i * width/8);
            g2d.drawLine(x, height/3, x, height);
        }
    }
    
    private static void drawJacket(Graphics2D g2d, int width, int height, Color color) {
        g2d.setColor(color);
        
        // Draw body
        int bodyWidth = width * 2/3;
        int bodyHeight = height * 3/4;
        g2d.fillRoundRect(width/2 - bodyWidth/2, height/6, bodyWidth, bodyHeight, 20, 20);
        
        // Draw collar
        g2d.setColor(color.darker());
        int collarWidth = width/4;
        g2d.fillArc(width/2 - collarWidth, height/6, collarWidth, height/4, 0, 90);
        g2d.fillArc(width/2, height/6, collarWidth, height/4, 90, 90);
        
        // Draw sleeves
        g2d.setColor(color);
        int sleeveWidth = width/3;
        int sleeveHeight = height/2;
        g2d.fillRoundRect(width/2 - bodyWidth/2 - sleeveWidth/3, height/4, sleeveWidth, sleeveHeight, 15, 15);
        g2d.fillRoundRect(width/2 + bodyWidth/2 - 2*sleeveWidth/3, height/4, sleeveWidth, sleeveHeight, 15, 15);
    }
    
    private static void drawGenericItem(Graphics2D g2d, int width, int height, Color color) {
        g2d.setColor(color);
        g2d.fillRoundRect(width/4, height/4, width/2, height/2, 20, 20);
        
        // Add some generic details
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(width/4 + 5, height/4 + 5, width/2 - 10, height/2 - 10, 15, 15);
    }
    
    private static Color parseColor(String colorName) {
        switch (colorName.toUpperCase()) {
            case "RED": return new Color(220, 50, 50);
            case "BLUE": return new Color(50, 100, 220);
            case "GREEN": return new Color(50, 180, 50);
            case "YELLOW": return new Color(220, 220, 50);
            case "PURPLE": return new Color(150, 50, 220);
            case "ORANGE": return new Color(220, 150, 50);
            case "PINK": return new Color(255, 182, 193);
            case "BROWN": return new Color(139, 69, 19);
            case "GRAY": return new Color(128, 128, 128);
            case "BLACK": return new Color(30, 30, 30);
            case "WHITE": return Color.WHITE;
            case "NAVY": return new Color(0, 0, 128);
            case "BEIGE": return new Color(245, 245, 220);
            default: return new Color(100, 100, 100);
        }
    }
} 