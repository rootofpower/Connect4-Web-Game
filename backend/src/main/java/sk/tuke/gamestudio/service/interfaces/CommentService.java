package sk.tuke.gamestudio.service.interfaces;

import sk.tuke.gamestudio.dto.CommentDTO;
import sk.tuke.gamestudio.dto.requests.AddCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDTO addComment(String username, AddCommentRequest request);

    void deleteComment(String username, Long commentId);

    void clearCommentsByGame(String game);

    List<CommentDTO> getCommentsByGame(String game);
}
