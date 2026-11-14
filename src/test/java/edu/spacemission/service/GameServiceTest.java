package edu.spacemission.service;

import edu.spacemission.database.DatabaseConnection;
import edu.spacemission.database.PlayerDAO;
import edu.spacemission.database.QuestionDAO;
import edu.spacemission.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    @TempDir Path temporaryHome;

    @Test
    void persistsMissionScoresAndUnlocksTheSecondLevel() throws Exception {
        System.setProperty("user.home", temporaryHome.toString());
        DatabaseConnection.initialize();
        GameService service = new GameService();
        Player player = service.newPlayer("Learner", "Ali");

        assertEquals(8, new QuestionDAO().forMission(2).size());
        assertFalse(service.levelTwoUnlocked(player));

        service.completeMission(player, 1, 80);
        service.completeMission(player, 2, 75);
        assertTrue(service.levelTwoUnlocked(player));

        service.completeMission(player, 1, 20);
        assertEquals(155, new PlayerDAO().latest().orElseThrow().totalScore());
        assertEquals(80, service.progress(player).get(0).score());
    }
}
