package sk.tuke.gamestudio.service.JPA;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.dto.requests.AddScoreRequest;
import sk.tuke.gamestudio.dto.ScoreDTO;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.repository.ScoreRepository;
import sk.tuke.gamestudio.service.interfaces.ScoreService;
import sk.tuke.gamestudio.service.interfaces.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScoreServiceJPA implements ScoreService {

    private final ScoreRepository scoreRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(ScoreServiceJPA.class);

    @Override
    public ScoreDTO addScore(String username, AddScoreRequest request) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (request == null || request.getGame() == null || request.getGame().isBlank()) {
            throw new IllegalArgumentException("Game name cannot be null or blank");
        }
        if (request.getPoints() < 0) {
            throw new IllegalArgumentException("Points cannot be negative");
        }

        log.info("Adding score for user '{}' in game '{}' with points {}",
                username, request.getGame(), request.getPoints());

        Score score = new Score(request.getGame(), user, request.getPoints());

        Score savedScore = scoreRepository.save(score);
        log.debug("Score saved with ID: {}", savedScore.getId());

        return ScoreDTO.fromEntity(savedScore);
    }

    @Override
    @Transactional(readOnly = true) // Оптимізація читання
    public List<ScoreDTO> getTopScores(String game) {
        if (game == null || game.isBlank()) {
            log.warn("Attempted to get top scores for null/blank game name");
            return List.of();
        }
        log.debug("Fetching top 10 scores for game: {}", game);
        List<Score> topScores = scoreRepository.findTop10ByGameOrderByPointsDesc(game);

        return topScores.stream()
                .map(ScoreDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void resetScores(String game) {
        if (game == null || game.isBlank()) {
            log.warn("Attempted to reset scores for null/blank game name");
            throw new IllegalArgumentException("Game name cannot be blank for reset");
        }
        log.warn("Resetting all scores for game: {}", game);
        scoreRepository.resetGameScores(game);
    }
}