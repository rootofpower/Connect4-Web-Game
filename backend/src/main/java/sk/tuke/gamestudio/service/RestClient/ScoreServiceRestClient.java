// package sk.tuke.gamestudio.service.RestClient;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.web.client.RestClientException;
// import org.springframework.web.client.RestTemplate;
// import sk.tuke.gamestudio.entity.Score;
// import sk.tuke.gamestudio.service.exceptions.ScoreException;
// import sk.tuke.gamestudio.service.interfaces.ScoreService;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;

// public class ScoreServiceRestClient implements ScoreService {
// @Value("${remote.server.api}")
// private String url;

// @Autowired
// private RestTemplate restTemplate;

// @Override
// public void addScore(Score score) {
// if (score == null || score.getGame() == null ||
// score.getGame().trim().isEmpty()) {
// throw new ScoreException("Invalid score or game name: addScore");
// }
// String postUrl = url + "/" + score.getGame() + "/score";
// try {
// restTemplate.postForEntity(postUrl, score, Score.class);
// } catch (RestClientException ex) {
// throw new ScoreException("Error while posting score", ex);
// }
// }

// @Override
// public List<Score> getTopScores(String game) {
// if (game == null || game.trim().isEmpty()) {
// throw new ScoreException("Invalid game name: getTopScores");
// }
// String getUrl = url + "/" + game + "/score";
// try {
// Score[] scores = restTemplate.getForObject(getUrl, Score[].class);
// if (scores == null) {
// return Collections.emptyList();
// } else {
// return Arrays.asList(scores);
// }
// } catch (RestClientException ex) {
// throw new ScoreException("Error while getting top scores", ex);
// }
// }

// @Override
// public void reset(String game) {
// if (game == null || game.trim().isEmpty()) {
// throw new ScoreException("Invalid game name: reset");
// }
// String deleteUrl = url + "/" + game + "/score";
// try {
// restTemplate.delete(deleteUrl);
// } catch (Exception ex) {
// throw new ScoreException("Error while deleting scores", ex);
// }
// }
// }
