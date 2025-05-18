package sk.tuke.gamestudio.service.interfaces;

import java.util.List;
import java.util.Optional;

import sk.tuke.gamestudio.entity.PlayerStats;
import sk.tuke.gamestudio.entity.enums.GameOutcome;

public interface PlayerStatsService {

    void recordGameResult(String username1, String username2, String game, GameOutcome outcome);

    Optional<PlayerStats> getPlayerStats(String username, String game);

    List<PlayerStats> getLeaderboard(String game, String sortBy, int limit);

    void resetPlayerStats(String username, String game);

    void resetAllPlayerStats(String game);
}
