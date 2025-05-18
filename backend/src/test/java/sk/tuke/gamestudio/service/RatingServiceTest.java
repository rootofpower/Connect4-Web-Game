// package sk.tuke.gamestudio.service;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
// import sk.tuke.gamestudio.entity.Rating;
// import sk.tuke.gamestudio.service.interfaces.RatingService;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// @SpringBootTest
// @ActiveProfiles("test")
// class RatingServiceTest {
// @Autowired
// private RatingService ratingService;
// String game = "connect4";
// @Test
// void removeRating() {
// ratingService.removeRating(game);
// assertEquals(0, ratingService.getAverageRating(game));
// }

// @Test
// void getRating() {
// ratingService.removeRating(game);
// ratingService.setRating(new Rating(game, "marko", 5));
// var ratings = ratingService.getRating(game, "marko");
// assertEquals(5, ratings);
// ratingService.setRating(new Rating(game, "marko", 3));
// ratings = ratingService.getRating(game, "marko");
// assertEquals(3, ratings);
// ratingService.removeRating(game);
// }

// @Test
// void setRating() {
// ratingService.removeRating(game);
// ratingService.setRating(new Rating(game, "marko", 5));
// var ratings = ratingService.getRating(game, "marko");
// assertEquals(5, ratings);
// ratingService.setRating(new Rating(game, "marko", 3));
// ratings = ratingService.getRating(game, "marko");
// assertEquals(3, ratings);
// ratingService.setRating(new Rating(game, "marko", 4));
// ratings = ratingService.getRating(game, "marko");
// assertEquals(4, ratings);
// ratingService.setRating(new Rating(game, "janko", 2));
// ratings = ratingService.getRating(game, "janko");
// assertEquals(2, ratings);
// ratingService.removeRating(game);
// }

// @Test
// void getAverageRating() {
// ratingService.removeRating(game);
// ratingService.setRating(new Rating(game, "marko", 5));
// ratingService.setRating(new Rating(game, "janko", 3));
// var ratings = ratingService.getAverageRating(game);
// assertEquals(4, ratings);
// ratingService.setRating(new Rating(game, "marko", 3));
// ratings = ratingService.getAverageRating(game);
// assertEquals(3, ratings);
// ratingService.removeRating(game);
// }

// }