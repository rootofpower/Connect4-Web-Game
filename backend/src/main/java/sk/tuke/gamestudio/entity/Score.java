package sk.tuke.gamestudio.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(of = { "id" })
@Entity
@Table(name = "scores", indexes = {
        @Index(name = "idx_score_game_points", columnList = "game, points DESC")
})
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Тип ID - Long

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Зв'язок з User, обов'язковий
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String game;

    @Column(nullable = false)
    private int points;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant playedAt;

    public Score(String game, User user, int points) {
        this.game = game;
        this.user = user;
        this.points = points;
    }
}