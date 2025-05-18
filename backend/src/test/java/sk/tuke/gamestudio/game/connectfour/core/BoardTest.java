package sk.tuke.gamestudio.game.connectfour.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {
    @Test
    public void testBoardInitialization() {
        Board board = new Board();
        assertEquals(6, board.getRows());
        assertEquals(7, board.getColumns());
    }

    @Test
    public void testCustomBoardInitialization() {
        Board board = new Board(5, 6);
        assertEquals(5, board.getRows());
        assertEquals(6, board.getColumns());
    }

}