package com.wardrobemanager.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class AnimatedButton extends JButton {
    private float alpha = 0.5f;
    private Color hoverColor = new Color(70, 130, 180, 60);
    private boolean mouseOver = false;
    private float rippleSize = 0f;
    private Point ripplePoint;
    private Timer rippleTimer;
    private Timer fadeTimer;

    public AnimatedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
        setForeground(new Color(50, 50, 50));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                startFadeTimer(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                startFadeTimer(false);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                ripplePoint = e.getPoint();
                rippleSize = 0f;
                if (rippleTimer != null && rippleTimer.isRunning()) {
                    rippleTimer.stop();
                }
                rippleTimer = new Timer(20, evt -> {
                    rippleSize += 5f;
                    if (rippleSize > getWidth()) {
                        rippleTimer.stop();
                    }
                    repaint();
                });
                rippleTimer.start();
            }
        });
    }

    private void startFadeTimer(boolean fadeIn) {
        if (fadeTimer != null && fadeTimer.isRunning()) {
            fadeTimer.stop();
        }
        fadeTimer = new Timer(20, evt -> {
            if (fadeIn) {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadeTimer.stop();
                }
            } else {
                alpha -= 0.05f;
                if (alpha <= 0.5f) {
                    alpha = 0.5f;
                    fadeTimer.stop();
                }
            }
            repaint();
        });
        fadeTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background
        g2.setColor(new Color(240, 240, 240));
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fill(roundedRectangle);

        // Draw hover effect
        if (mouseOver) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(hoverColor);
            g2.fill(roundedRectangle);
        }

        // Draw ripple effect
        if (ripplePoint != null) {
            g2.setColor(new Color(255, 255, 255, 50));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            float rippleDiameter = rippleSize * 2;
            g2.fill(new RoundRectangle2D.Float(
                ripplePoint.x - rippleSize,
                ripplePoint.y - rippleSize,
                rippleDiameter,
                rippleDiameter,
                rippleDiameter,
                rippleDiameter
            ));
        }

        g2.dispose();
        super.paintComponent(g);
    }
} 