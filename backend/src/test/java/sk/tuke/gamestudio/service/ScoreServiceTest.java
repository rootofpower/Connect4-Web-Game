// package sk.tuke.gamestudio.service;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import sk.tuke.gamestudio.entity.Score;
// import sk.tuke.gamestudio.service.interfaces.ScoreService;

// import java.util.Date;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// @SpringBootTest
// @ActiveProfiles("test")
// public class ScoreServiceTest {

// @Autowired
// private ScoreService scoreService;
// String game = "connect4";
// @Test
// void reset() {
// scoreService.reset(game);
// assertEquals(0, scoreService.getTopScores(game).size());
// }

// @Test
// void addScore(){
// scoreService.reset(game);
// scoreService.addScore(new Score(game, "marko", 100, new Date()));
// var scores = scoreService.getTopScores(game);
// assertEquals(1, scores.size());
// var score = scores.get(0);
// assertEquals("marko", score.getPlayer());
// assertEquals(game, score.getGame());
// assertEquals(100, score.getPoints());
// scoreService.reset(game);
// }

// @Test
// void getTopScores() {
// scoreService.reset(game);
// scoreService.addScore(new Score(game, "marko", 100, new Date()));
// scoreService.addScore(new Score(game, "janko", 200, new Date()));
// scoreService.addScore(new Score(game, "misko", 300, new Date()));
// scoreService.addScore(new Score(game, "janko", 100, new Date()));
// var scores = scoreService.getTopScores(game);
// assertEquals(3, scores.size());

// var score = scores.get(0);
// assertEquals("misko", score.getPlayer());
// assertEquals(game, score.getGame());
// assertEquals(300, score.getPoints());

// score = scores.get(1);
// assertEquals("janko", score.getPlayer());
// assertEquals(game, score.getGame());
// assertEquals(300, score.getPoints());
// score = scores.get(2);
// assertEquals("marko", score.getPlayer());
// assertEquals(game, score.getGame());
// assertEquals(100, score.getPoints());
// scoreService.reset(game);
// }

// }