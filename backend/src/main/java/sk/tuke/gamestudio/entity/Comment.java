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
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_game_time", columnList = "game, commented_at DESC"),
        @Index(name = "idx_comment_user", columnList = "user_id")
})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String game;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(name = "commented_at", nullable = false, updatable = false)
    private Instant commentedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment(String game, User user, String comment) {
        this.game = game;
        this.user = user;
        this.comment = comment;
    }
}
