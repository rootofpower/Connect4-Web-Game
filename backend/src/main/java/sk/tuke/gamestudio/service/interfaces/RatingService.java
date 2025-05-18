package sk.tuke.gamestudio.service.interfaces;

import java.util.Optional;

import sk.tuke.gamestudio.dto.AverageRatingDTO;
import sk.tuke.gamestudio.dto.requests.SetRatingRequest;

public interface RatingService {

    Optional<Double> getRatingByUserAndGame(String username, String game);

    AverageRatingDTO getAverageRating(String game);

    AverageRatingDTO setRating(String username, SetRatingRequest request);

    void deleteRating(String username, String game);
}
