package sk.tuke.gamestudio.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.gamestudio.entity.Comment;

@Data
@NoArgsConstructor
public class CommentDTO {

    private Long id;
    private String game;
    private String username;
    private String comment;
    private Instant commentedAt;

    public static CommentDTO fromEntity(Comment comment) {
        if (comment == null)
            return null;
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setGame(comment.getGame());
        dto.setComment(comment.getComment());
        dto.setCommentedAt(comment.getCommentedAt());
        if (comment.getUser() != null) {
            dto.setUsername(comment.getUser().getUsername());
        } else {
            dto.setUsername(null);
        }
        return dto;
    }
}
