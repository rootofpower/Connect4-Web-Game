// package sk.tuke.gamestudio.service.JDBC;

// import org.springframework.beans.factory.annotation.Autowired;
// import sk.tuke.gamestudio.entity.Comment;
// import sk.tuke.gamestudio.service.exceptions.CommentException;
// import sk.tuke.gamestudio.service.interfaces.CommentService;

// import javax.sql.DataSource;
// import java.sql.SQLException;
// import java.sql.Statement;
// import java.sql.Timestamp;
// import java.util.ArrayList;
// import java.util.List;

// public class CommentServiceJDBC implements CommentService {
// private static final String INSERT = "INSERT INTO comments (game, player,
// comment, commented_at) " +
// "VALUES (?, ?, ?, ?)";

// private static final String SELECT = "SELECT game, player, comment,
// commented_at " +
// "FROM comments " +
// "WHERE game = ? AND player = ? AND id = ?" +
// "ORDER BY commented_at LIMIT 1";
// private static final String DELETE = "DELETE FROM comments WHERE game = ? AND
// player = ? AND id = ?";

// private static final String SELECT_ALL = "SELECT game, player, comment,
// commented_at, id FROM comments WHERE game = ? ORDER BY commented_at";

// private static final String DELETE_ALL = "DELETE FROM comments WHERE game =
// ?";

// @Autowired
// private DataSource dataSource;

// @Override
// public void setComment(Comment comment) {
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(INSERT,
// Statement.RETURN_GENERATED_KEYS)) {
// statement.setString(1, comment.getGame());
// statement.setString(2, comment.getPlayer());
// statement.setString(3, comment.getComment());
// statement.setTimestamp(4, new Timestamp(comment.getCommentedAt().getTime()));
// statement.executeUpdate();
// try (var resultSet = statement.getGeneratedKeys()) {
// if (resultSet.next()) {
// comment.setId(resultSet.getInt(1));
// }
// }
// } catch (SQLException e) {
// throw new CommentException("Error while adding comment", e);
// }
// }

// @Override
// public Comment getComment(String game, String player, int id) {
// Comment comment = null;
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(SELECT)) {
// statement.setString(1, game);
// statement.setString(2, player);
// statement.setInt(3, id);
// try (var resultSet = statement.executeQuery()) {
// if (resultSet.next()) {
// comment = new Comment();
// comment.setGame(resultSet.getString(1));
// comment.setPlayer(resultSet.getString(2));
// comment.setComment(resultSet.getString(3));
// comment.setCommentedAt(resultSet.getTimestamp(4));
// comment.setId(id);
// }
// }
// } catch (SQLException e) {
// throw new CommentException("Error while getting comment", e);
// }
// if (comment == null) {
// throw new CommentException("Comment not found for game: " + game +
// ", player: " + player + ", id: " + id);
// }
// return comment;
// }

// @Override
// public void deleteComment(String game, String player, int id) {
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(DELETE)) {
// statement.setString(1, game);
// statement.setString(2, player);
// statement.setInt(3, id);
// statement.executeUpdate();
// } catch (SQLException e) {
// throw new CommentException("Error while deleting comment", e);
// }
// }

// @Override
// public void clearComments(String game) {
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(DELETE_ALL)) {
// statement.setString(1, game);
// statement.executeUpdate();
// } catch (SQLException e) {
// throw new CommentException("Error while clearing comments", e);
// }
// }

// @Override
// public List<Comment> getComments(String game) {
// var comments = new ArrayList<Comment>();
// try (var connection = dataSource.getConnection();
// var statement = connection.prepareStatement(SELECT_ALL)) {
// statement.setString(1, game);
// try (var resultSet = statement.executeQuery()) {
// while (resultSet.next()) {
// var comment = new Comment();
// comment.setGame(resultSet.getString(1));
// comment.setPlayer(resultSet.getString(2));
// comment.setComment(resultSet.getString(3));
// comment.setCommentedAt(resultSet.getTimestamp(4));
// comment.setId(resultSet.getInt(5));
// comments.add(comment);

// }
// }
// } catch (SQLException e) {
// throw new CommentException("Error while getting comments", e);
// }
// return comments;
// }
// }
