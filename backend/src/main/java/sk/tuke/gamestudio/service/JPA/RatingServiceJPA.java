package sk.tuke.gamestudio.service.JPA;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.dto.AverageRatingDTO;
import sk.tuke.gamestudio.dto.requests.SetRatingRequest;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.User;
import sk.tuke.gamestudio.repository.RatingRepository;
import sk.tuke.gamestudio.service.interfaces.RatingService;
import sk.tuke.gamestudio.service.interfaces.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RatingServiceJPA implements RatingService {
    private final RatingRepository ratingRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(RatingServiceJPA.class);

    @Override
    public AverageRatingDTO setRating(String username, SetRatingRequest request) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        String game = request.getGame();
        Double newRatingValue = request.getRating();

        log.info("User '{}' setting rating {} for game '{}'", username, newRatingValue, game);

        Optional<Rating> existingRatingOpt = ratingRepository.findByUserAndGame(user, game);

        Rating ratingToSave;
        if (existingRatingOpt.isPresent()) {
            ratingToSave = existingRatingOpt.get();
            log.debug("Updating existing rating for user '{}', game '{}'. Old value: {}", username, game,
                    ratingToSave.getRating());
            ratingToSave.setRating(newRatingValue);
        } else {
            log.debug("Creating new rating for user '{}', game '{}'.", username, game);
            ratingToSave = new Rating(game, user, newRatingValue);
        }

        ratingRepository.save(ratingToSave);
        log.info("Rating saved successfully for user '{}', game '{}'.", username, game);

        return getAverageRating(game);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Double> getRatingByUserAndGame(String username, String game) {
        Optional<User> userOpt = userService.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        return ratingRepository.findByUserAndGame(userOpt.get(), game)
                .map(Rating::getRating);
    }

    @Override
    @Transactional(readOnly = true)
    public AverageRatingDTO getAverageRating(String game) {
        log.debug("Calculating average rating for game: {}", game);
        Double average = ratingRepository.findAverageRatingByGame(game);
        long count = ratingRepository.countByGame(game);

        double avgValue = (average != null) ? average : 0.0;

        log.debug("Average rating for game '{}' is {} based on {} ratings.", game, avgValue, count);
        return new AverageRatingDTO(game, avgValue, count);
    }

    @Override
    public void deleteRating(String username, String game) {
        User user = userService.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        log.info("Attempting to delete rating for user '{}' and game '{}'", username, game);
        Optional<Rating> ratingOpt = ratingRepository.findByUserAndGame(user, game);

        ratingOpt.ifPresentOrElse(
                rating -> {
                    ratingRepository.delete(rating);
                    log.info("Rating deleted successfully for user '{}' and game '{}'", username, game);
                },
                () -> {
                    log.warn("Rating not found for user '{}' and game '{}'. Nothing deleted.", username, game);
                });
    }
}
