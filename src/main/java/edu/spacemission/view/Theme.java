package edu.spacemission.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

final class Theme {
    static final Color BG = new Color(8, 15, 35);
    static final Color PANEL = new Color(18, 30, 59);
    static final Color PANEL_LIGHT = new Color(28, 44, 78);
    static final Color TEXT = new Color(235, 242, 255);
    static final Color MUTED = new Color(159, 177, 210);
    static final Color CYAN = new Color(91, 220, 239);
    static final Color GREEN = new Color(99, 224, 169);
    static final Color ORANGE = new Color(255, 177, 103);

    private Theme() { }

    static JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(18, 18));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(26, 34, 28, 34));
        return panel;
    }

    static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 30));
        return label;
    }

    static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        return label;
    }

    static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(TEXT);
        button.setBackground(PANEL_LIGHT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(72, 101, 146), 1, true),
                new EmptyBorder(11, 18, 11, 18)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(42, 62, 100), 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        return panel;
    }

    static JLabel body(String text) {
        JLabel label = new JLabel("<html><div style='width:270px'>" + text + "</div></html>");
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }
}
