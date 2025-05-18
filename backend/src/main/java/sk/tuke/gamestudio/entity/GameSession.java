package sk.tuke.gamestudio.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import sk.tuke.gamestudio.entity.enums.GameStatus;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString(exclude = { "player1", "player2" })
@EqualsAndHashCode(of = { "id" })
@Table(name = "game_sessions", indexes = {
        @Index(name = "ixd_gamesession_status", columnList = "status")
})
public class GameSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String gameName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player1_user_id", nullable = false)
    private User player1;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "player2_user_id", nullable = false)
    private User player2;

    @Lob
    @Column(name = "board_state", nullable = false, columnDefinition = "TEXT")
    private String boardState;

    @Column(nullable = false, length = 100)
    private String currentPlayerUsername;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GameStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    public GameSession(String gameName, User player1, User player2, String initialBoardState) {
        this.gameName = gameName;
        this.player1 = player1;
        this.player2 = player2;
        this.boardState = initialBoardState;
        this.status = GameStatus.PLAYER_1_TURN;
        this.currentPlayerUsername = player1.getUsername();
    }

}