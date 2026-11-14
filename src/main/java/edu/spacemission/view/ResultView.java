package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;

public final class ResultView extends JPanel {
    public ResultView(String mission, int score, int previousBest, Runnable replay, Runnable missions) {
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel overline = new JLabel("MISSION DEBRIEF");
        overline.setForeground(Theme.CYAN);
        overline.setFont(new Font("SansSerif", Font.BOLD, 13));
        overline.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel heading = Theme.title("Mission complete");
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel name = Theme.subtitle(mission);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel points = Theme.title(score + " / 100");
        points.setForeground(score >= 70 ? Theme.GREEN : Theme.ORANGE);
        points.setFont(new Font("SansSerif", Font.BOLD, 44));
        points.setAlignmentX(Component.CENTER_ALIGNMENT);
        String feedback = score >= 85 ? "Excellent work, mission specialist!"
                : score >= 60 ? "Good progress. Keep building your skills."
                : "Training complete. Replay to improve your score.";
        JLabel performance = Theme.subtitle(feedback);
        performance.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel best = Theme.subtitle("Previous best: " + previousBest + " points  •  Best score is saved automatically.");
        best.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton replayButton = Theme.button("Replay mission");
        replayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        replayButton.addActionListener(event -> replay.run());
        JButton missionsButton = Theme.button("Back to mission control");
        missionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        missionsButton.addActionListener(event -> missions.run());
        card.add(Box.createVerticalGlue());
        card.add(overline);
        card.add(Box.createVerticalStrut(12));
        card.add(heading);
        card.add(Box.createVerticalStrut(7));
        card.add(name);
        card.add(Box.createVerticalStrut(20));
        card.add(points);
        card.add(Box.createVerticalStrut(8));
        card.add(performance);
        card.add(Box.createVerticalStrut(8));
        card.add(best);
        card.add(Box.createVerticalStrut(20));
        card.add(replayButton);
        card.add(Box.createVerticalStrut(10));
        card.add(missionsButton);
        card.add(Box.createVerticalGlue());
        card.setPreferredSize(new Dimension(560, 440));
        add(card);
    }
}
