package edu.spacemission.view;

import edu.spacemission.model.MissionProgress;
import edu.spacemission.model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.IntConsumer;

public final class LevelSelection extends JPanel {
    public LevelSelection(Player player, List<MissionProgress> progress, boolean levelTwoUnlocked,
                          IntConsumer playMission, Runnable changeCharacter, Runnable menu) {
        setLayout(new BorderLayout(18, 18));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(23, 30, 24, 30));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);
        titles.add(Theme.title("Mission Control"));
        titles.add(Theme.subtitle(player.playerName() + "  •  " + player.characterName()
                + "  •  " + player.totalScore() + " points"));
        heading.add(titles, BorderLayout.WEST);
        JButton character = Theme.button("Change astronaut");
        character.addActionListener(event -> changeCharacter.run());
        heading.add(character, BorderLayout.EAST);
        add(heading, BorderLayout.NORTH);

        JPanel levels = new JPanel(new GridLayout(2, 1, 0, 14));
        levels.setOpaque(false);
        levels.add(levelCard("LEVEL 1  /  BASIC TRAINING", "Master movement in microgravity and pass your knowledge check.",
                progress.subList(0, Math.min(2, progress.size())), true, playMission));
        levels.add(levelCard("LEVEL 2  /  SPACECRAFT OPERATIONS", levelTwoUnlocked
                        ? "Unlocked — complete a spacewalk repair and respond to emergencies."
                        : "Locked — complete both Level 1 missions to qualify.",
                progress.size() > 2 ? progress.subList(2, progress.size()) : List.of(),
                levelTwoUnlocked, playMission));
        add(levels, BorderLayout.CENTER);
        JButton back = Theme.button("Main menu");
        back.addActionListener(event -> menu.run());
        add(back, BorderLayout.SOUTH);
    }

    private JPanel levelCard(String title, String description, List<MissionProgress> missions,
                             boolean unlocked, IntConsumer playMission) {
        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(12, 9));
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 5));
        info.setOpaque(false);
        JLabel name = Theme.title(title);
        name.setFont(new Font("SansSerif", Font.BOLD, 19));
        info.add(name);
        info.add(Theme.subtitle(description));
        card.add(info, BorderLayout.NORTH);
        JPanel missionList = new JPanel(new GridLayout(1, 2, 12, 0));
        missionList.setOpaque(false);
        for (MissionProgress mission : missions) {
            JPanel missionCard = new JPanel(new BorderLayout(8, 7));
            missionCard.setOpaque(false);
            String status = mission.completed() ? "COMPLETE  •  " + mission.score() + " pts" : "NOT COMPLETE";
            JLabel label = new JLabel("<html><b>" + mission.missionName() + "</b><br><span style='color:#9fb1d2'>"
                    + status + "</span></html>");
            label.setForeground(Theme.TEXT);
            missionCard.add(label, BorderLayout.CENTER);
            JButton play = Theme.button(mission.completed() ? "Replay" : "Play mission");
            play.setEnabled(unlocked);
            play.addActionListener(event -> playMission.accept(mission.missionId()));
            missionCard.add(play, BorderLayout.EAST);
            missionList.add(missionCard);
        }
        card.add(missionList, BorderLayout.CENTER);
        return card;
    }
}
