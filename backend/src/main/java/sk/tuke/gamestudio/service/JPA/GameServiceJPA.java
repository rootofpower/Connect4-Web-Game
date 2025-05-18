package sk.tuke.gamestudio.service.JPA;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.GameOutcome;
import sk.tuke.gamestudio.entity.enums.GameStatus;
import sk.tuke.gamestudio.game.connectfour.core.Board;
import sk.tuke.gamestudio.game.connectfour.core.CellState;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// import jakarta.transaction.Transactional;
import org.springframework.transaction.annotation.*;
import lombok.RequiredArgsConstructor;
import sk.tuke.gamestudio.dto.GameSessionDTO;
import sk.tuke.gamestudio.entity.GameSession;
import sk.tuke.gamestudio.repository.GameSessionRepository;
import sk.tuke.gamestudio.service.interfaces.GameService;
import sk.tuke.gamestudio.service.interfaces.PlayerStatsService;

@Service
@Transactional
@RequiredArgsConstructor
public class GameServiceJPA implements GameService {

    private final GameSessionRepository gameSessionRepository;
    private final PlayerStatsService playerStatsService;
    private final ObjectMapper objectMapper;
    // private final LobbyService lobbyService;

    private static final Logger log = LoggerFactory.getLogger(GameServiceJPA.class);

    @Override
    public GameSessionDTO createNewGameSession(User player1, User player2, String gameName) {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }
        if (Objects.equals(player1.getId(), player2.getId())) {
            throw new IllegalArgumentException("Players cannot be the same");
        }
        if (gameName == null || gameName.isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty");
        }

        log.info("Creating new game session: {} for players: {} and {}", gameName, player1.getUsername(),
                player2.getUsername());

        Board initialBoard = new Board();
        String initialBoardJson = serializeBoard(initialBoard);
        GameSession gameSession = new GameSession(gameName, player1, player2, initialBoardJson);

        GameSession savedGameSession = gameSessionRepository.save(gameSession);
        log.info("New game session created: {} with ID: {}", gameName, savedGameSession.getId());
        return GameSessionDTO.fromEntity(savedGameSession);
    }

    @Override
    @Transactional(readOnly = true)
    public GameSessionDTO getGameState(Long gameSessionId, String requestingUsername) {
        log.debug("Fetching game state for session ID: {}", gameSessionId);
        GameSession game = findGameByIdOrThrow(gameSessionId);

        String player1Username = game.getPlayer1().getUsername();
        String player2Username = game.getPlayer2().getUsername();
        if (!requestingUsername.equals(player1Username) && !requestingUsername.equals(player2Username)) {
            log.warn("Unauthorized access attempt by user: {} to game session: {}", requestingUsername, gameSessionId);
            throw new AccessDeniedException(
                    "User '" + requestingUsername + "' is not authorized to access game session " + gameSessionId);
        }
        log.debug("Game state fetched successfully for session ID: {}", gameSessionId);
        return GameSessionDTO.fromEntity(game);
    }

    @Override
    public GameSessionDTO makeMove(Long gameSessionId, Long playerId, int column) {
        log.info("Processing move for gameId: {}, playerId: {}, column: {}", gameSessionId, playerId, column);
        GameSession gameSession = findGameByIdOrThrow(gameSessionId);

        validateGameIsGoing(gameSession);
        User currentPlayer = validatePlayerTurn(gameSession, playerId);
        Board board = deserializeBoard(gameSession.getBoardState());
        if (column < 0 || column >= board.getColumns() || board.getCellState(0, column) != CellState.EMPTY) {
            log.warn("Invalid move attempt by player {} in game {}: Column {} is full or invalid.",
                    currentPlayer.getUsername(), gameSessionId, column);
            throw new IllegalArgumentException("Invalid move: Column " + column + " is full or invalid");
        }
        CellState playerCellState = getPlayerCellState(gameSession, currentPlayer);
        boolean moveResult = board.dropToken(column, playerCellState);
        if (!moveResult) {
            log.error("Invalid move attempt by player {} in game {}: Column {} is full.", currentPlayer.getUsername(),
                    gameSessionId, column);
            throw new IllegalArgumentException("Invalid move: Column " + column + " is full");
        }
        log.debug("Move successful for player {} in game {}: Column {} updated.", currentPlayer.getUsername(),
                gameSessionId, column);
        log.info("Game {}: Player {} made a move in column {}. Board state: {}", gameSessionId,
                currentPlayer.getUsername(), column, board);
        boolean win = board.checkWin(playerCellState);
        boolean draw = !win && board.isFull();
        GameStatus newStatus;
        GameOutcome outcome = null;

        if (win) {
            newStatus = (playerCellState == CellState.PLAYER1) ? GameStatus.PLAYER_1_WINS : GameStatus.PLAYER_2_WINS;
            outcome = (playerCellState == CellState.PLAYER1) ? GameOutcome.PLAYER_1_WINS : GameOutcome.PLAYER_2_WINS;
            log.info("Game {} finished. Winner: {}. Status: {}", gameSessionId,
                    currentPlayer.getUsername(), newStatus);
        } else if (draw) {
            newStatus = GameStatus.DRAW;
            outcome = GameOutcome.DRAW;
            log.info("Game {} finished. Status: {}", gameSessionId, newStatus);
        } else {
            User nextPlayer = Objects.equals(currentPlayer.getId(), gameSession.getPlayer1().getId())
                    ? gameSession.getPlayer2()
                    : gameSession.getPlayer1();
            gameSession.setCurrentPlayerUsername(nextPlayer.getUsername());
            newStatus = (playerCellState == CellState.PLAYER1) ? GameStatus.PLAYER_2_TURN : GameStatus.PLAYER_1_TURN;
            log.debug("Game {} continues. Next player: {}. Status: {}", gameSessionId,
                    gameSession.getCurrentPlayerUsername(), newStatus);
        }
        gameSession.setStatus(newStatus);
        gameSession.setBoardState(serializeBoard(board));
        GameSession updatedGameSession = gameSessionRepository.save(gameSession);
        if (outcome != null) {
            log.info("Game {} finished with outcome: {}", gameSessionId, outcome);
            recordStats(gameSession, outcome);
        }
        return GameSessionDTO.fromEntity(updatedGameSession);
    }

    private GameSession findGameByIdOrThrow(Long gameSessionId) {
        return gameSessionRepository.findById(gameSessionId)
                .orElseThrow(() -> new IllegalArgumentException("Game session not found"));
    }

    private String serializeBoard(Board board) {
        try {
            return objectMapper.writeValueAsString(board.getCells());
        } catch (JsonProcessingException e) {
            log.error("Error serializing board: {}", e.getMessage());
            throw new RuntimeException("Error serializing board", e);
        }
    }

    private void validateGameIsGoing(GameSession gameSession) {
        GameStatus status = gameSession.getStatus();
        if (status != GameStatus.PLAYER_1_TURN && status != GameStatus.PLAYER_2_TURN) {
            log.warn("Game {} is not in a playable state: {}", gameSession.getId(), status);
            throw new IllegalStateException("Game is not in a playable state: " + status);
        }
    }

    private User validatePlayerTurn(GameSession gameSession, Long playerId) {
        User player1 = gameSession.getPlayer1();
        User player2 = gameSession.getPlayer2();
        String currentPlayerUsername = gameSession.getCurrentPlayerUsername();
        User expectedPlayer = null;

        if (currentPlayerUsername.equals(player1.getUsername())) {
            expectedPlayer = player1;
        } else if (currentPlayerUsername.equals(player2.getUsername())) {
            expectedPlayer = player2;
        }

        if (expectedPlayer == null || !Objects.equals(expectedPlayer.getId(), playerId)) {
            log.warn("Invalid turn in game {}: Expected player: {}, Current player: {}",
                    gameSession.getId(), currentPlayerUsername, playerId);
            throw new IllegalStateException("It's not your turn (Expected: " + currentPlayerUsername + ")");
        }
        return expectedPlayer;
    }

    private CellState getPlayerCellState(GameSession gameSession, User player) {
        if (gameSession.getPlayer1().getId().equals(player.getId())) {
            return CellState.PLAYER1;
        } else if (gameSession.getPlayer2().getId().equals(player.getId())) {
            return CellState.PLAYER2;
        } else {
            log.error("User {} is not player1 or player2 in game {}", player.getUsername(), gameSession.getId());
            throw new IllegalArgumentException("Player not part of the game session");
        }
    }

    private Board deserializeBoard(String boardJson) {
        try {
            CellState[][] cells = objectMapper.readValue(boardJson, CellState[][].class);
            if (cells == null || cells.length == 0 || (cells[0].length == 0)) {
                log.error("Deserialized cell array is invalid (empty or null) from JSON: {}", boardJson);
                throw new IOException("Deserialized cell array is invalid (empty or null)");
            }
            Board board = new Board(cells.length, cells[0].length);
            board.setCells(cells);
            return board;
        } catch (IOException e) {
            log.error("Error deserializing board from JSON: {}. Message: {}", boardJson, e.getMessage());
            throw new RuntimeException("Error deserializing board", e);
        }
    }

    private void recordStats(GameSession gameSession, GameOutcome outcome) {
        try {
            log.info("Recording stats for game session: {} with outcome: {}", gameSession.getId(), outcome);
            playerStatsService.recordGameResult(
                    gameSession.getPlayer1().getUsername(),
                    gameSession.getPlayer2().getUsername(),
                    gameSession.getGameName(),
                    outcome);
            log.info("Player stats recorded successfully for game session: {}", gameSession.getId());

            // try {
            // lobbyService.deleteLobbyByGameSessionId(gameSession.getId());
            // log.info("Lobby deleted successfully for game session: {}",
            // gameSession.getId());
            // } catch (Exception e) {
            // log.error("Failed to delete lobby for game session: {}", gameSession.getId(),
            // e);
            // }
        } catch (Exception e) {
            log.error("Failed to record player stats for the game: {}", gameSession.getId(), e);
        }
    }
}
