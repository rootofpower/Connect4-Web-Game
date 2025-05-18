package sk.tuke.gamestudio.entity;

import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.tuke.gamestudio.entity.enums.LobbyStatus;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lobbies", indexes = {
        @Index(name = "idx_lobby_code", columnList = "lobby_code", unique = true),
})
@ToString(exclude = { "hostUser", "opponentUser" })
public class Lobby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String lobbyCode;

    // host user is not optional, because the lobby must have a host
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_user_id", nullable = false)
    private User hostUser;

    // opponent user is optional, because the lobby can be created without an
    // opponent
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_user_id")
    private User opponentUser;

    @Column(nullable = false)
    private boolean hostReady = false;

    @Column(nullable = false)
    private boolean opponentReady = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LobbyStatus status;

    @Column(nullable = false)
    private String gameName;

    @Column(name = "game_session_id")
    private Long gameSessionId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Lobby(String lobbyCode, User hostUser, String gameName) {
        this.lobbyCode = lobbyCode;
        this.hostUser = hostUser;
        this.gameName = gameName;
        this.status = LobbyStatus.WAITING_FOR_OPPONENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Lobby lobby = (Lobby) o;
        return id != null && Objects.equals(id, lobby.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
