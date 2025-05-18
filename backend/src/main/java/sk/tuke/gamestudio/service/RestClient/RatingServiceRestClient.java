// package sk.tuke.gamestudio.service.RestClient;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.web.client.HttpClientErrorException;
// import org.springframework.web.client.RestClientException;
// import org.springframework.web.client.RestTemplate;
// import sk.tuke.gamestudio.entity.Rating;
// import sk.tuke.gamestudio.service.exceptions.RatingException;
// import sk.tuke.gamestudio.service.interfaces.RatingService;

// public class RatingServiceRestClient implements RatingService {
// @Value("${remote.server.api}")
// private String url;

// @Autowired
// private RestTemplate restTemplate;

// @Override
// public void setRating(Rating rating) {
// if (rating == null || rating.getGame() == null ||
// rating.getGame().trim().isEmpty()) {
// throw new RatingException("Invalid rating or game name: setRating");
// }
// String postUrl = url + "/" + rating.getGame() + "/rating";
// try {
// restTemplate.postForEntity(postUrl, rating, Rating.class);
// } catch (RestClientException ex) {
// throw new RatingException("Error while posting rating", ex);
// }
// }

// @Override
// public double getRating(String game, String player) {
// if (game == null || game.trim().isEmpty() || player == null ||
// player.trim().isEmpty()) {
// throw new RatingException("Invalid game name or player name: getRating");
// }
// String getUrl = url + "/" + game + "/rating/" + player;

// try {
// Double rating = restTemplate.getForObject(getUrl, Double.class);
// return (rating != null) ? rating : 0.0;
// } catch (HttpClientErrorException.NotFound ex) {
// throw new RatingException("Rating not found", ex);
// } catch (RestClientException ex) {
// throw new RatingException("Error while getting rating", ex);
// }

// }

// @Override
// public double getAverageRating(String game) {
// if (game == null || game.trim().isEmpty()) {
// throw new RatingException("Invalid game name: getAverageRating");
// }
// String getUrl = url + "/" + game + "/rating";
// try {
// Double averageRating = restTemplate.getForObject(getUrl, Double.class);
// return (averageRating != null) ? averageRating : 0.0;
// } catch (HttpClientErrorException.NotFound ex) {
// return 0.0;
// } catch (RestClientException ex) {
// throw new RatingException("Error while getting average rating for game '" +
// game + "'", ex);
// }
// }

// @Override
// public void removeRating(String game) {
// if (game == null || game.trim().isEmpty()) {
// throw new RatingException("Invalid game name: removeRating");
// }
// String deleteUrl = url + "/" + game + "/rating";
// try {
// restTemplate.delete(deleteUrl);
// } catch (Exception ex) {
// throw new RatingException("Error while deleting rating", ex);
// }
// }
// }
