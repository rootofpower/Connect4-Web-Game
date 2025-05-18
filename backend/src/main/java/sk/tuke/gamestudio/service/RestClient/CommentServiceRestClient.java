// package sk.tuke.gamestudio.service.RestClient;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.web.client.RestClientException;
// import org.springframework.web.client.RestTemplate;
// import sk.tuke.gamestudio.entity.Comment;
// import sk.tuke.gamestudio.service.exceptions.CommentException;
// import sk.tuke.gamestudio.service.interfaces.CommentService;

// import java.util.Collections;
// import java.util.List;

// public class CommentServiceRestClient implements CommentService {
// @Value("${remote.server.api}")
// private String url;

// @Autowired
// private RestTemplate restTemplate;

// @Override
// public void setComment(Comment comment) {
// if (comment == null || comment.getGame() == null ||
// comment.getGame().trim().isEmpty()) {
// throw new CommentException("Invalid comment or game name: setComment, CSRC");
// }
// String postUrl = url + "/" + comment.getGame() + "/comment";
// try {
// var response = restTemplate.postForEntity(postUrl, comment, Comment.class);
// if (response.getBody() != null) {
// comment.setId(response.getBody().getId());
// }
// } catch (RestClientException ex) {
// throw new CommentException("Error while posting comment: CSRC", ex);
// }
// }

// @Override
// public Comment getComment(String game, String player, int id) {
// if (game == null || game.trim().isEmpty() || player == null ||
// player.trim().isEmpty() || id <= 0) {
// System.out.println("DEBUG LOG: GAME = " + game + "; PLAYER: " + player + ";
// ID: " + id);
// throw new CommentException("Invalid game name or player name: getComment,
// CSRC");
// }
// String getUrl = url + "/" + game + "/comment/" + id + "?player=" + player;
// return restTemplate.getForObject(getUrl, Comment.class);
// }

// @Override
// public void deleteComment(String game, String player, int id) {
// if (game == null || game.trim().isEmpty() || player == null ||
// player.trim().isEmpty() || id <= 0) {
// throw new CommentException("Invalid game name or player name: deleteComment,
// CSRC");
// }
// String deleteUrl = url + "/" + game + "/comment/" + id + "?player=" + player;
// restTemplate.delete(deleteUrl);
// }

// @Override
// public void clearComments(String game) {
// if (game == null || game.trim().isEmpty()) {
// throw new CommentException("Invalid game name: clearComments, CSRC");
// }
// String deleteUrl = url + "/" + game + "/comment";
// try {
// restTemplate.delete(deleteUrl);
// } catch (RestClientException ex) {
// throw new CommentException("Error while clearing comments: CSRC", ex);
// }
// }

// @Override
// public List<Comment> getComments(String game) {
// if (game == null || game.trim().isEmpty()) {
// throw new CommentException("Invalid game name: getComments, CSRC");
// }
// String getUrl = url + "/" + game + "/comment";
// try {
// Comment[] comments = restTemplate.getForObject(getUrl, Comment[].class);
// if (comments == null) {
// return Collections.emptyList();
// } else {
// return List.of(comments);
// }
// } catch (RestClientException ex) {
// throw new CommentException("Error while getting comments: CSRC", ex);
// }
// }
// }
