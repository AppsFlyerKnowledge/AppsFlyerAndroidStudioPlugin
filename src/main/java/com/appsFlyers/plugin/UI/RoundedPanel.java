package com.appsFlyers.plugin.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// מחלקה פנימית לציור פאנל מעוגל

public class RoundedPanel extends JPanel {
    private static final int MIN_PANEL_HEIGHT = 60;

    private final int cornerRadius;
    private final Color backgroundColor;

    public RoundedPanel(int cornerRadius, Color backgroundColor) {
        this.cornerRadius = cornerRadius;
        this.backgroundColor = backgroundColor;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        g2.setColor(backgroundColor);
        g2.fill(round);
        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width, Math.max(MIN_PANEL_HEIGHT, size.height));
    }
}
