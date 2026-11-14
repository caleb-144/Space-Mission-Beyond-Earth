package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;

public final class MainMenu extends StarfieldPanel {
    public MainMenu(Runnable start, Runnable continueGame, Runnable howToPlay, Runnable exit, boolean hasSave) {
        setLayout(new GridBagLayout());
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        JLabel kicker = new JLabel("ASTRONAUT TRAINING PROGRAM");
        kicker.setForeground(Theme.CYAN);
        kicker.setFont(new Font("SansSerif", Font.BOLD, 13));
        kicker.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = new JLabel("<html><div style='text-align:center'>SPACE MISSION<br><span style='color:#5bdcef'>BEYOND EARTH</span></div></html>");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 37));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("Your journey from trainee to mission specialist begins here.");
        subtitle.setForeground(Theme.MUTED);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(kicker);
        content.add(Box.createVerticalStrut(18));
        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(32));
        addMenuButton(content, "Start Game", start);
        JButton continueButton = Theme.button("Continue Game");
        continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        continueButton.setEnabled(hasSave);
        continueButton.addActionListener(event -> continueGame.run());
        content.add(continueButton);
        addMenuButton(content, "How to Play", howToPlay);
        addMenuButton(content, "Exit", exit);
        add(content);
    }

    private void addMenuButton(JPanel content, String text, Runnable action) {
        JButton button = Theme.button(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(240, 48));
        button.addActionListener(event -> action.run());
        content.add(button);
        content.add(Box.createVerticalStrut(12));
    }
}
