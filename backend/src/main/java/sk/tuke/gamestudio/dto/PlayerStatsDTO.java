package sk.tuke.gamestudio.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.gamestudio.entity.PlayerStats;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerStatsDTO {

    private Long userId;
    private String username;
    private String game;
    private int wins;
    private int losses;
    private int draws;
    private int mmr;
    private int totalGames;
    private double winRate;
    private Date lastPlayed;

    public static PlayerStatsDTO fromEntity(PlayerStats playerStats) {
        if (playerStats == null) {
            return null;
        }

        PlayerStatsDTO dto = new PlayerStatsDTO();

        if (playerStats.getUser() != null) {
            dto.setUserId(playerStats.getUser().getId());
            dto.setUsername(playerStats.getUser().getUsername());
        } else {
            dto.setUserId(null);
            dto.setUsername("N/A");
        }
        dto.setGame(playerStats.getGame());
        dto.setWins(playerStats.getWins());
        dto.setLosses(playerStats.getLosses());
        dto.setDraws(playerStats.getDraws());
        dto.setMmr(playerStats.getMmr());
        dto.setTotalGames(playerStats.getTotalGames());
        dto.setWinRate(playerStats.getWinRate());
        dto.setLastPlayed(playerStats.getLastPlayedAt());

        return dto;
    }

}