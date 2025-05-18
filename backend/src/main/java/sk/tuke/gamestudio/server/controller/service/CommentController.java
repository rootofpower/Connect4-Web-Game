package sk.tuke.gamestudio.server.controller.service;

import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*; // Імпорт анотацій
import sk.tuke.gamestudio.dto.CommentDTO;
import sk.tuke.gamestudio.dto.requests.AddCommentRequest;
import sk.tuke.gamestudio.service.interfaces.CommentService;

import java.util.Map;

@RestController
@RequestMapping("/api/connect4/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    // get request for getting comments
    // GET -> http://localhost:8080/api/connect4/comment
    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam String game) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Game name cannot be blank."));
        }
        try {
            log.debug("Request to get comments for game: {}", game);
            // Сервіс вже повертає List<CommentDTO>
            List<CommentDTO> comments = commentService.getCommentsByGame(game);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error retrieving comments for game {}: {}", game, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error retrieving comments."));
        }
    }

    // post request for adding comment
    // POST -> http://localhost:8080/api/connect4/comment
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addComment(@Valid @RequestBody AddCommentRequest request, Authentication authentication) {
        try {
            log.info("Request to add comment: {}", request);
            String username = authentication.getName();
            log.info("User '{}' adding comment for game '{}'", username, request.getGame());
            CommentDTO savedComment = commentService.addComment(username, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
        } catch (EntityNotFoundException e) {
            log.warn("Add comment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Add comment failed due to invalid input: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            log.warn("User '{}' does not have permission to add comment: {}", authentication.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to add a comment."));
        } catch (Exception e) {
            log.error("Error adding comment for user '{}': {}", authentication.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal server error adding comment."));
        }
    }

    // delete request for deleting comment
    // DELETE -> http://localhost:8080/api/connect4/comment/{commentId}
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("User '{}' attempting to delete comment {}", username, commentId);
            commentService.deleteComment(username, commentId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Delete comment failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            log.warn("Access denied for user '{}' trying to delete comment {}: {}", authentication.getName(), commentId,
                    e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to delete this comment."));
        } catch (Exception e) {
            log.error("Error deleting comment {} for user '{}'", commentId, authentication.getName(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error deleting comment."));
        }
    }

    // delete request for deleting all comments by game
    // DELETE -> http://localhost:8080/api/connect4/comment
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> clearCommentsByGame(@RequestParam String game) {
        if (game == null || game.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            log.warn("ADMIN request to clear all comments for game: {}", game);
            commentService.clearCommentsByGame(game);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            log.error("Error clearing comments for game {}: {}", game, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
