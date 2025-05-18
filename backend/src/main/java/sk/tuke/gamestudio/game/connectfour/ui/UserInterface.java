package sk.tuke.gamestudio.game.connectfour.ui;

import sk.tuke.gamestudio.game.connectfour.core.Board;
import sk.tuke.gamestudio.game.connectfour.core.GameController;
import sk.tuke.gamestudio.game.connectfour.core.Player;
import sk.tuke.gamestudio.service.interfaces.CommentService;
import sk.tuke.gamestudio.service.interfaces.RatingService;
import sk.tuke.gamestudio.service.interfaces.ScoreService;

public interface UserInterface {
    void displayWelcome();

    void displayOptions();

    int getPlayerAction(int maxActions);

    void displayBoard(Board board);

    int getPlayerMove(Player player);

    void displayGameOver(Player winner);

    void displayInvalidMove();

    void displayTurn(Player player);

//    void displayTopScore(ScoreService scoreService);
//
//    void displayGameRating(RatingService ratingService);
//
//    void displayGameComment(CommentService commentService);

    void displayExitMessage();

    void playGame(GameController gc, Player[] players, int playerCount);

//    void addScore(ScoreService scoreService, Player player, int score);
//
//    void addComment(CommentService commentService, Player player);
//
//    void addRating(RatingService ratingService, Player player);

    void actionAfterGameOver(GameController gc, Player winner);

    String getPlayerName(boolean playerNumber);
}