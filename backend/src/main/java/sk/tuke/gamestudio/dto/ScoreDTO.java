package sk.tuke.gamestudio.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.gamestudio.entity.Score;

@Data
@NoArgsConstructor
public class ScoreDTO {

    private Long id;
    private String game;
    private String username;
    private int points;
    private Instant playedAt;

    public static ScoreDTO fromEntity(Score entity) {
        if (entity == null)
            return null;
        ScoreDTO dto = new ScoreDTO();
        dto.setId(entity.getId());
        dto.setGame(entity.getGame());
        dto.setPoints(entity.getPoints());
        dto.setPlayedAt(entity.getPlayedAt());
        if (entity.getUser() != null) {
            dto.setUsername(entity.getUser().getUsername());
        }
        return dto;
    }
}
