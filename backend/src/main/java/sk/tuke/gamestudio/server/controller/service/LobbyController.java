package sk.tuke.gamestudio.server.controller.service;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import sk.tuke.gamestudio.dto.LobbyDTO;
import sk.tuke.gamestudio.dto.requests.CreateLobbyRequest;
import sk.tuke.gamestudio.entity.Lobby;
import sk.tuke.gamestudio.service.interfaces.LobbyService;

@RestController
@RequestMapping("/api/connect4/lobby")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class LobbyController {
    private final LobbyService lobbyService;
    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    // post request for creating a new lobby
    // POST -> http://localhost:8080/api/connect4/lobby/create
    @PostMapping("/create")
    public ResponseEntity<?> createLobby(@RequestBody CreateLobbyRequest request, Authentication authentication) {
        try {
            String username = authentication.getName();
            logger.info("Creating lobby for user: {}", username);
            logger.info("Lobby request: {}", request);
            Lobby createdLobby = lobbyService.createLobby(username, request.getGameName());
            // return 201 Created with the created lobby data
            return ResponseEntity.status(HttpStatus.CREATED).body(LobbyDTO.fromEntity(createdLobby));
        } catch (EntityNotFoundException e) {
            logger.warn("Lobby creation failed: User '{}' not found ", authentication.getName());
            // return 404 Not Found with error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating lobby: {}", e.getMessage());
            // return 500 Internal Server Error with error message
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error creating lobby"));
        }
    }

    // post request for joining a lobby
    // POST -> http://localhost:8080/api/connect4/lobby/join/{lobbyCode}
    @PostMapping("/join/{lobbyCode}")
    public ResponseEntity<?> joinLobby(@PathVariable String lobbyCode, Authentication authentication) {
        try {
            String username = authentication.getName();
            logger.info("User '{}' is trying to join lobby with code: {}", username, lobbyCode);
            Lobby joinedLobby = lobbyService.joinLobby(username, lobbyCode);
            // return 200 OK with the joined lobby data
            return ResponseEntity.ok(LobbyDTO.fromEntity(joinedLobby));
        } catch (EntityNotFoundException e) {
            logger.warn("Lobby joining failed: User '{}' not found ", authentication.getName());
            // return 404 Not Found with error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Lobby joining failed: {}", e.getMessage());
            // return 409 Conflict with error message
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error joining lobby: {}", e.getMessage());
            // return 500 Internal Server Error with error message
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error joining lobby"));
        }
    }

    // get request for getting lobby by code
    // GET -> http://localhost:8080/api/connect4/lobby/{lobbyCode}
    @GetMapping("/{lobbyCode}")
    public ResponseEntity<?> getLobby(@PathVariable String lobbyCode, Authentication authentication) {
        try {
            String username = authentication.getName();
            logger.info("User '{}' is trying to get lobby with code: {}", username, lobbyCode);
            Lobby lobby = lobbyService.getLobbyState(lobbyCode);
            // return 200 OK with the lobby data
            return ResponseEntity.ok(LobbyDTO.fromEntity(lobby));
        } catch (EntityNotFoundException e) {
            logger.warn("Lobby retrieval failed: User '{}' not found ", authentication.getName());
            // return 404 Not Found with error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving lobby: {}", e.getMessage());
            // return 500 Internal Server Error with error message
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error retrieving lobby"));
        }
    }

    // post request for changing player state to ready
    // POST -> http://localhost:8080/api/connect4/lobby/{lobbyCode}/ready
    @PostMapping("/{lobbyCode}/ready")
    public ResponseEntity<?> setReady(@PathVariable String lobbyCode, Authentication authentication) {
        return setReadyStatus(lobbyCode, authentication, true);
    }

    // post request for changing player state to not ready
    // POST -> http://localhost:8080/api/connect4/lobby/{lobbyCode}/unready
    @PostMapping("/{lobbyCode}/unready")
    public ResponseEntity<?> setNotReady(@PathVariable String lobbyCode, Authentication authentication) {
        return setReadyStatus(lobbyCode, authentication, false);
    }

    private ResponseEntity<?> setReadyStatus(String lobbyCode, Authentication authentication, boolean isReady) {
        try {
            String username = authentication.getName();
            logger.info("User '{}' is trying to set ready status to {} for lobby with code: {}", username, isReady,
                    lobbyCode);
            logger.info("CONTROLLER setReadyStatus: User from Authentication: '{}', LobbyCode: {}, isReady: {}",
                    username, lobbyCode, isReady);
            Lobby lobby = lobbyService.setPlayerReady(username, lobbyCode, isReady);
            // return 200 OK with the updated lobby data
            return ResponseEntity.ok(LobbyDTO.fromEntity(lobby));
        } catch (EntityNotFoundException e) {
            logger.warn("Lobby state update failed: User '{}' not found ", authentication.getName());
            // return 404 Not Found with error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Lobby state update failed: {}", e.getMessage());
            // return 400 Bad Request with error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating lobby state: {}", e.getMessage());
            // return 500 Internal Server Error with error message
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error updating lobby state"));
        }
    }

    // post request for starting the game
    // POST -> http://localhost:8080/api/connect4/lobby/{lobbyCode}/start
    @PostMapping("/{lobbyCode}/start")
    public ResponseEntity<?> startGame(@PathVariable String lobbyCode, Authentication authentication) {
        try {
            String username = authentication.getName();
            logger.info("User '{}' is trying to start game for lobby with code: {}", username, lobbyCode);
            Object result = lobbyService.startGame(lobbyCode, username);

            // return 200 OK with the updated lobby data
            return ResponseEntity.ok(result);
        } catch (EntityNotFoundException e) {
            logger.warn("Lobby start failed: User '{}' not found ", authentication.getName());
            // return 404 Not Found with error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Lobby start failed: {}", e.getMessage());
            // return 400 Bad Request with error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error starting game: {}", e.getMessage());
            // return 500 Internal Server Error with error message
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error starting game"));
        }
    }

    // post request for leaving a lobby
    // POST -> http://localhost:8080/api/connect4/lobby/{lobbyCode}/leave
    @PostMapping("/{lobbyCode}/leave")
    public ResponseEntity<?> leaveLobby(@PathVariable String lobbyCode, Authentication authentication) {
        try {
            String username = authentication.getName();
            logger.info("User '{}' is trying to leave lobby with code: {}", username, lobbyCode);
            Optional<Lobby> lobbyOpt = lobbyService.leaveLobby(username, lobbyCode);
            return lobbyOpt
                    .map(lobby -> ResponseEntity.ok(LobbyDTO.fromEntity(lobby)))
                    .orElseGet(() -> ResponseEntity.noContent().build());
        } catch (EntityNotFoundException e) {
            logger.warn("Lobby leave failed: User '{}' not found ", authentication.getName());
            // return 404 Not Found with error message
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Lobby leave failed: {}", e.getMessage());
            // return 400 Bad Request with error message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error leaving lobby: {}", e.getMessage());
            // return 500 Internal Server Error with error message
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error leaving lobby"));
        }
    }

}
