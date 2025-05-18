package sk.tuke.gamestudio.game.connectfour.core;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameController {
    private Board board;
    private final Player[] players;
    private GameState state;
    private int currentPlayerIndex;
    private int moveCount;
    private boolean winDetected;
    private Player lastPlayer;
    private int score;

    public GameController(int numPlayers) {
        players = new Player[numPlayers];
        if (numPlayers < 2 || numPlayers > 2) {
            throw new IllegalArgumentException("Number of players must be 2.");
        }
        state = GameState.START;
    }

    public void initializeGame() {
        board = new Board();
        state = GameState.PLAYING;
        currentPlayerIndex = 0;
        moveCount = 0;
        winDetected = false;
        lastPlayer = null;
        score = 0;
    }

    public void makeMove(int column) {
        if (state != GameState.PLAYING) {
            throw new IllegalStateException("Game is not in a playable state.");
        }

        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            throw new IllegalStateException("Current player is not set.");
        }
        lastPlayer = currentPlayer;

        CellState playerCellState = (currentPlayerIndex == 0) ? CellState.PLAYER1 : CellState.PLAYER2;

        if (board.dropToken(column, playerCellState)) {
            moveCount++;
            if (board.checkWin(playerCellState)) {
                winDetected = true;
                state = GameState.ENDGAME;
            } else if (board.isFull()) {
                state = GameState.ENDGAME;
            } else {
                switchPlayer();
            }
        }
    }

    public boolean isValidMove(int column) {
        if (column < 0 || column >= board.getColumns() || state != GameState.PLAYING) {
            return false;
        }
        try {
            return board.getCellState(0, column) == CellState.EMPTY;
        } catch (IllegalArgumentException e) {
            System.err.println("Error checking valid move: " + e.getMessage());
            return false;
        }

    }

    public void switchPlayer() {
        if (players != null && players.length > 0) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        } else {
            throw new IllegalStateException("No players available to switch.");
        }
    }

    public Player checkWinner() {
        if (state != GameState.ENDGAME) {
            return null;
        }

        if (winDetected) {
            return lastPlayer;
        }

        return null;
    }

    public boolean checkDraw() {
        return state == GameState.ENDGAME && !winDetected;
    }

    public Player getCurrentPlayer() {
        if (players == null || currentPlayerIndex < 0 || currentPlayerIndex >= players.length) {
            throw new IllegalStateException("Current player index is out of bounds.");
        }
        return players[currentPlayerIndex];
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public void setPlayer(int index, Player player) {
        if (players != null && index >= 0 && index < players.length) {
            players[index] = player;
        } else {
            throw new IllegalArgumentException("Invalid player index or players array is null.");
        }
    }

    public int getScore() {
        score += 100;
        return score;
    }
}