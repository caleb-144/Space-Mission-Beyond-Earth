package edu.spacemission.database;

import edu.spacemission.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class PlayerDAO {
    public Player create(String name, String character) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement insert = connection.prepareStatement(
                     "INSERT INTO players(player_name, character_name) VALUES(?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, name);
            insert.setString(2, character);
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Player was saved, but its ID was not returned.");
                return new Player(keys.getLong(1), name, character, 0);
            }
        }
    }

    public void updateCharacter(long id, String character) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement("UPDATE players SET character_name = ? WHERE player_id = ?")) {
            statement.setString(1, character);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    public void updateTotalScore(long id, int score) throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement("UPDATE players SET total_score = ? WHERE player_id = ?")) {
            statement.setInt(1, score);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

    public Optional<Player> latest() throws SQLException {
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT player_id, player_name, character_name, total_score FROM players ORDER BY player_id DESC LIMIT 1");
             ResultSet rows = statement.executeQuery()) {
            if (!rows.next()) return Optional.empty();
            return Optional.of(new Player(rows.getLong("player_id"), rows.getString("player_name"),
                    rows.getString("character_name"), rows.getInt("total_score")));
        }
    }
}
