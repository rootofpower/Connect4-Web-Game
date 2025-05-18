package sk.tuke.gamestudio.service.JPA;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

import sk.tuke.gamestudio.entity.PlayerStats;
import sk.tuke.gamestudio.repository.PlayerStatsRepository;
import sk.tuke.gamestudio.service.interfaces.PlayerStatsService;
import sk.tuke.gamestudio.service.interfaces.UserService;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.GameOutcome;

@Service
@Transactional
@RequiredArgsConstructor

public class PlayerStatsServiceJPA implements PlayerStatsService {
    // Implement the methods from PlayerStatsService interface
    // Use JPA to interact with the database

    private final PlayerStatsRepository playerStatsRepository;
    private final UserService userService;

    @Value("${gamestudio.stats.mmr-change:40}")
    private int MMR_CHANGE;

    @Override
    public void recordGameResult(String username1, String username2, String game, GameOutcome outcome) {
        User user1 = userService.findUserByUsername(username1)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username1));
        User user2 = userService.findUserByUsername(username2)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username2));

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("User not found");
        }

        PlayerStats playerStats1 = playerStatsRepository.findByUserAndGame(user1, game)
                .orElseGet(() -> new PlayerStats(user1, game));
        PlayerStats playerStats2 = playerStatsRepository.findByUserAndGame(user2, game)
                .orElseGet(() -> new PlayerStats(user2, game));

        int mmr1 = playerStats1.getMmr();
        int mmr2 = playerStats2.getMmr();
        int newMmr1 = mmr1;
        int newMmr2 = mmr2;

        switch (outcome) {
            case PLAYER_1_WINS:
                playerStats1.setWins(playerStats1.getWins() + 1);
                playerStats2.setLosses(playerStats2.getLosses() + 1);
                newMmr1 = mmr1 + MMR_CHANGE;
                newMmr2 = Math.max(0, mmr2 - MMR_CHANGE);
                break;
            case PLAYER_2_WINS:
                playerStats1.setLosses(playerStats1.getLosses() + 1);
                playerStats2.setWins(playerStats2.getWins() + 1);
                newMmr1 = Math.max(0, mmr1 - MMR_CHANGE);
                newMmr2 = mmr2 + MMR_CHANGE;
                break;
            case DRAW:
                playerStats1.setDraws(playerStats1.getDraws() + 1);
                playerStats2.setDraws(playerStats2.getDraws() + 1);
                newMmr1 = mmr1;
                newMmr2 = mmr2;
                break;
        }

        playerStats1.setMmr(newMmr1);
        playerStats2.setMmr(newMmr2);
        playerStatsRepository.save(playerStats1);
        playerStatsRepository.save(playerStats2);
    }

    @Override
    public Optional<PlayerStats> getPlayerStats(String username, String game) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return playerStatsRepository.findByUserAndGame(user, game);
    }

    @Override
    public List<PlayerStats> getLeaderboard(String game, String sortBy, int limit) {
        if ("mmr".equalsIgnoreCase(sortBy)) {
            return playerStatsRepository.findTop10ByGameOrderByMmrDesc(game);
        } else if ("wins".equalsIgnoreCase(sortBy)) {
            return playerStatsRepository.findTop10ByGameOrderByWinsDesc(game);
        } else {
            return playerStatsRepository.findTop10ByGameOrderByMmrDesc(game);
        }
    }

    @Override
    public void resetPlayerStats(String username, String game) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        PlayerStats playerStats = playerStatsRepository.findByUserAndGame(user, game)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Player stats not found for user: " + username + " and game: " + game));
        playerStats.setWins(0);
        playerStats.setLosses(0);
        playerStats.setDraws(0);
        playerStats.setMmr(1000);
        playerStatsRepository.save(playerStats);
    }

    @Override
    public void resetAllPlayerStats(String game) {
        List<PlayerStats> playerStatsList = playerStatsRepository.findAllByGame(game);
        for (PlayerStats playerStats : playerStatsList) {
            playerStats.setWins(0);
            playerStats.setLosses(0);
            playerStats.setDraws(0);
            playerStats.setMmr(1000);
            playerStatsRepository.save(playerStats);
        }
    }
}
