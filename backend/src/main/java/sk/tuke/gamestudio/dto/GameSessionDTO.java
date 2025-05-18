package sk.tuke.gamestudio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.gamestudio.entity.GameSession;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionDTO {

    private Long id;
    private String gameName;
    private String player1Username;
    private String player2Username;
    private String boardState;
    private String currentPlayerUsername;
    private String status;

    public static GameSessionDTO fromEntity(GameSession session) {
        if (session == null) {
            return null;
        }

        GameSessionDTO dto = new GameSessionDTO();
        dto.setId(session.getId());
        dto.setGameName(session.getGameName());

        if (session.getPlayer1() != null) {
            dto.setPlayer1Username(session.getPlayer1().getUsername());
        }

        if (session.getPlayer2() != null) {
            dto.setPlayer2Username(session.getPlayer2().getUsername());
        }

        dto.setBoardState(session.getBoardState());
        dto.setCurrentPlayerUsername(session.getCurrentPlayerUsername());

        if (session.getStatus() != null) {
            dto.setStatus(session.getStatus().name());
        }

        return dto;
    }
}
