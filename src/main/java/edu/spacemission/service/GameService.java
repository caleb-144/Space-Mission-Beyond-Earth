package edu.spacemission.service;

import edu.spacemission.database.PlayerDAO;
import edu.spacemission.database.ProgressDAO;
import edu.spacemission.model.MissionProgress;
import edu.spacemission.model.Player;

import java.sql.SQLException;
import java.util.List;

public final class GameService {
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();

    public Player newPlayer(String name, String character) throws SQLException {
        return playerDAO.create(name, character);
    }

    public void selectCharacter(Player player, String character) throws SQLException {
        playerDAO.updateCharacter(player.id(), character);
    }

    public List<MissionProgress> progress(Player player) throws SQLException {
        return progressDAO.getForPlayer(player.id());
    }

    public void completeMission(Player player, int missionId, int score) throws SQLException {
        progressDAO.save(player.id(), missionId, score);
        int total = progressDAO.getForPlayer(player.id()).stream().mapToInt(MissionProgress::score).sum();
        playerDAO.updateTotalScore(player.id(), total);
    }

    public boolean levelTwoUnlocked(Player player) throws SQLException {
        List<MissionProgress> progress = progress(player);
        return progress.size() >= 2 && progress.get(0).completed() && progress.get(1).completed();
    }
}
