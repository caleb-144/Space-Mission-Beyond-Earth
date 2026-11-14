package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;

public final class HowToPlayView extends JPanel {
    public HowToPlayView(Runnable back) {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(34, 44, 34, 44));
        add(Theme.title("How to Play"), BorderLayout.NORTH);
        JTextArea instructions = new JTextArea("""
                YOUR TRAINING PATH

                1. Choose Ali or Alicia. Your astronaut and mission progress are saved on this computer.
                2. Complete both Basic Training missions to unlock spacecraft operations.
                3. Replay any unlocked mission to practice. Your best score for each mission is kept.

                ZERO-GRAVITY TRAINING
                Use the arrow keys or WASD to move. Collect all three training beacons, then reach the green airlock.
                Plan your route around the floating debris.

                SPACE KNOWLEDGE ASSESSMENT
                Select one of four answers. You will see an explanation after every answer.

                SPACEWALK & SATELLITE REPAIR
                Complete each repair before oxygen runs out. Each repair and the oxygen remaining contribute to your score.

                EMERGENCY SURVIVAL
                Read each spacecraft scenario and select the safest procedure. Explanations reinforce the correct response.

                Your total score and mission completion status are saved automatically.
                """);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        instructions.setBackground(Theme.PANEL);
        instructions.setForeground(Theme.TEXT);
        instructions.setFont(new Font("SansSerif", Font.PLAIN, 15));
        instructions.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(instructions, BorderLayout.CENTER);
        JButton backButton = Theme.button("Back");
        backButton.addActionListener(event -> back.run());
        add(backButton, BorderLayout.SOUTH);
    }
}
