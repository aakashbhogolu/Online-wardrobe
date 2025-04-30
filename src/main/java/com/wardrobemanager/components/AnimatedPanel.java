package com.wardrobemanager.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AnimatedPanel extends JPanel {
    private float alpha = 0f;
    private Timer fadeTimer;
    private Color gradientStart = new Color(240, 248, 255);
    private Color gradientEnd = new Color(230, 240, 250);

    public AnimatedPanel() {
        setOpaque(false);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                startFadeIn();
            }
        });
    }

    private void startFadeIn() {
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }
        
        fadeTimer = new Timer(50, e -> {
            alpha += 0.1f;
            if (alpha >= 1f) {
                alpha = 1f;
                fadeTimer.stop();
            }
            repaint();
        });
        fadeTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Create gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, gradientStart,
            0, getHeight(), gradientEnd
        );
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Add subtle shadow border
        g2.setColor(new Color(0, 0, 0, 20));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        g2.dispose();
        super.paintComponent(g);
    }

    public void setGradientColors(Color start, Color end) {
        this.gradientStart = start;
        this.gradientEnd = end;
        repaint();
    }
} 