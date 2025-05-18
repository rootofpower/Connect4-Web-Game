package sk.tuke.gamestudio.service.interfaces;

import java.util.Optional;

import sk.tuke.gamestudio.entity.Lobby;

public interface LobbyService {

    Lobby createLobby(String hostName, String gameName);

    Lobby joinLobby(String username, String lobbyCode);

    Optional<Lobby> leaveLobby(String username, String lobbyCode);

    Lobby getLobbyState(String lobbyCode);

    Lobby setPlayerReady(String username, String lobbyCode, boolean isReady);

    Object startGame(String lobbyCode, String hostName);

    void deleteLobby(String lobbyCode);

    void deleteLobbyByGameSessionId(Long gameSessionId);
}
