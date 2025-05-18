package sk.tuke.gamestudio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sk.tuke.gamestudio.entity.Lobby;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LobbyDTO {
    private String lobbyCode;
    private String hostUsername;
    private String opponentUsername;
    private boolean hostReady;
    private boolean opponentReady;
    private String status;
    private String gameName;
    private Long gameSessionId;

    public static LobbyDTO fromEntity(Lobby lobby) {
        if (lobby == null) {
            return null;
        }
        LobbyDTO lobbyDTO = new LobbyDTO();
        lobbyDTO.setLobbyCode(lobby.getLobbyCode());
        lobbyDTO.setGameName(lobby.getGameName());
        lobbyDTO.setStatus(lobby.getStatus().name());
        lobbyDTO.setHostReady(lobby.isHostReady());
        lobbyDTO.setOpponentReady(lobby.isOpponentReady());
        lobbyDTO.setGameSessionId(lobby.getGameSessionId() != null ? lobby.getGameSessionId() : null);
        if (lobby.getHostUser() != null) {
            lobbyDTO.setHostUsername(lobby.getHostUser().getUsername());
        }
        if (lobby.getOpponentUser() != null) {
            lobbyDTO.setOpponentUsername(lobby.getOpponentUser().getUsername());
        } else {
            lobbyDTO.setOpponentUsername(null);
        }
        return lobbyDTO;
    }

}
