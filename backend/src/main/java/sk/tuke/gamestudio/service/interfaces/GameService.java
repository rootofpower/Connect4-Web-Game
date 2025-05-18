package sk.tuke.gamestudio.service.interfaces;

import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.dto.GameSessionDTO;

public interface GameService {

    GameSessionDTO createNewGameSession(User player1, User player2, String gameName);

    GameSessionDTO getGameState(Long gameSessionId, String requestingUsername);

    GameSessionDTO makeMove(Long gameSessionId, Long playerId, int column);
}
