package sk.tuke.gamestudio.server.controller.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import sk.tuke.gamestudio.dto.PlayerStatsDTO;
import sk.tuke.gamestudio.entity.PlayerStats;
import sk.tuke.gamestudio.service.interfaces.PlayerStatsService;

@RestController
@RequestMapping("/api/connect4")
@RequiredArgsConstructor
@Slf4j
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;
    private static final Logger log = LoggerFactory.getLogger(PlayerStatsController.class);
    // get request for leaderboard
    // GET -> http://localhost:8080/api/connect4/leaderboard?sortBy=mmr&limit=10
    // GET -> http://connect4-rop.duckdns.org/api/connect4/leaderboard?sortBy=mmr&limit=10
    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerStatsDTO>> getLeaderboard(
            @RequestParam String game,
            @RequestParam(required = false, defaultValue = "mmr") String sortBy,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        log.info("Received request for leaderboard with sortBy: {} and limit: {} and game: {}", sortBy, limit, game);
        if (limit <= 0) {
            limit = 10;
        }
        
        List<PlayerStats> leaderboardData = playerStatsService.getLeaderboard(game, sortBy, limit);

        List<PlayerStatsDTO> leaderboardDTO = leaderboardData.stream()
                .map(PlayerStatsDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderboardDTO);
        // return 200 OK with the leaderboard data
    }

    // get request for player stats
    // GET -> http://localhost:8080/api/connect4/leaderboard/player/{username}
    @GetMapping("/leaderboard/player/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlayerStatsDTO> getPlayerStats(@PathVariable String game, @PathVariable String username) {
        Optional<PlayerStats> playerStats = playerStatsService.getPlayerStats(username, game);

        return playerStats
                .map(PlayerStatsDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // delete request for player stats
    // DELETE -> http://localhost:8080/api/connect4/leaderboard/player/{username}
    @DeleteMapping("/leaderboard/player/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> resetPlayerStats(@PathVariable String game, @PathVariable String username) {
        playerStatsService.resetPlayerStats(username, game);
        return ResponseEntity.noContent().build();
    }

    // delete request for all player stats
    // DELETE -> http://localhost:8080/api/connect4/leaderboard
    @DeleteMapping("/leaderboard")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<Void> resetAllPlayerStats(@PathVariable String game) {
        playerStatsService.resetAllPlayerStats(game);
        return ResponseEntity.noContent().build();
    }

}
