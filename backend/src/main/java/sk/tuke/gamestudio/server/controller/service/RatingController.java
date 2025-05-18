package sk.tuke.gamestudio.server.controller.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.dto.AverageRatingDTO;
import sk.tuke.gamestudio.dto.requests.SetRatingRequest;
import sk.tuke.gamestudio.service.interfaces.RatingService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/connect4/rating")
@RequiredArgsConstructor

public class RatingController {

    private final RatingService ratingService;
    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    // get request for getting average rating
    // GET -> http://localhost:8080/api/connect4/rating/average
    @GetMapping("/average")
    public ResponseEntity<?> getAverageRating(@RequestParam String game) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Game name cannot be blank."));
        }
        try {
            log.debug("Request for average rating for game: {}", game);
            AverageRatingDTO avgRating = ratingService.getAverageRating(game);
            return ResponseEntity.ok(avgRating);
        } catch (Exception e) {
            log.error("Error getting average rating for game {}: {}", game, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error retrieving average rating."));
        }
    }

    // get request for getting my rating
    // GET -> http://localhost:8080/api/connect4/rating/my-rating
    @GetMapping("/my-rating")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyRating(@RequestParam String game, Authentication authentication) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Game name cannot be blank."));
        }
        try {
            String username = authentication.getName();
            log.debug("User '{}' requesting their rating for game: {}", username, game);
            Optional<Double> ratingOpt = ratingService.getRatingByUserAndGame(username, game);
            return ratingOpt
                    .map(rating -> ResponseEntity.ok(Map.of("rating", rating)))
                    .orElseGet(() -> ResponseEntity.notFound().build()); // 404 Not Found
        } catch (EntityNotFoundException e) {
            log.warn("Get my rating failed for game {}: User '{}' not found.", game, authentication.getName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            log.warn("Set rating failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting rating for user '{}' and game {}: {}", authentication.getName(), game,
                    e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error retrieving rating."));
        }
    }

    // post request for adding rating
    // POST -> http://localhost:8080/api/connect4/rating
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> setRating(@RequestBody SetRatingRequest request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("User '{}' setting rating for game '{}': {}", username, request.getGame(), request.getRating());
            AverageRatingDTO updatedAverage = ratingService.setRating(username, request);
            return ResponseEntity.ok(updatedAverage);
        } catch (EntityNotFoundException e) {
            log.warn("Set rating failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Set rating failed due to invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            log.warn("Set rating failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error setting rating for user '{}', game '{}': {}", authentication.getName(), request.getGame(),
                    e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error setting rating."));
        }
    }

    // delete request for deleting rating
    // DELETE -> http://localhost:8080/api/connect4/rating
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteRating(@RequestParam String game, Authentication authentication) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            String username = authentication.getName();
            log.info("User '{}' deleting their rating for game '{}'", username, game);
            ratingService.deleteRating(username, game);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Delete rating failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.warn("Failed to delete rating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting rating for user '{}', game '{}': {}", authentication.getName(), game,
                    e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
