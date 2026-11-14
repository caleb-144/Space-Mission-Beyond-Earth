package edu.spacemission.controller;

import edu.spacemission.database.PlayerDAO;
import edu.spacemission.database.QuestionDAO;
import edu.spacemission.model.Astronaut;
import edu.spacemission.model.MissionProgress;
import edu.spacemission.model.Player;
import edu.spacemission.service.GameService;
import edu.spacemission.view.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class GameController {
    private static final String MENU = "menu";
    private final JFrame frame = new JFrame("Space Mission: Beyond Earth");
    private final JPanel screen = new JPanel(new CardLayout());
    private final GameService gameService = new GameService();
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private Player player;
    private int screenNumber;
    private boolean hasSavedPlayer;

    public GameController() throws SQLException {
        Optional<Player> latest = playerDAO.latest();
        hasSavedPlayer = latest.isPresent();
        player = latest.orElse(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(820, 610));
        frame.setSize(1000, 740);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(screen);
        showMainMenu();
    }

    public void showWindow() {
        frame.setVisible(true);
    }

    private void showMainMenu() {
        show(MENU, new MainMenu(this::startNewGame, this::continueGame, this::showHowToPlay,
                () -> frame.dispose(), hasSavedPlayer));
    }

    private void startNewGame() {
        JTextField nameField = new JTextField();
        nameField.setColumns(22);
        int answer = JOptionPane.showConfirmDialog(frame, nameField, "Enter your astronaut trainee name",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (answer != JOptionPane.OK_OPTION) return;
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter a name to begin your training.");
            return;
        }
        try {
            player = gameService.newPlayer(name, "Ali");
            hasSavedPlayer = true;
            showCharacterSelection();
        } catch (SQLException exception) {
            showError("Could not save your player profile: " + exception.getMessage());
        }
    }

    private void continueGame() {
        try {
            Optional<Player> latest = playerDAO.latest();
            if (latest.isPresent()) {
                player = latest.get();
                showLevelSelection();
            } else {
                hasSavedPlayer = false;
                showMainMenu();
            }
        } catch (SQLException exception) {
            showError("Could not load your saved game: " + exception.getMessage());
        }
    }

    private void showCharacterSelection() {
        if (player == null) return;
        show("character-" + nextScreen(), new CharacterSelection(player.playerName(), this::selectCharacter, this::showMainMenu));
    }

    private void selectCharacter(Astronaut astronaut) {
        try {
            gameService.selectCharacter(player, astronaut.getName());
            player = new Player(player.id(), player.playerName(), astronaut.getName(), player.totalScore());
            showLevelSelection();
        } catch (SQLException exception) {
            showError("Could not save your astronaut selection: " + exception.getMessage());
        }
    }

    private void showLevelSelection() {
        if (player == null) {
            showMainMenu();
            return;
        }
        try {
            List<MissionProgress> progress = gameService.progress(player);
            boolean unlocked = progress.size() >= 2 && progress.get(0).completed() && progress.get(1).completed();
            show("levels-" + nextScreen(), new LevelSelection(player, progress, unlocked,
                    this::launchMission, this::showCharacterSelection, this::showMainMenu));
        } catch (SQLException exception) {
            showError("Could not load mission progress: " + exception.getMessage());
        }
    }

    private void launchMission(int missionId) {
        if (player == null) return;
        try {
            switch (missionId) {
                case 1 -> show("mission-1-" + nextScreen(), new ZeroGravityTrainingView(this::showLevelSelection,
                        score -> completeMission(1, score)));
                case 2 -> {
                    var questions = questionDAO.forMission(2);
                    if (questions.isEmpty()) throw new SQLException("No assessment questions are available.");
                    show("mission-2-" + nextScreen(), new SpaceKnowledgeAssessmentView(questions,
                            this::showLevelSelection, score -> completeMission(2, score)));
                }
                case 3, 4 -> {
                    if (!gameService.levelTwoUnlocked(player)) {
                        showError("Complete both Basic Training missions to unlock Level 2.");
                        showLevelSelection();
                        return;
                    }
                    if (missionId == 3) {
                        show("mission-3-" + nextScreen(), new SatelliteRepairView(this::showLevelSelection,
                                score -> completeMission(3, score)));
                    } else {
                        show("mission-4-" + nextScreen(), new EmergencySurvivalView(this::showLevelSelection,
                                score -> completeMission(4, score)));
                    }
                }
                default -> showError("That mission is not available.");
            }
        } catch (SQLException exception) {
            showError("Could not start this mission: " + exception.getMessage());
        }
    }

    private void completeMission(int missionId, int score) {
        try {
            List<MissionProgress> before = gameService.progress(player);
            int previousBest = before.stream().filter(item -> item.missionId() == missionId)
                    .mapToInt(MissionProgress::score).findFirst().orElse(0);
            gameService.completeMission(player, missionId, score);
            int total = gameService.progress(player).stream().mapToInt(MissionProgress::score).sum();
            player = new Player(player.id(), player.playerName(), player.characterName(), total);
            String[] names = {"", "Zero-Gravity Training", "Space Knowledge Assessment",
                    "Spacewalk & Satellite Repair", "Emergency Survival Simulation"};
            show("result-" + nextScreen(), new ResultView(names[missionId], score, previousBest,
                    () -> launchMission(missionId), this::showLevelSelection));
        } catch (SQLException exception) {
            showError("Mission finished, but progress could not be saved: " + exception.getMessage());
            showLevelSelection();
        }
    }

    private void showHowToPlay() {
        show("instructions-" + nextScreen(), new HowToPlayView(this::showMainMenu));
    }

    private int nextScreen() {
        return ++screenNumber;
    }

    private void show(String name, JPanel panel) {
        screen.add(panel, name);
        ((CardLayout) screen.getLayout()).show(screen, name);
        screen.revalidate();
        screen.repaint();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Space Mission", JOptionPane.ERROR_MESSAGE);
    }
}
