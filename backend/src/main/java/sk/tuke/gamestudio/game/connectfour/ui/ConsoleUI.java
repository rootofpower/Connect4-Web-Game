package sk.tuke.gamestudio.game.connectfour.ui;

// import java.util.Date;
import java.util.Scanner;

// import sk.tuke.gamestudio.entity.Comment;
// import sk.tuke.gamestudio.entity.Rating;
// import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.connectfour.core.*;
import sk.tuke.gamestudio.service.interfaces.CommentService;
import sk.tuke.gamestudio.service.interfaces.RatingService;
import sk.tuke.gamestudio.service.interfaces.ScoreService;

public class ConsoleUI implements UserInterface {

    private final Scanner scanner;
//    private final ScoreService scoreService;
//    private final RatingService ratingService;
//    private final CommentService commentService;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BOLD = "\u001B[1m";

    public ConsoleUI() {
        scanner = new Scanner(System.in);
    }

    @Override
    public void displayWelcome() {
        System.out.println(ANSI_BOLD + ANSI_CYAN + "====================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    Welcome to the game           " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "         CONNECT FOUR             " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "====================================" + ANSI_RESET);
        System.out.println("Goal: Connect 4 tokens of the same color horizontally, vertically, or diagonally.");
        System.out.println("Players take turns dropping tokens into columns.");
        System.out.println("The first player to connect 4 tokens wins.");
        System.out.println("Press 'q' at any time to quit the game.");
        displayOptions();
    }

    @Override
    public void displayOptions() {
        System.out.println(ANSI_BOLD + ANSI_CYAN + "====================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    Choose an option:              " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    1. Start Game                  " + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_CYAN + "    2. View Top Scores             " + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_CYAN + "    3. View Game Rating            " + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_CYAN + "    4. View Game Comments          " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    2. Exit                        " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "====================================" + ANSI_RESET);
    }

    @Override
    public int getPlayerAction(int maxActions) {
        int action = -1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter your choice (1-" + maxActions + "): ");
            String input = scanner.nextLine().trim();

            try {
                action = Integer.parseInt(input);
                if (action >= 1 && action <= maxActions) {
                    validInput = true;
                } else {
                    System.out.println(ANSI_RED + "Invalid choice. Please enter a number between 1 and " + maxActions
                            + "." + ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Invalid format. Please enter a number between 1 and " + maxActions + "."
                        + ANSI_RESET);
            }
        }

        return action;
    }

    @Override
    public void displayBoard(Board board) {
        System.out.println();

        System.out.print(" ");
        for (int col = 0; col < board.getColumns(); col++) {
            System.out.print(" " + (col + 1) + "  ");
        }
        System.out.println();

        System.out.print("+");
        for (int col = 0; col < board.getColumns(); col++) {
            System.out.print("---+");
        }
        System.out.println();

        for (int row = 0; row < board.getRows(); row++) {
            System.out.print("|");
            for (int col = 0; col < board.getColumns(); col++) {
                CellState cellState = board.getCellState(row, col);
                String displayChar;
                String colorCode = "";

                switch (cellState) {
                    case PLAYER1:
                        colorCode = ANSI_RED;
                        displayChar = " O ";
                        break;
                    case PLAYER2:
                        colorCode = ANSI_YELLOW;
                        displayChar = " O ";
                        break;
                    case EMPTY:
                    default:
                        displayChar = " . ";
                        colorCode = "";
                        break;
                }
                System.out.print(colorCode + displayChar + ANSI_RESET); // Друкуємо клітинку (з кольором або без)
                System.out.print("|");
            }
            System.out.println();

            System.out.print("+");
            for (int col = 0; col < board.getColumns(); col++) {
                System.out.print("---+");
            }
            System.out.println();
        }
        System.out.println();
    }

    @Override
    public int getPlayerMove(Player player) {
        int column = -1;
        boolean validInput = false;

        while (!validInput) {
            System.out.print(getPlayerColorCode(player) + player.getName() + ANSI_RESET +
                    ", enter column number (1-7) or 'q' to quit: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) {
                System.exit(0);
            }

            try {
                column = Integer.parseInt(input);
                column = column - 1;
                if (column >= 0 && column < 7) {
                    validInput = true;
                } else {
                    System.out.println(ANSI_RED + "Invalid column. Enter a number between 1 and 7." + ANSI_RESET);
                }
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Invalid format. Enter a number between 1 and 7." + ANSI_RESET);
            }
        }

        return column;
    }

    @Override
    public void displayGameOver(Player winner) {
        if (winner != null) {
            System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
            System.out.println(
                    ANSI_BOLD + getPlayerColorCode(winner) + "Player " + winner.getName() + " wins!" + ANSI_RESET);
            System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
        } else {
            System.out.println(ANSI_BOLD + ANSI_BLUE + "====================================" + ANSI_RESET);
            System.out.println(ANSI_BOLD + ANSI_BLUE + "Draw! The board is full." + ANSI_RESET);
            System.out.println(ANSI_BOLD + ANSI_BLUE + "====================================" + ANSI_RESET);
        }
    }

    @Override
    public void displayInvalidMove() {
        System.out.println(ANSI_RED + "Invalid move! Column is full or doesn't exist." + ANSI_RESET);
    }

    @Override
    public void displayTurn(Player player) {
        System.out.println(getPlayerColorCode(player) + "Turn of player " + player.getName() + ANSI_RESET);
    }

//    @Override
//    public void displayTopScore(ScoreService scoreService) {
//        var scores = scoreService.getTopScores("connect4");
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "Top Scores:" + ANSI_RESET);
//        for (int i = 0; i < scores.size(); i++) {
//            var score = scores.get(i);
//            System.out.println(ANSI_BOLD + ANSI_GREEN + (i + 1) + ". " + ANSI_RED + score.getUsername() + ": "
//                    + score.getPoints() + ANSI_RESET);
//        }
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//    }
//
//    @Override
//    public void displayGameRating(RatingService ratingService) {
//        var rating = ratingService.getAverageRating("connect4");
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//        System.out
//                .println(ANSI_BOLD + ANSI_GREEN + "Game Rating: " + ANSI_RED + rating + ANSI_GREEN + "/5" + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//    }
//
//    @Override
//    public void displayGameComment(CommentService commentService) {
//        var comments = commentService.getCommentsByGame("connect4");
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "Game Comments:" + ANSI_RESET);
//        for (var comment : comments) {
//            System.out.println(ANSI_BOLD + ANSI_GREEN + comment.getUsername() + ": " + ANSI_RED + comment.getComment()
//                    + ANSI_GREEN + " Written at: " + comment.getCommentedAt() + ANSI_RESET);
//        }
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//    }

    @Override
    public void displayExitMessage() {
        System.out.println(ANSI_BOLD + ANSI_RED + "====================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_RED + "Thank you for playing!" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_RED + "====================================" + ANSI_RESET);
    }

    @Override
    public void playGame(GameController gc, Player[] players, int playerCount) {
        gc.initializeGame();
        displayBoard(gc.getBoard());
        while (gc.getGameState() == GameState.PLAYING) {
            Player currentPlayer = gc.getCurrentPlayer();
            displayTurn(currentPlayer);
            int column = getPlayerMove(currentPlayer);
            if (gc.isValidMove(column)) {
                gc.makeMove(column);
                displayBoard(gc.getBoard());
            } else {
                displayInvalidMove();
            }
        }
        Player winner = gc.checkWinner();
        displayGameOver(winner);
        displayAfterGame();
        actionAfterGameOver(gc, winner);
    }

//    @Override
//    public void addScore(ScoreService scoreService, Player player, int score) {
//        System.out.println(ANSI_RED + "Cannot add Score from ConsoleUI in current implementation." + ANSI_RESET);
//        System.out.println(ANSI_RED + "Backend API requires user authentication." + ANSI_RESET);
//
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "====================================" + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "Score added: " + score + ANSI_RESET);
//    }
//
//    @Override
//    public void addComment(CommentService commentService, Player player) {
//        System.out.print("Enter your comment: ");
//        String comment = scanner.nextLine().trim();
//        System.out.println(ANSI_RED + "Cannot add comment from ConsoleUI in current implementation." + ANSI_RESET);
//        System.out.println(ANSI_RED + "Backend API requires user authentication." + ANSI_RESET);
//
//        System.out.println(ANSI_BOLD + ANSI_GREEN + "Comment added: " + comment + ANSI_RESET);
//    }
//
//    @Override
//    public void addRating(RatingService ratingService, Player player) {
//        double rating = -1;
//        boolean validInput = false;
//
//        while (!validInput) {
//            System.out.print("Enter your rating (1-5): ");
//            String input = scanner.nextLine().trim();
//
//            try {
//                rating = Double.parseDouble(input);
//                if (rating >= 1 && rating <= 5) {
//                    validInput = true;
//                } else {
//                    System.out.println(
//                            ANSI_RED + "Invalid rating. Please enter a double number between 1 and 5." + ANSI_RESET);
//                }
//            } catch (NumberFormatException e) {
//                System.out.println(
//                        ANSI_RED + "Invalid format. Please enter a double number between 1 and 5." + ANSI_RESET);
//            }
//        }
//
//        System.out.println(ANSI_RED + "Cannot add rating from ConsoleUI in current implementation." + ANSI_RESET);
//        System.out.println(ANSI_RED + "Backend API requires user authentication." + ANSI_RESET);
//    }

    private String getPlayerColorCode(Player player) {
        return player.getColor().equals("red") ? ANSI_RED : ANSI_YELLOW;
    }

    public void displayAfterGame() {
        // writes message to player with question if he wants to play again
        System.out.println(ANSI_BOLD + ANSI_CYAN + "====================================" + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    What you want to do?          " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    1. Return to main menu        " + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_CYAN + "    2. Add scores                 " + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_CYAN + "    3. Add game rating            " + ANSI_RESET);
//        System.out.println(ANSI_BOLD + ANSI_CYAN + "    4. Add game comment           " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "    2. Exit                       " + ANSI_RESET);
        System.out.println(ANSI_BOLD + ANSI_CYAN + "====================================" + ANSI_RESET);
    }

    @Override
    public void actionAfterGameOver(GameController gc, Player winner) {
        int action;
        boolean scoreAdded = false;
        do {
            action = getPlayerAction(2);
            switch (action) {
                case 1:
                    gc.initializeGame();
                    displayBoard(gc.getBoard());
                    break;
                case 2:
                    displayExitMessage();
                    System.exit(0);
                    break;
            }
        } while (action != 1);
    }


    @Override
    public String getPlayerName(boolean playerNumber) {
        System.out.print("Enter name for player " + (playerNumber ? "1" : "2") + ": ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println(ANSI_RED + "Name cannot be empty. Please enter a valid name." + ANSI_RESET);
            return getPlayerName(playerNumber);
        }
        return name;
    }
}