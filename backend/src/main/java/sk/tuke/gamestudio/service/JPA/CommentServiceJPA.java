package sk.tuke.gamestudio.service.JPA;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.dto.requests.AddCommentRequest;
import sk.tuke.gamestudio.dto.CommentDTO;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.Role;
import sk.tuke.gamestudio.repository.CommentRepository;
import sk.tuke.gamestudio.service.interfaces.CommentService;
import sk.tuke.gamestudio.service.interfaces.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceJPA implements CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(CommentServiceJPA.class);

    @Override
    public CommentDTO addComment(String username, AddCommentRequest request) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        log.info("Adding comment for game '{}' by user '{}'", request.getGame(), username);
        Comment newComment = new Comment(request.getGame(), user, request.getComment());

        Comment savedComment = commentRepository.save(newComment);

        return CommentDTO.fromEntity(savedComment);
    }

    @Override
    public void deleteComment(String username, Long commentId) {
        log.info("Attempting to delete comment with ID {} by user '{}'", commentId, username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        User requestingUser = userService.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        boolean isAuthor = Objects.equals(comment.getUser().getId(), requestingUser.getId());
        boolean isAdmin = requestingUser.getRole() == Role.ADMIN;

        if (!isAuthor && !isAdmin) {
            log.warn("User '{}' denied access to delete comment {}", username, commentId);
            throw new AccessDeniedException("User does not have permission to delete this comment.");
        }

        commentRepository.delete(comment);
        log.info("Comment with ID {} deleted successfully by user '{}'", commentId, username);
    }

    @Override
    public void clearCommentsByGame(String game) {
        log.warn("Clearing all comments for game: {}", game);
        long deletedCount = commentRepository.deleteByGame(game);
        log.info("Cleared {} comments for game: {}", deletedCount, game);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByGame(String game) {
        log.debug("Fetching comments for game: {}", game);
        List<Comment> comments = commentRepository.findByGameOrderByCommentedAtDesc(game);
        return comments.stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

}
