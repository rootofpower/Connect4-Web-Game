package sk.tuke.gamestudio.entity;

import java.time.Instant;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = { "user" })
@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "game" })
}, indexes = {
        @Index(name = "idx_rating_user_game", columnList = "user_id, game"),
        @Index(name = "idx_rating_game", columnList = "game")
})
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String game;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(nullable = false)
    private double rating;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @UpdateTimestamp
    @Column(name = "rated_at", nullable = false)
    private Instant ratedAt;

    public Rating(String game, User user, double rating) {
        this.game = game;
        this.user = user;
        this.rating = rating;
    }
}
