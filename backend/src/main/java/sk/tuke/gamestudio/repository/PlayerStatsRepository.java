package sk.tuke.gamestudio.repository;

import sk.tuke.gamestudio.entity.PlayerStats;
import sk.tuke.gamestudio.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {

    Optional<PlayerStats> findByUserAndGame(User user, String game);

    List<PlayerStats> findTop10ByGameOrderByMmrDesc(String game);

    List<PlayerStats> findTop10ByGameOrderByWinsDesc(String game);

    List<PlayerStats> findTop10ByGameOrderByLossesDesc(String game);

    List<PlayerStats> findAllByGame(String game);
}
