// package sk.tuke.gamestudio.service.JDBC;

// import org.springframework.beans.factory.annotation.Autowired;
// import sk.tuke.gamestudio.entity.Rating;
// import sk.tuke.gamestudio.service.exceptions.RatingException;
// import sk.tuke.gamestudio.service.interfaces.RatingService;

// import javax.sql.DataSource;
// import java.sql.SQLException;

// public class RatingServiceJDBC implements RatingService {
// private static final String INSERT = "INSERT INTO ratings (game, player,
// rating) " +
// "VALUES (?, ?, ?)" +
// "ON CONFLICT (game, player) " +
// "DO UPDATE SET " +
// "rating = EXCLUDED.rating";
// private static final String SELECT = "SELECT game, player, rating " +
// "FROM ratings WHERE game = ? AND player = ? " +
// "ORDER BY rating DESC LIMIT 1";
// private static final String DELETE = "DELETE FROM ratings WHERE game = ?";
// private static final String SELECT_AVG = "SELECT AVG(rating) " +
// "FROM ratings WHERE game = ? " +
// "GROUP BY game";
// @Autowired
// private DataSource dataSource;

// @Override
// public void setRating(Rating rating) {
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(INSERT)) {
// statement.setString(1, rating.getGame());
// statement.setString(2, rating.getPlayer());
// statement.setDouble(3, rating.getRating());
// statement.executeUpdate();
// } catch (SQLException e) {
// throw new RatingException("Error while adding rating", e);
// }
// }

// @Override
// public double getRating(String game, String player) {
// double result = -1;
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(SELECT)) {
// statement.setString(1, game);
// statement.setString(2, player);
// try (var resultSet = statement.executeQuery()) {
// if (resultSet.next()) {
// result = resultSet.getDouble("rating");
// }
// }
// } catch (SQLException e) {
// throw new RatingException("Error while getting rating", e);
// }
// if (result == -1) {
// throw new RatingException("No rating found for player " + player + " in game
// " + game);
// }
// return result;
// }

// @Override
// public double getAverageRating(String game) {
// double result = 0;
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(SELECT_AVG)) {
// statement.setString(1, game);
// try (var resultSet = statement.executeQuery()) {
// while (resultSet.next()) {
// result = resultSet.getDouble(1);
// }
// }
// } catch (SQLException e) {
// throw new RatingException("Error while getting average rating", e);
// }
// return result;
// }

// @Override
// public void removeRating(String game) {
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(DELETE)) {
// statement.setString(1, game);
// statement.executeUpdate();
// } catch (SQLException e) {
// throw new RatingException("Error while removing rating", e);
// }
// }

// }
