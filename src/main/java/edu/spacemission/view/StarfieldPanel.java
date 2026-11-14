package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;

class StarfieldPanel extends JPanel {
    StarfieldPanel() {
        setOpaque(true);
        setBackground(Theme.BG);
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(new Color(210, 230, 255, 105));
        int width = getWidth();
        int height = getHeight();
        for (int i = 0; i < 75; i++) {
            int x = (i * 127 + 31) % Math.max(width, 1);
            int y = (i * 83 + 17) % Math.max(height, 1);
            int size = i % 11 == 0 ? 3 : 1;
            g.fillOval(x, y, size, size);
        }
        g.dispose();
    }
}
