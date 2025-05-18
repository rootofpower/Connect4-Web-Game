package sk.tuke.gamestudio.game.connectfour.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @Test
    public void testInitializeGame() {
        GameController gameController = new GameController(2);
        gameController.initializeGame();
        assertNotNull(gameController.getBoard());
        assertEquals(GameState.PLAYING, gameController.getState());
    }

    @Test
    public void testMakeMove() {
        GameController gameController = new GameController(2);
        gameController.initializeGame();
        gameController.makeMove(0);
        assertEquals(GameState.PLAYING, gameController.getState());
        assertEquals(1, gameController.getMoveCount());
    }

    @Test
    public void testMakeMoveInvalidState() {
        GameController gameController = new GameController(2);
        gameController.initializeGame();
        gameController.makeMove(0);
        gameController.setState(GameState.ENDGAME);
        gameController.makeMove(1);
        assertEquals(GameState.ENDGAME, gameController.getState());
        assertEquals(1, gameController.getMoveCount());
    }

} 