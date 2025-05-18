package sk.tuke.gamestudio.server.controller.service;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import sk.tuke.gamestudio.dto.GameSessionDTO;
import sk.tuke.gamestudio.dto.requests.MakeMoveRequest;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.service.interfaces.GameService;
import sk.tuke.gamestudio.service.interfaces.UserService;

@RestController
@RequestMapping("/api/connect4/game")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class GameController {

    private final GameService gameService;
    private final UserService userService;
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(GameController.class);

    // get request for game state
    // GET -> http://localhost:8080/api/connect4/game/{gameId}/state
    @GetMapping("/{gameId}/state")
    public ResponseEntity<?> getGameState(@PathVariable Long gameId, Authentication authentication) {
        try {
            String username = authentication.getName();
            logger.debug("User {} is requesting game state for gameId: {}", username, gameId);
            GameSessionDTO gameState = gameService.getGameState(gameId, username);

            logger.info("Successfully retrieved game state for gameId: {}", gameId);
            return ResponseEntity.ok(gameState);
        } catch (EntityNotFoundException e) {
            logger.error("Game state not found for gameId: {}", gameId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            logger.warn("Access denied for user '{}' trying to get state for game {}: {}", authentication.getName(),
                    gameId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving game state for gameId: {}", gameId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error fetching game state."));
        }
    }

    // post request for make move
    // POST -> http://localhost:8080/api/connect4/game/{gameId}/move
    // @PostMapping("/{gameId}/move")
    // public ResponseEntity<?> makeMove(@PathVariable Long gameId, @RequestBody
    // MakeMoveRequest moveRequest,
    // Authentication authentication, HttpServletRequest request) {
    // String contentType = request.getContentType();
    // logger.info("Received request Content-Type: {}", contentType);
    // logger.info("Received request body: {}", moveRequest);

    // logger.debug("Received move request for gameId: {} with column: {}", gameId,
    // moveRequest.getColumn());
    // try {
    // String username = authentication.getName();
    // User user = userService.findUserByUsername(username)
    // .orElseThrow(() -> new EntityNotFoundException("User not found" + username));
    // Long userId = user.getId();
    // int column = moveRequest.getColumn();
    // logger.info("User {} is making a move in gameId: {} at column: {}", username,
    // gameId, column);
    // GameSessionDTO gameState = gameService.makeMove(gameId, userId, column);
    // logger.info("Successfully made move in gameId: {} at column: {}", gameId,
    // column);
    // return ResponseEntity.ok(gameState);
    // } catch (EntityNotFoundException e) {
    // // player of game not found, return 404
    // logger.error("Game state not found for gameId: {}", gameId, e);
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",
    // e.getMessage()));
    // } catch (IllegalStateException e) {
    // // game is not in a valid state, return 409
    // logger.error("Game is not in a valid state for gameId: {}", gameId, e);
    // return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",
    // e.getMessage()));
    // } catch (IllegalArgumentException e) {
    // // invalid move, return 400
    // // logger.error("Invalid move in gameId: {} at column: {}", gameId,
    // // moveRequest.getColumn(), e);
    // return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    // } catch (Exception e) {
    // // internal server error, return 500
    // // logger.error("Error making move in gameId: {} at column: {}", gameId,
    // // moveRequest.getColumn(), e);
    // return ResponseEntity.internalServerError()
    // .body(Map.of("error", "Internal server error making move."));
    // }
    // }
    @PostMapping("/{gameId}/move")
    public ResponseEntity<?> makeMove(@PathVariable Long gameId,
            // @RequestBody MakeMoveRequest moveRequest, // Тимчасово закоментуй
            Authentication authentication,
            HttpServletRequest request) { // request вже є
        String contentType = request.getContentType();
        logger.info("Received request Content-Type: {}", contentType);

        StringBuilder requestBodyBuilder = new StringBuilder();
        String rawRequestBody = "";
        try (java.io.BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBodyBuilder.append(line);
            }
            rawRequestBody = requestBodyBuilder.toString();
        } catch (java.io.IOException e) {
            logger.error("Error reading request body directly: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not read request body"));
        }

        logger.info("CONTROLLER - RAW Request Body String: \"{}\"", rawRequestBody); // Дуже важливий лог!

        // Тепер спробуємо розпарсити цей rawRequestBody вручну
        if (rawRequestBody != null && !rawRequestBody.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                MakeMoveRequest parsedDto = objectMapper.readValue(rawRequestBody, MakeMoveRequest.class); // Використовуй
                                                                                                           // свій DTO
                logger.info("CONTROLLER - Manually parsed DTO: {}", parsedDto); // Що тут?

                // Якщо тут parsedDto.getColumn() має значення, то проблема в автоматичній
                // обробці Spring
                // Якщо і тут null/0, то проблема або в самому JSON, або в DTO/Jackson

                // Твоя подальша логіка (використовуючи parsedDto.getColumn())
                String username = authentication.getName();
                User user = userService.findUserByUsername(username)
                        .orElseThrow(() -> new EntityNotFoundException("User not found" + username));
                Long userId = user.getId();
                // УВАГА: parsedDto.getColumn() може бути null, якщо тип Integer. Потрібна
                // перевірка.
                int column = (parsedDto.getColumn() != null) ? parsedDto.getColumn() : -1; // -1 як індикатор помилки
                if (column == -1 && parsedDto.getColumn() == null) {
                    logger.warn("Column was null after manual parsing!");
                }

                logger.info("User {} is making a move in gameId: {} at column: {}", username, gameId, column);
                GameSessionDTO gameState = gameService.makeMove(gameId, userId, column);
                logger.info("Successfully made move in gameId: {} at column: {}", gameId, column);
                return ResponseEntity.ok(gameState);

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                logger.error("Error manually parsing JSON: " + rawRequestBody, e);
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON format: " + e.getMessage()));
            } catch (Exception e) { // Інші помилки після парсингу
                logger.error("Error in business logic after manual parsing for gameId: {} ", gameId, e);
                return ResponseEntity.internalServerError()
                        .body(Map.of("error", "Internal server error after manual parsing."));
            }
        } else {
            logger.warn("Request body was empty or null after direct reading.");
            return ResponseEntity.badRequest().body(Map.of("error", "Request body is empty (read directly)."));
        }
    }
}
