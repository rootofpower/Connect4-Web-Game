package sk.tuke.gamestudio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sk.tuke.gamestudio.entity.GameSession;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.entity.enums.GameStatus;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
    @Query("SELECT gs FROM GameSession gs WHERE gs.gameName = :gameName " +
            "AND (gs.player1.username = :username OR gs.player2.username = :username) " +
            "AND gs.status IN (sk.tuke.gamestudio.entity.enums.GameStatus.PLAYER_1_TURN, sk.tuke.gamestudio.entity.enums.GameStatus.PLAYER_2_TURN)")
        // Важливо: вказати повний шлях до enum в JPQL або зареєструвати його інакше
        // Альтернатива, якщо EnumType.STRING: AND gs.status IN ('PLAYER_1_TURN', 'PLAYER_2_TURN')
    Optional<GameSession> findActiveByUsernameAndGameName(@Param("username") String username, @Param("gameName") String gameName);

    @Modifying // Необхідно для DELETE/UPDATE запитів
    @Query("DELETE FROM GameSession gs WHERE gs.gameName = :gameName AND (gs.player1.username = :username OR gs.player2.username = :username)")
    void deleteByUsernameAndGameName(@Param("username") String username, @Param("gameName") String gameName);

    @Modifying
    @Query("DELETE FROM GameSession gs WHERE gs.player1.username = :username OR gs.player2.username = :username")
    void deleteByUsername(@Param("username") String username);

    @Modifying
    void deleteByGameName(String game);

    List<GameSession> findByPlayer1OrPlayer2AndStatusIn(User player1, User player2, List<GameStatus> statuses);}
