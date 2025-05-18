package sk.tuke.gamestudio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.tuke.gamestudio.entity.Score;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findTop10ByGameOrderByPointsDesc(String game);

    @Modifying
    @Query("DELETE FROM Score s WHERE s.game = :game")
    void resetGameScores(@Param("game") String game);

}
