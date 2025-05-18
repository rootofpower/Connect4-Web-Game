package sk.tuke.gamestudio.service.JPA;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import sk.tuke.gamestudio.dto.GameSessionDTO;
import sk.tuke.gamestudio.entity.Lobby;
import sk.tuke.gamestudio.repository.LobbyRepository;
import sk.tuke.gamestudio.service.interfaces.GameService;
import sk.tuke.gamestudio.service.interfaces.LobbyService;
import sk.tuke.gamestudio.service.interfaces.UserService;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.LobbyStatus;

@Service
@RequiredArgsConstructor
@Transactional
public class LobbyServiceJPA implements LobbyService {

    private final LobbyRepository lobbyRepository;
    private final UserService userService;
    private final GameService gameService;

    private static final int LOBBY_CODE_LENGTH = 6;
    private static final String LOBBY_CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new Random();

    private static final Logger log = LoggerFactory.getLogger(LobbyServiceJPA.class);

    @Override
    public Lobby createLobby(String hostName, String gameName) {
        User host = userService.findUserByUsername(hostName).orElseThrow(
                () -> new EntityNotFoundException("User not found: " + hostName));

        String lobbyCode = generateLobbyCode();
        do {
            lobbyCode = generateLobbyCode();
        } while (lobbyRepository.existsByLobbyCode(lobbyCode));

        Lobby lobby = new Lobby(lobbyCode, host, gameName);

        return lobbyRepository.save(lobby);
    }

    @Override
    public Lobby joinLobby(String playerName, String lobbyCode) {
        Lobby lobby = lobbyRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found: " + lobbyCode));

        User opponent = userService.findUserByUsername(playerName)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + playerName));

        if (lobby.getStatus() != LobbyStatus.WAITING_FOR_OPPONENT) {
            throw new IllegalStateException("Lobby is not in a state to join: " + lobby.getStatus());
        }
        if (lobby.getOpponentUser() != null) {
            throw new IllegalStateException("Lobby is already full: " + lobby.getLobbyCode());
        }
        if (Objects.equals(lobby.getHostUser().getId(), opponent.getId())) {
            throw new IllegalStateException("You cannot join your own lobby: " + lobby.getLobbyCode());
        }

        lobby.setOpponentUser(opponent);
        lobby.setStatus(LobbyStatus.WAITING_FOR_READY);
        return lobbyRepository.save(lobby);

    }

    @Override
    public Optional<Lobby> leaveLobby(String playerName, String lobbyCode) {
        Lobby lobby = lobbyRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found: " + lobbyCode));
        User user = userService.findUserByUsername(playerName)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + playerName));

        Long userId = user.getId();
        Long hostId = lobby.getHostUser().getId();
        User opponent = lobby.getOpponentUser();
        Long opponentId = (opponent != null) ? opponent.getId() : null;
        if (Objects.equals(userId, hostId)) {
            // Host is leaving
            if (opponent != null) {
                lobby.setHostUser(opponent);
                lobby.setOpponentUser(null);
                lobby.setHostReady(lobby.isOpponentReady());
                lobby.setOpponentReady(false);
                lobby.setStatus(LobbyStatus.WAITING_FOR_OPPONENT);
                return Optional.of(lobbyRepository.save(lobby));
            } else {
                lobbyRepository.delete(lobby);
                return Optional.empty();
            }
        } else if (Objects.equals(userId, opponentId)) {
            // Opponent is leaving
            lobby.setOpponentUser(null);
            lobby.setOpponentReady(false);
            lobby.setStatus(LobbyStatus.WAITING_FOR_OPPONENT);
            return Optional.of(lobbyRepository.save(lobby));
        } else {
            throw new IllegalStateException("User '" + playerName + "' is not part of the lobby: '" + lobbyCode + "'");
        }
    }

    @Override
    public Lobby getLobbyState(String lobbyCode) {
        return lobbyRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found: " + lobbyCode));
    }

    @Override
    public Lobby setPlayerReady(String username, String lobbyCode, boolean isReady) {
        Lobby lobby = getLobbyState(lobbyCode);
        log.info("SERVICE setPlayerReady: Attempting to find user by username: '{}'", username);
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> {
                    // --- ДОДАЙ ЛОГ ПЕРЕД ПОМИЛКОЮ ---
                    log.error("SERVICE setPlayerReady: User lookup FAILED for username: '{}'", username);
                    // -------------------------------
                    return new EntityNotFoundException("User not found: " + username);
                });
        log.info("SERVICE setPlayerReady: User found with ID: {}", user.getId());
        if (Objects.equals(lobby.getHostUser().getId(), user.getId())) {
            lobby.setHostReady(isReady);
        } else if (lobby.getOpponentUser() != null && Objects.equals(lobby.getOpponentUser().getId(), user.getId())) {
            lobby.setOpponentReady(isReady);
        } else {
            throw new IllegalStateException("User is not part of the lobby: " + username);
        }

        if (lobby.getStatus() == LobbyStatus.WAITING_FOR_READY) {
            if (lobby.isHostReady() && lobby.isOpponentReady()) {
                lobby.setStatus(LobbyStatus.READY_TO_START);
            } else {
                lobby.setStatus(LobbyStatus.WAITING_FOR_READY);
            }
        }
        return lobbyRepository.save(lobby);
    }

    @Override
    public Object startGame(String lobbyCode, String hostName) {
        Lobby lobby = lobbyRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found: " + lobbyCode));
        User host = userService.findUserByUsername(hostName)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + hostName));
        log.info("Starting game: {}. for lobby: {} with host: {}", lobby.getGameName(), lobbyCode, hostName);
        if (lobby.getStatus() != LobbyStatus.READY_TO_START) {
            throw new IllegalStateException("Lobby is not ready to start: " + lobby.getStatus());
        }
        if (!Objects.equals(lobby.getHostUser().getId(), host.getId())) {
            throw new IllegalStateException("Only the host can start the game: " + hostName);
        }
        User opponent = lobby.getOpponentUser();
        if (opponent == null) {
            log.error("Start game failed: Opponent user is null {}", lobbyCode);
            throw new IllegalStateException("Opponent is not set: " + lobby.getLobbyCode());
        }
        GameSessionDTO gameSessionDTO;
        try {
            log.debug("Calling gameService to start game session for lobby: {}", lobbyCode);
            gameSessionDTO = gameService.createNewGameSession(host, opponent, lobby.getGameName());
            if (gameSessionDTO == null || gameSessionDTO.getId() == null) {
                log.error("Game session is null for lobby: {}", lobbyCode);
                throw new NullPointerException("Game session is null for lobby: " + lobbyCode);
            }
            log.info("Game session: {} started successfully for lobby: {}", gameSessionDTO.getId(), lobbyCode);
        } catch (Exception e) {
            log.error("Error starting game for lobby: {}. Exception: {}", lobbyCode, e.getMessage());
            throw new RuntimeException("Error starting game for lobby: " + lobbyCode, e);
        }

        lobby.setGameSessionId(gameSessionDTO.getId());
        lobby.setStatus(LobbyStatus.IN_GAME);
        lobbyRepository.save(lobby);
        log.info("Lobby {} is now in game with session ID: {}", lobbyCode, gameSessionDTO.getId());

        // return gameSession.getId();
        return Map.of(
                "message", "Game started successfully",
                "gameSessionId", gameSessionDTO.getId());
    }

    @Override
    public void deleteLobby(String lobbyCode) {
        Lobby lobby = lobbyRepository.findByLobbyCode(lobbyCode)
                .orElseThrow(() -> new EntityNotFoundException("Lobby not found: " + lobbyCode));
        lobbyRepository.delete(lobby);
    }

    private String generateLobbyCode() {
        StringBuilder lobbyCode = new StringBuilder(LOBBY_CODE_LENGTH);
        for (int i = 0; i < LOBBY_CODE_LENGTH; i++) {
            lobbyCode.append(LOBBY_CODE_CHARACTERS.charAt(random.nextInt(LOBBY_CODE_CHARACTERS.length())));
        }
        return lobbyCode.toString();
    }

    @Override
    public void deleteLobbyByGameSessionId(Long gameSessionId) {
        if (gameSessionId == null) {
            log.warn("Attempted to delete lobby with null game session ID");
            return;
        }
        Optional<Lobby> lobbyOptional = lobbyRepository.findByGameSessionId(gameSessionId);
        lobbyOptional.ifPresentOrElse(
                lobby -> {
                    lobbyRepository.delete(lobby);
                    log.info("Deleted lobby with game session ID: {}", gameSessionId);
                },
                () -> log.warn("No lobby found with game session ID: {}", gameSessionId));
    }
}
