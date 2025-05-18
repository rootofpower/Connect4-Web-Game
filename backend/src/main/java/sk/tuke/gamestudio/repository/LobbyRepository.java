package sk.tuke.gamestudio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.tuke.gamestudio.entity.Lobby;
import sk.tuke.gamestudio.entity.enums.LobbyStatus;

@Repository
public interface LobbyRepository extends JpaRepository<Lobby, Long> {

    Optional<Lobby> findByLobbyCode(String lobbyCode);

    boolean existsByLobbyCode(String lobbyCode);

    List<Lobby> findByStatus(LobbyStatus status);

    Optional<Lobby> findByGameSessionId(Long gameSessionId);
}
