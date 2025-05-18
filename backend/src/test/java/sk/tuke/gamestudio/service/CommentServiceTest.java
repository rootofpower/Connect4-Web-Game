// package sk.tuke.gamestudio.service;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import sk.tuke.gamestudio.entity.Comment;
// import sk.tuke.gamestudio.service.interfaces.CommentService;

// import java.util.Date;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// @SpringBootTest
// @ActiveProfiles("test")
// public class CommentServiceTest {

// @Autowired
// private CommentService commentService;
// String game = "connect4";

// @Test
// public void clearComments() {
// commentService.clearComments(game);
// commentService.setComment(new Comment(game, "marko1", "test1", new Date()));
// commentService.setComment(new Comment(game, "marko2", "test2", new Date()));
// commentService.setComment(new Comment(game, "marko3", "test3", new Date()));
// commentService.clearComments(game);
// assertEquals(0, commentService.getComments(game).size());
// }

// @Test
// public void setComment() {
// commentService.clearComments(game);
// commentService.setComment(new Comment(game, "marko1", "test1", new Date()));
// var comments = commentService.getComments(game);
// assertEquals(1, comments.size());
// var comment = comments.get(0);
// assertEquals("marko1", comment.getPlayer());
// assertEquals(game, comment.getGame());
// assertEquals("test1", comment.getComment());
// commentService.clearComments(game);
// }

// @Test
// public void getComment() {
// commentService.clearComments(game);
// Comment getCommentTest = new Comment(game, "marko2", "test2", new Date());
// commentService.setComment(getCommentTest);
// int testId = getCommentTest.getId();
// var comment = commentService.getComment(game, "marko2", testId);
// assertEquals("marko2", comment.getPlayer());
// assertEquals(game, comment.getGame());
// assertEquals("test2", comment.getComment());
// commentService.clearComments(game);
// }

// @Test
// public void getComments() {
// commentService.clearComments(game);
// commentService.setComment(new Comment(game, "marko3", "test3", new Date()));
// commentService.setComment(new Comment(game, "janko", "test3", new Date()));
// var comments = commentService.getComments(game);
// assertEquals(2, comments.size());
// var comment = comments.get(0);
// assertEquals("marko3", comment.getPlayer());
// assertEquals(game, comment.getGame());
// assertEquals("test3", comment.getComment());
// comment = comments.get(1);
// assertEquals("janko", comment.getPlayer());
// assertEquals(game, comment.getGame());
// assertEquals("test3", comment.getComment());
// commentService.clearComments(game);
// }

// @Test
// public void deleteComment() {
// commentService.clearComments(game);
// commentService.setComment(new Comment(game, "marko4", "test4", new Date()));
// commentService.setComment(new Comment(game, "janko", "test4", new Date()));
// var comments = commentService.getComments(game);
// assertEquals(2, comments.size());
// var comment = comments.get(0);
// assertEquals("marko4", comment.getPlayer());
// assertEquals(game, comment.getGame());
// assertEquals("test4", comment.getComment());
// int testId = comment.getId();
// commentService.deleteComment(game, "marko4", testId);
// comments = commentService.getComments(game);
// assertEquals(1, comments.size());
// commentService.clearComments(game);
// }

// }
