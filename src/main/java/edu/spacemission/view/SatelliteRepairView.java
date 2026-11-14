package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;

public final class SatelliteRepairView extends JPanel {
    private static final String[] TASKS = {
            "Inspect the damaged solar array",
            "Secure the loose power connector",
            "Deploy the replacement antenna"
    };
    private final Runnable back;
    private final IntConsumer finished;
    private final JLabel oxygenLabel = new JLabel();
    private final JLabel taskLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel feedback = new JLabel(" ");
    private final JButton repair = Theme.button("Complete repair task");
    private int task;
    private int oxygen = 100;
    private Timer timer;
    private boolean ended;

    public SatelliteRepairView(Runnable back, IntConsumer finished) {
        this.back = back;
        this.finished = finished;
        setLayout(new BorderLayout(18, 18));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(25, 38, 25, 38));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel headings = new JPanel(new GridLayout(2, 1, 0, 5));
        headings.setOpaque(false);
        headings.add(Theme.title("Spacewalk & Satellite Repair"));
        headings.add(Theme.subtitle("Work carefully outside the spacecraft. Oxygen is limited."));
        header.add(headings, BorderLayout.CENTER);
        JButton exit = Theme.button("Leave mission");
        exit.addActionListener(event -> leave());
        header.add(exit, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel scene = new StarfieldPanel();
        scene.setLayout(new GridBagLayout());
        JPanel sceneContent = new JPanel();
        sceneContent.setOpaque(false);
        sceneContent.setLayout(new BoxLayout(sceneContent, BoxLayout.Y_AXIS));
        JLabel satellite = new JLabel("       .  ✦  .       ", SwingConstants.CENTER);
        satellite.setForeground(Theme.CYAN);
        satellite.setFont(new Font("SansSerif", Font.BOLD, 34));
        satellite.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel craft = new JLabel("  ┌─────────────────────────────┐", SwingConstants.CENTER);
        craft.setForeground(new Color(172, 190, 219));
        craft.setFont(new Font("Monospaced", Font.BOLD, 21));
        craft.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel astronaut = new JLabel("        ◉");
        astronaut.setForeground(Theme.ORANGE);
        astronaut.setFont(new Font("SansSerif", Font.BOLD, 38));
        astronaut.setAlignmentX(Component.CENTER_ALIGNMENT);
        sceneContent.add(satellite);
        sceneContent.add(Box.createVerticalStrut(26));
        sceneContent.add(craft);
        sceneContent.add(astronaut);
        scene.add(sceneContent);
        add(scene, BorderLayout.CENTER);

        JPanel lower = new JPanel(new GridLayout(3, 1, 0, 9));
        lower.setOpaque(false);
        oxygenLabel.setForeground(Theme.CYAN);
        oxygenLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        lower.add(oxygenLabel);
        taskLabel.setForeground(Theme.TEXT);
        taskLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        lower.add(taskLabel);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        actions.setOpaque(false);
        feedback.setForeground(Theme.MUTED);
        actions.add(feedback);
        repair.addActionListener(event -> doRepair());
        actions.add(repair);
        lower.add(actions);
        add(lower, BorderLayout.SOUTH);
        updateDisplay();
        timer = new Timer(1000, event -> {
            oxygen = Math.max(0, oxygen - 1);
            updateDisplay();
            if (oxygen == 0) fail();
        });
        timer.start();
    }

    private void doRepair() {
        if (ended) return;
        oxygen = Math.max(0, oxygen - 5);
        feedback.setForeground(Theme.GREEN);
        feedback.setText("Repair complete!");
        task++;
        if (task == TASKS.length) {
            ended = true;
            timer.stop();
            repair.setEnabled(false);
            int score = Math.min(100, 60 + oxygen / 3);
            taskLabel.setText("All satellite systems restored!");
            feedback.setText("Mission complete! Oxygen bonus: " + (oxygen / 3) + " points.");
            Timer finishTimer = new Timer(1000, event -> finished.accept(score));
            finishTimer.setRepeats(false);
            finishTimer.start();
        } else {
            updateDisplay();
        }
    }

    private void updateDisplay() {
        oxygenLabel.setText("OXYGEN  " + oxygen + "%");
        oxygenLabel.setForeground(oxygen < 25 ? Theme.ORANGE : Theme.CYAN);
        taskLabel.setText(task < TASKS.length ? "REPAIR " + (task + 1) + " OF 3  •  " + TASKS[task] : taskLabel.getText());
    }

    private void fail() {
        ended = true;
        timer.stop();
        taskLabel.setText("Oxygen depleted — return to the airlock and try again.");
        feedback.setForeground(Theme.ORANGE);
        feedback.setText("No mission score saved. Replay from mission control.");
        repair.setText("Return to mission control");
        repair.addActionListener(event -> back.run());
    }

    private void leave() {
        if (timer != null) timer.stop();
        back.run();
    }
}
