package edu.spacemission.database;

import edu.spacemission.model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class QuestionDAO {
    public List<Question> forMission(int missionId) throws SQLException {
        List<Question> questions = new ArrayList<>();
        try (Connection connection = DatabaseConnection.open();
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT question_text, option_a, option_b, option_c, option_d, correct_answer, explanation
                     FROM questions WHERE mission_id = ? ORDER BY question_id""")) {
            statement.setInt(1, missionId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    questions.add(new Question(rows.getString("question_text"),
                            List.of(rows.getString("option_a"), rows.getString("option_b"),
                                    rows.getString("option_c"), rows.getString("option_d")),
                            rows.getString("correct_answer"), rows.getString("explanation")));
                }
            }
        }
        return questions;
    }
}
