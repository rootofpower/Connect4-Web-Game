package sk.tuke.gamestudio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.User;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndGame(User user, String game);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.game = :game")
    Double findAverageRatingByGame(@Param("game") String game);

    long countByGame(String game);

    Optional<Rating> findByUser_UsernameAndGame(String username, String game);

}
