package edu.spacemission.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnection {
    private static final String DB_URL = createUrl();

    private DatabaseConnection() { }

    private static String createUrl() {
        Path database = Path.of(System.getProperty("user.home"), ".space-mission-beyond-earth", "game.db");
        try {
            Files.createDirectories(database.getParent());
        } catch (IOException exception) {
            throw new IllegalStateException("Could not create the game data directory: " + database.getParent(), exception);
        }
        return "jdbc:sqlite:" + database;
    }

    public static Connection open() throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initialize() throws SQLException {
        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS players (
                      player_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_name TEXT NOT NULL,
                      character_name TEXT NOT NULL DEFAULT 'Ali',
                      total_score INTEGER NOT NULL DEFAULT 0,
                      created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS levels (
                      level_id INTEGER PRIMARY KEY,
                      level_name TEXT NOT NULL,
                      description TEXT NOT NULL,
                      unlock_status INTEGER NOT NULL DEFAULT 0 CHECK (unlock_status IN (0, 1))
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS missions (
                      mission_id INTEGER PRIMARY KEY,
                      level_id INTEGER NOT NULL REFERENCES levels(level_id),
                      mission_name TEXT NOT NULL,
                      description TEXT NOT NULL,
                      max_score INTEGER NOT NULL CHECK (max_score >= 0)
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS player_progress (
                      progress_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      player_id INTEGER NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
                      mission_id INTEGER NOT NULL REFERENCES missions(mission_id),
                      completion_status INTEGER NOT NULL DEFAULT 0 CHECK (completion_status IN (0, 1)),
                      score INTEGER NOT NULL DEFAULT 0 CHECK (score >= 0),
                      completed_at TEXT,
                      UNIQUE(player_id, mission_id)
                    )""");
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS questions (
                      question_id INTEGER PRIMARY KEY,
                      mission_id INTEGER NOT NULL REFERENCES missions(mission_id),
                      question_text TEXT NOT NULL,
                      option_a TEXT NOT NULL,
                      option_b TEXT NOT NULL,
                      option_c TEXT NOT NULL,
                      option_d TEXT NOT NULL,
                      correct_answer TEXT NOT NULL CHECK (correct_answer IN ('A', 'B', 'C', 'D')),
                      explanation TEXT NOT NULL
                    )""");
            seed(statement);
        }
    }

    private static void seed(Statement statement) throws SQLException {
        statement.executeUpdate("""
                INSERT OR IGNORE INTO levels(level_id, level_name, description, unlock_status) VALUES
                (1, 'Basic Training', 'Learn to move in microgravity and test your space knowledge.', 1),
                (2, 'Spacecraft Operation & Simulation', 'Repair a satellite and respond to spacecraft emergencies.', 0)""");
        statement.executeUpdate("""
                INSERT OR IGNORE INTO missions(mission_id, level_id, mission_name, description, max_score) VALUES
                (1, 1, 'Zero-Gravity Training', 'Collect all floating training beacons and reach the airlock.', 100),
                (2, 1, 'Space Knowledge Assessment', 'Answer questions about planets, gravity, and exploration.', 100),
                (3, 2, 'Spacewalk & Satellite Repair', 'Complete repair tasks before your oxygen runs low.', 100),
                (4, 2, 'Emergency Survival Simulation', 'Choose safe procedures during spacecraft emergencies.', 100)""");
        statement.executeUpdate("""
                INSERT OR IGNORE INTO questions
                (question_id, mission_id, question_text, option_a, option_b, option_c, option_d, correct_answer, explanation) VALUES
                (1, 2, 'Which planet is known as the Red Planet?', 'Venus', 'Mars', 'Jupiter', 'Mercury', 'B', 'Iron-rich dust gives Mars its reddish appearance.'),
                (2, 2, 'What keeps the planets in orbit around the Sun?', 'Magnetism', 'Air resistance', 'Gravity', 'Solar wind', 'C', 'The Sun’s gravity continually bends each planet’s path into an orbit.'),
                (3, 2, 'What does an astronaut experience in orbit?', 'Microgravity', 'No mass', 'No motion', 'Stronger gravity than on Earth', 'A', 'Astronauts and their spacecraft are in continuous free fall, creating a microgravity experience.'),
                (4, 2, 'What is the International Space Station?', 'A telescope on Mars', 'An orbiting research laboratory', 'A rocket engine', 'A lunar lander', 'B', 'The ISS is a crewed laboratory orbiting Earth.'),
                (5, 2, 'Which equipment helps an astronaut breathe during a spacewalk?', 'A parachute', 'A spacesuit life-support system', 'A solar panel', 'A radio dish', 'B', 'A spacesuit provides oxygen, pressure, and temperature control.'),
                (6, 2, 'Which is the largest planet in our solar system?', 'Saturn', 'Neptune', 'Earth', 'Jupiter', 'D', 'Jupiter is the largest planet in our solar system.'),
                (7, 2, 'Why do astronauts appear to float inside an orbiting spacecraft?', 'There is no gravity in space', 'They and the spacecraft are falling together around Earth', 'The cabin is full of helium', 'The spacecraft is outside the solar system', 'B', 'Gravity is still present in low Earth orbit; the crew and spacecraft are in free fall together.'),
                (8, 2, 'What is a satellite?', 'An object that orbits another object in space', 'A type of spacesuit', 'A star that has gone out', 'A rocket launch site', 'A', 'Natural and human-made satellites orbit planets and other celestial bodies.')""");
    }
}
