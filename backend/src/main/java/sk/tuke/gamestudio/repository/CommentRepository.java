package sk.tuke.gamestudio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByGameOrderByCommentedAtDesc(String game);

    Optional<Comment> findByIdAndUser(Long id, User user);

    long deleteByGame(String game);
}
