package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;

final class AstronautIcon extends JComponent {
    private final Color suit;

    AstronautIcon(String color) {
        suit = Color.decode(color);
        setPreferredSize(new Dimension(112, 132));
    }

    @Override protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = getWidth() / 2;
        g.setColor(new Color(255, 255, 255, 25));
        g.fillOval(x - 43, 4, 86, 86);
        g.setColor(new Color(226, 207, 183));
        g.fillOval(x - 18, 14, 36, 39);
        g.setColor(new Color(139, 204, 229));
        g.fillRoundRect(x - 24, 21, 48, 25, 12, 12);
        g.setColor(suit);
        g.fillRoundRect(x - 30, 56, 60, 59, 20, 20);
        g.fillRoundRect(x - 44, 65, 17, 42, 9, 9);
        g.fillRoundRect(x + 27, 65, 17, 42, 9, 9);
        g.setColor(new Color(217, 230, 239));
        g.fillRoundRect(x - 9, 57, 18, 30, 6, 6);
        g.setColor(new Color(255, 255, 255, 190));
        g.drawOval(x - 15, 9, 30, 30);
        g.dispose();
    }
}
