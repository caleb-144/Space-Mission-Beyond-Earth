package edu.spacemission.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntConsumer;

public final class ZeroGravityTrainingView extends JPanel {
    private final Set<Point> collected = new HashSet<>();
    private final Runnable back;
    private final IntConsumer finished;
    private final JLabel status = new JLabel();
    private final TrainingBoard board = new TrainingBoard();
    private int x = 0, y = 0, moves = 0;
    private boolean complete;
    private static final Point[] BEACONS = {new Point(2, 0), new Point(5, 2), new Point(1, 4)};
    private static final Point AIRLOCK = new Point(7, 4);
    private static final Set<Point> DEBRIS = Set.of(new Point(3, 1), new Point(3, 2), new Point(6, 3));

    public ZeroGravityTrainingView(Runnable back, IntConsumer finished) {
        this.back = back;
        this.finished = finished;
        setLayout(new BorderLayout(12, 12));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(20, 28, 20, 28));
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 4));
        titles.setOpaque(false);
        titles.add(Theme.title("Zero-Gravity Training"));
        titles.add(Theme.subtitle("Collect all 3 glowing beacons, avoid debris, then reach the green airlock."));
        header.add(titles, BorderLayout.CENTER);
        JButton menu = Theme.button("Leave mission");
        menu.addActionListener(event -> back.run());
        header.add(menu, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        board.setPreferredSize(new Dimension(560, 365));
        board.setMinimumSize(new Dimension(400, 280));
        add(board, BorderLayout.CENTER);
        status.setForeground(Theme.CYAN);
        status.setFont(new Font("SansSerif", Font.BOLD, 15));
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.add(status, BorderLayout.CENTER);
        footer.add(Theme.subtitle("Move: arrow keys or W A S D"), BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);
        updateStatus();
        bindMove("UP", 0, -1);
        bindMove("DOWN", 0, 1);
        bindMove("LEFT", -1, 0);
        bindMove("RIGHT", 1, 0);
        bindMove("W", 0, -1);
        bindMove("S", 0, 1);
        bindMove("A", -1, 0);
        bindMove("D", 1, 0);
        SwingUtilities.invokeLater(board::requestFocusInWindow);
    }

    private void bindMove(String key, int dx, int dy) {
        String action = "move-" + key;
        board.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), action);
        board.getActionMap().put(action, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { moveAstronaut(dx, dy); }
        });
    }

    private void moveAstronaut(int dx, int dy) {
        if (complete) return;
        int nextX = x + dx, nextY = y + dy;
        if (nextX < 0 || nextX > 7 || nextY < 0 || nextY > 4 || DEBRIS.contains(new Point(nextX, nextY))) return;
        x = nextX;
        y = nextY;
        moves++;
        for (Point beacon : BEACONS) {
            if (beacon.equals(new Point(x, y))) collected.add(beacon);
        }
        board.repaint();
        updateStatus();
        if (collected.size() == BEACONS.length && AIRLOCK.equals(new Point(x, y))) {
            complete = true;
            int score = Math.max(50, 100 - Math.max(0, moves - 12) * 2);
            status.setText("Training complete! Route score: " + score + " / 100");
            Timer timer = new Timer(900, event -> finished.accept(score));
            timer.setRepeats(false);
            timer.start();
        }
    }

    private void updateStatus() {
        status.setText("Beacons: " + collected.size() + " / 3     •     Moves: " + moves
                + (collected.size() == 3 ? "     •     Airlock is ready!" : ""));
    }

    private final class TrainingBoard extends JPanel {
        TrainingBoard() {
            setBackground(new Color(10, 22, 48));
            setFocusable(true);
            setBorder(BorderFactory.createLineBorder(new Color(53, 79, 125), 1));
        }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cell = Math.min(getWidth() / 8, getHeight() / 5);
            int ox = (getWidth() - cell * 8) / 2, oy = (getHeight() - cell * 5) / 2;
            g.setColor(new Color(38, 57, 91));
            for (int col = 0; col <= 8; col++) g.drawLine(ox + col * cell, oy, ox + col * cell, oy + cell * 5);
            for (int row = 0; row <= 5; row++) g.drawLine(ox, oy + row * cell, ox + cell * 8, oy + row * cell);
            for (int col = 0; col < 8; col++) {
                for (int row = 0; row < 5; row++) {
                    if (((col * 5 + row) % 7) == 0) {
                        g.setColor(new Color(170, 197, 235, 105));
                        g.fillOval(ox + col * cell + cell / 2, oy + row * cell + cell / 2, 2, 2);
                    }
                }
            }
            for (Point debris : DEBRIS) {
                int px = ox + debris.x * cell, py = oy + debris.y * cell;
                g.setColor(new Color(255, 166, 112));
                g.fillOval(px + cell / 4, py + cell / 4, cell / 2, cell / 2);
                g.setColor(new Color(255, 219, 180));
                g.drawOval(px + cell / 4, py + cell / 4, cell / 2, cell / 2);
            }
            for (Point beacon : BEACONS) {
                if (!collected.contains(beacon)) {
                    int px = ox + beacon.x * cell, py = oy + beacon.y * cell;
                    g.setColor(Theme.CYAN);
                    g.fillOval(px + cell / 3, py + cell / 3, cell / 3, cell / 3);
                    g.setColor(new Color(91, 220, 239, 70));
                    g.fillOval(px + cell / 5, py + cell / 5, cell * 3 / 5, cell * 3 / 5);
                }
            }
            int gx = ox + AIRLOCK.x * cell, gy = oy + AIRLOCK.y * cell;
            g.setColor(new Color(99, 224, 169, collected.size() == 3 ? 255 : 90));
            g.fillRoundRect(gx + cell / 5, gy + cell / 5, cell * 3 / 5, cell * 3 / 5, 10, 10);
            int ax = ox + x * cell, ay = oy + y * cell;
            g.setColor(new Color(235, 242, 255));
            g.fillOval(ax + cell / 4, ay + cell / 5, cell / 2, cell / 2);
            g.setColor(Theme.CYAN);
            g.fillRoundRect(ax + cell / 4, ay + cell / 2, cell / 2, cell / 3, 8, 8);
            g.dispose();
        }
    }
}
