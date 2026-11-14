package edu.spacemission.view;

import edu.spacemission.model.Ali;
import edu.spacemission.model.Alicia;
import edu.spacemission.model.Astronaut;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public final class CharacterSelection extends JPanel {
    public CharacterSelection(String playerName, Consumer<Astronaut> selected, Runnable back) {
        setLayout(new BorderLayout(18, 18));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(30, 38, 30, 38));
        JLabel heading = Theme.title("Choose your astronaut");
        JLabel info = Theme.subtitle("Welcome aboard, " + playerName + ". Select a crew member for your mission.");
        JPanel top = new JPanel(new GridLayout(2, 1, 0, 7));
        top.setOpaque(false);
        top.add(heading);
        top.add(info);
        add(top, BorderLayout.NORTH);
        JPanel choices = new JPanel(new GridLayout(1, 2, 20, 0));
        choices.setOpaque(false);
        choices.add(characterCard(new Ali(), selected));
        choices.add(characterCard(new Alicia(), selected));
        add(choices, BorderLayout.CENTER);
        JButton backButton = Theme.button("Back to menu");
        backButton.addActionListener(event -> back.run());
        add(backButton, BorderLayout.SOUTH);
    }

    private JPanel characterCard(Astronaut astronaut, Consumer<Astronaut> selected) {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        AstronautIcon illustration = new AstronautIcon(astronaut.getSuitColor());
        illustration.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel name = Theme.title(astronaut.getName());
        name.setFont(new Font("SansSerif", Font.BOLD, 25));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel description = Theme.body(astronaut.getDescription());
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton choose = Theme.button("Select " + astronaut.getName());
        choose.setAlignmentX(Component.CENTER_ALIGNMENT);
        choose.addActionListener(event -> selected.accept(astronaut));
        card.add(Box.createVerticalGlue());
        card.add(illustration);
        card.add(Box.createVerticalStrut(8));
        card.add(name);
        card.add(Box.createVerticalStrut(8));
        card.add(description);
        card.add(Box.createVerticalStrut(20));
        card.add(choose);
        card.add(Box.createVerticalGlue());
        return card;
    }
}
