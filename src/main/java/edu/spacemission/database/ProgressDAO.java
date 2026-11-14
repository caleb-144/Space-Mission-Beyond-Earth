package edu.spacemission.database;

import edu.spacemission.model.MissionProgress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class ProgressDAO {
    public List<MissionProgress> getForPlayer(long playerId) throws SQLException {
        List<MissionProgress> progress = new ArrayList<>();
        String query = """
                SELECT m.mission_id, m.mission_name, m.max_score,
                       COALESCE(p.score, 0) AS score, COALESCE(p.completion_status, 0) AS completed
                FROM missions m LEFT JOIN player_progress p
                  ON p.mission_id = m.mission_id AND p.player_id = ?
                ORDER BY m.mission_id""";
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, playerId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    progress.add(new MissionProgress(rows.getInt("mission_id"), rows.getString("mission_name"),
                            rows.getInt("score"), rows.getInt("max_score"), rows.getInt("completed") == 1));
                }
            }
        }
        return progress;
    }

    public void save(long playerId, int missionId, int score) throws SQLException {
        String query = """
                INSERT INTO player_progress(player_id, mission_id, completion_status, score, completed_at)
                VALUES(?, ?, 1, ?, CURRENT_TIMESTAMP)
                ON CONFLICT(player_id, mission_id) DO UPDATE SET
                  completion_status = 1, score = MAX(player_progress.score, excluded.score),
                  completed_at = CURRENT_TIMESTAMP""";
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, playerId);
            statement.setInt(2, missionId);
            statement.setInt(3, score);
            statement.executeUpdate();
        }
    }
}
