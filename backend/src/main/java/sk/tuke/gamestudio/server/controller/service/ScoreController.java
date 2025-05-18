package sk.tuke.gamestudio.server.controller.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.dto.requests.AddScoreRequest;
import sk.tuke.gamestudio.dto.ScoreDTO;
import sk.tuke.gamestudio.service.interfaces.ScoreService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;
    private static final Logger log = LoggerFactory.getLogger(ScoreController.class);

    // post request for adding score
    // POST -> http://localhost:8080/api/score
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addScore(@Valid @RequestBody AddScoreRequest request, Authentication authentication) {
        try {
            String username = authentication.getName(); // Отримуємо ім'я поточного користувача
            ScoreDTO savedScoreDTO = scoreService.addScore(username, request);
            log.info("API: Score added for user '{}', game '{}'", username, request.getGame());
            // return 201 Created with the saved score
            return ResponseEntity.status(HttpStatus.CREATED).body(savedScoreDTO);
        } catch (EntityNotFoundException e) {
            log.warn("API addScore failed: {}", e.getMessage());
            // return 404 Not Found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("API addScore failed due to invalid input: {}", e.getMessage());
            // return 400 Bad Request
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("API error adding score: {}", e.getMessage(), e);
            // return 500 Internal Server Error
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to add score due to an internal error."));
        }
    }

    // get request for getting top scores
    // GET -> http://localhost:8080/api/score/{game}
    @GetMapping("/{game}")
    public ResponseEntity<?> getTopScores(@PathVariable String game) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Game name cannot be blank."));
        }
        try {
            List<ScoreDTO> topScores = scoreService.getTopScores(game);
            return ResponseEntity.ok(topScores);
        } catch (Exception e) {
            log.error("API error retrieving top scores for game {}: {}", game, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve top scores."));
        }
    }

    // delete request for deleting score
    // DELETE -> http://localhost:8080/api/score/{game}
    @DeleteMapping("/{game}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetScores(@PathVariable String game) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            scoreService.resetScores(game);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("API error resetting scores for game {}: {}", game, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}