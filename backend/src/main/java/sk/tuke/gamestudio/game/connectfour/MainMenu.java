package sk.tuke.gamestudio.game.connectfour;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.tuke.gamestudio.game.connectfour.core.GameController;
import sk.tuke.gamestudio.game.connectfour.core.Player;
// import sk.tuke.gamestudio.game.connectfour.ui.ConsoleUI;
import sk.tuke.gamestudio.game.connectfour.ui.UserInterface;
// import sk.tuke.gamestudio.service.JPA.CommentServiceJPA;
// import sk.tuke.gamestudio.service.JPA.RatingServiceJPA;
// import sk.tuke.gamestudio.service.JPA.ScoreServiceJPA;
//import sk.tuke.gamestudio.service.interfaces.CommentService;
//import sk.tuke.gamestudio.service.interfaces.RatingService;
//import sk.tuke.gamestudio.service.interfaces.ScoreService;

// import static sk.tuke.gamestudio.game.connectfour.ui.ConsoleUI.ANSI_RED;
// import static sk.tuke.gamestudio.game.connectfour.ui.ConsoleUI.ANSI_RESET;

@Component
public class MainMenu {

    // start the game, game must be started from "main menu", player select option
    // 1-5, when he select 1, game will start
    // if player select 2, he can see his score, if he select 3, he can see his
    // rating, if he select 4, he can see his comments, if he select 5, he can exit
    // the game
    // if player exit from started game, he comes back to main menu
    // after game is finished, player can set his rating and comment
    // after game player comes back to main menu
    //
//    @Autowired
//    ScoreService scoreService;
//
//    @Autowired
//    RatingService ratingService;
//
//    @Autowired
//    CommentService commentService;

    public void startMenu(UserInterface ui, GameController gc) {
        ui.displayWelcome();
        int action;
        do {
            action = ui.getPlayerAction(2);
            if (action == 1) {
                Player player1 = new Player(1, ui.getPlayerName(true), "red");
                Player player2 = new Player(2, ui.getPlayerName(false), "yellow");
                gc.setPlayer(0, player1);
                gc.setPlayer(1, player2);
                ui.playGame(gc, gc.getPlayers(), 2);
                //                case 2:
//                    ui.displayTopScore(scoreService);
//                    ui.displayOptions();
//                    break;
//                case 3:
//                    ui.displayGameRating(ratingService);
//                    ui.displayOptions();
//                    break;
//                case 4:
//                    ui.displayGameComment(commentService);
//                    ui.displayOptions();
//                    break;
            } else {
                ui.displayOptions();
            }

        } while (action != 5);
        ui.displayExitMessage();
        System.exit(0);
    }

    // public static void main(String[] args) {
    // var ui = new ConsoleUI(
    // new ScoreServiceJPA(),
    // new RatingServiceJPA(),
    // new CommentServiceJPA()
    // );
    // var gameController = new GameController(2);
    // MainMenu menu = new MainMenu();
    // menu.startMenu(ui, gameController);
    // }
}