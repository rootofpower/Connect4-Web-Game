// package sk.tuke.gamestudio.service.JDBC;

// import org.springframework.beans.factory.annotation.Autowired;
// import sk.tuke.gamestudio.entity.Score;
// import sk.tuke.gamestudio.service.exceptions.ScoreException;
// import sk.tuke.gamestudio.service.interfaces.ScoreService;

// import javax.sql.DataSource;
// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;

// public class ScoreServiceJDBC implements ScoreService {
// private static final String INSERT = "INSERT INTO score (game, player,
// points, last_played_at) " +
// "VALUES (?, ?, ?, ?)" +
// "ON CONFLICT (game, player) " +
// "DO UPDATE SET " +
// "points = score.points + EXCLUDED.points, " +
// "last_played_at = NOW()";

// private static final String SELECT = "SELECT game, player, points,
// last_played_at FROM score WHERE game = ? ORDER BY points DESC LIMIT 10";
// private static final String DELETE = "DELETE FROM score WHERE game = ?";

// @Autowired
// private DataSource dataSource;

// @Override
// public void addScore(Score score) {

// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(INSERT)) {
// statement.setString(1, score.getGame());
// statement.setString(2, score.getPlayer());
// statement.setInt(3, score.getPoints());
// statement.setTimestamp(4, new Timestamp(score.getLastPlayedAt().getTime()));
// statement.executeUpdate();
// } catch (SQLException e) {
// throw new ScoreException("Error while adding score", e);
// }
// }

// @Override
// public List<Score> getTopScores(String game) {
// System.out.println(dataSource);
// var scores = new ArrayList<Score>();
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(SELECT)) {
// statement.setString(1, game);
// try (var resultSet = statement.executeQuery()) {
// while (resultSet.next()) {
// scores.add(new Score(
// resultSet.getString(1),
// resultSet.getString(2),
// resultSet.getInt(3),
// resultSet.getTimestamp(4)));
// }
// }
// } catch (SQLException e) {
// throw new ScoreException("Error while getting top scores", e);
// }
// return scores;
// }

// @Override
// public void reset(String game) {
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(DELETE)) {
// statement.setString(1, game);
// statement.executeUpdate();
// } catch (SQLException e) {
// throw new ScoreException("Error while deleting scores", e);
// }
// }
// }
