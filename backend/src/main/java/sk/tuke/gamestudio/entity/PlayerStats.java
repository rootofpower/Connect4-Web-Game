package sk.tuke.gamestudio.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(of = { "id" })
@Entity
@Table(name = "player_stats", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "game" }) }, indexes = {
                @Index(name = "idx_playerstats_game_mmr", columnList = "game, mmr DESC"),
                @Index(name = "idx_playerstats_game_wins", columnList = "game, wins DESC")
        })
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String game;

    @Column(nullable = false)
    private int wins = 0;

    @Column(nullable = false)
    private int losses = 0;

    @Column(nullable = false)
    private int draws = 0;

    @Column(nullable = false)
    private int mmr = 1000;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date lastPlayedAt = new Date();

    public PlayerStats(User user, String game) {
        this.user = user;
        this.game = game;
        this.lastPlayedAt = new Date();
    }

    public int getTotalGames() {
        return wins + losses + draws;
    }

    public double getWinRate() {
        int totalGames = getTotalGames();
        if (totalGames == 0) {
            return 0.0;
        }
        return (double) wins / totalGames * 100;
    }
}
