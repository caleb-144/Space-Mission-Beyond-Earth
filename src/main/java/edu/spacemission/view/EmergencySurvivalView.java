package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public final class EmergencySurvivalView extends JPanel {
    private record Scenario(String title, String prompt, String[] choices, int correct, String explanation) { }
    private static final Scenario[] SCENARIOS = {
            new Scenario("Oxygen system warning", "The cabin oxygen reading is falling. What should you do first?",
                    new String[]{"Ignore it; the sensor is probably wrong", "Notify the crew, check the backup supply, and follow the checklist", "Open the hatch to vent the cabin", "Turn off all communications"}, 1,
                    "Alerting the crew and following the emergency checklist helps verify the issue and safely switch to backup life support."),
            new Scenario("Electrical power fault", "A critical power circuit has failed. Which action is safest?",
                    new String[]{"Isolate the affected circuit and use the approved power-down procedure", "Reconnect wires while the circuit is live", "Disable every warning alarm", "Leave the spacecraft immediately"}, 0,
                    "Isolate the fault using the spacecraft checklist, then preserve power for essential life-support and communications."),
            new Scenario("Equipment malfunction", "A life-support fan stops working. What is the best response?",
                    new String[]{"Strike the unit until it starts", "Report the fault and use the documented backup procedure", "Remove the filter and discard it", "Wait until the next day to mention it"}, 1,
                    "Report equipment problems promptly and use documented backup procedures; improvised repairs can make a hazard worse.")
    };
    private final Runnable back;
    private final IntConsumer finished;
    private final JLabel progress = Theme.subtitle("");
    private final JLabel prompt = new JLabel();
    private final JLabel feedback = new JLabel(" ");
    private final JButton[] options = new JButton[4];
    private final JButton next = Theme.button("Continue");
    private int index;
    private int correct;

    public EmergencySurvivalView(Runnable back, IntConsumer finished) {
        this.back = back;
        this.finished = finished;
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(27, 38, 27, 38));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 5));
        titles.setOpaque(false);
        titles.add(Theme.title("Emergency Survival Simulation"));
        titles.add(progress);
        header.add(titles, BorderLayout.CENTER);
        JButton exit = Theme.button("Leave mission");
        exit.addActionListener(event -> back.run());
        header.add(exit, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(18, 18));
        prompt.setForeground(Theme.TEXT);
        prompt.setFont(new Font("SansSerif", Font.BOLD, 22));
        card.add(prompt, BorderLayout.NORTH);
        JPanel buttons = new JPanel(new GridLayout(2, 2, 12, 12));
        buttons.setOpaque(false);
        for (int i = 0; i < options.length; i++) {
            final int choice = i;
            options[i] = Theme.button("");
            options[i].setHorizontalAlignment(SwingConstants.LEFT);
            options[i].addActionListener(event -> answer(choice));
            buttons.add(options[i]);
        }
        card.add(buttons, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(12, 0));
        bottom.setOpaque(false);
        feedback.setForeground(Theme.MUTED);
        bottom.add(feedback, BorderLayout.CENTER);
        next.setVisible(false);
        next.addActionListener(event -> advance());
        bottom.add(next, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);
        showScenario();
    }

    private void showScenario() {
        Scenario scenario = SCENARIOS[index];
        progress.setText("EMERGENCY " + (index + 1) + " OF " + SCENARIOS.length);
        prompt.setText("<html><div style='width:680px'>" + scenario.title() + "<br><span style='font-size:15px;color:#9fb1d2'>"
                + scenario.prompt() + "</span></div></html>");
        for (int i = 0; i < 4; i++) {
            options[i].setText((char) ('A' + i) + "    " + scenario.choices()[i]);
            options[i].setEnabled(true);
        }
        feedback.setText("Select the safest procedure.");
        feedback.setForeground(Theme.MUTED);
        next.setVisible(false);
    }

    private void answer(int selected) {
        Scenario scenario = SCENARIOS[index];
        boolean isCorrect = selected == scenario.correct();
        if (isCorrect) correct++;
        for (JButton option : options) option.setEnabled(false);
        feedback.setText("<html>" + (isCorrect ? "Correct response. " : "Review this response. ") + scenario.explanation() + "</html>");
        feedback.setForeground(isCorrect ? Theme.GREEN : Theme.ORANGE);
        next.setText(index == SCENARIOS.length - 1 ? "See results" : "Next scenario");
        next.setVisible(true);
    }

    private void advance() {
        if (++index < SCENARIOS.length) showScenario();
        else finished.accept(Math.round(correct * 100f / SCENARIOS.length));
    }
}
