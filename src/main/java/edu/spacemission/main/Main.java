package edu.spacemission.main;

import edu.spacemission.controller.GameController;
import edu.spacemission.database.DatabaseConnection;

import javax.swing.*;
import java.sql.SQLException;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.initialize();
                new GameController().showWindow();
            } catch (SQLException | RuntimeException exception) {
                JOptionPane.showMessageDialog(null,
                        "Space Mission could not start.\n" + exception.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
