package sk.tuke.gamestudio.game.connectfour.core;

import lombok.Getter;
import lombok.Setter;

public class Board {
    @Getter
    @Setter
    private CellState[][] cells;
    @Getter
    private final int rows;
    @Getter
    private final int columns;
    private static final int DEFAULT_ROWS = 6;
    private static final int DEFAULT_COLUMNS = 7;
    private static final int WIN_COUNT = 4;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        initialize();
    }

    public Board() {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public void initialize() {
        this.cells = new CellState[rows][columns];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                this.cells[row][col] = CellState.EMPTY;
            }
        }
    }

    public boolean dropToken(int column, CellState playerState) {
        if (column < 0 || column >= this.columns || playerState == CellState.EMPTY) {
            return false;
        }

        for (int row = rows - 1; row >= 0; row--) {
            if (this.cells[row][column] == CellState.EMPTY) {
                this.cells[row][column] = playerState;
                return true;
            }
        }
        return false;
    }

    public boolean checkWin(CellState playerState) {
        if (playerState == CellState.EMPTY)
            return false;

        // Horizontal
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col <= this.columns - WIN_COUNT; col++) {
                if (checkLine(row, col, 0, 1, playerState))
                    return true;
            }
        }
        // Vertical
        for (int row = 0; row <= this.rows - WIN_COUNT; row++) {
            for (int col = 0; col < this.columns; col++) {
                if (checkLine(row, col, 1, 0, playerState))
                    return true;
            }
        }
        // Diagonal /
        for (int row = WIN_COUNT - 1; row < this.rows; row++) {
            for (int col = 0; col <= this.columns - WIN_COUNT; col++) {
                if (checkLine(row, col, -1, 1, playerState))
                    return true;
            }
        }
        // Diagonal \
        for (int row = 0; row <= this.rows - WIN_COUNT; row++) {
            for (int col = 0; col <= this.columns - WIN_COUNT; col++) {
                if (checkLine(row, col, 1, 1, playerState))
                    return true;
            }
        }
        return false;
    }

    private boolean checkLine(int startRow, int startCol, int rowIncrement, int colIncrement, CellState playerState) {
        for (int i = 0; i < WIN_COUNT; i++) {
            int row = startRow + i * rowIncrement;
            int col = startCol + i * colIncrement;
            if (row < 0 || row >= this.rows || col < 0 || col >= this.columns || this.cells[row][col] != playerState) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        for (int col = 0; col < this.columns; col++) {
            if (cells[0][col] == CellState.EMPTY) {
                return false;
            }
        }
        return true;
    }

    // public boolean checkEnd(Player player) {
    // return checkWin(player) || isFull();
    // }

    public CellState getCellState(int row, int column) {
        if (row < 0 || row >= this.rows || column < 0 || column >= this.columns) {
            throw new IllegalArgumentException("Invalid cell coordinates");
        }
        return this.cells[row][column];
    }

}