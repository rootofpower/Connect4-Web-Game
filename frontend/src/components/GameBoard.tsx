
import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { useToast } from '@/components/ui/use-toast';

interface GameBoardProps {
  rows?: number;
  cols?: number;
  onGameEnd?: (winner: number, board: number[][]) => void;
}

const GameBoard = ({ rows = 6, cols = 7, onGameEnd }: GameBoardProps) => {
  const [board, setBoard] = useState<number[][]>(Array(rows).fill(0).map(() => Array(cols).fill(0)));
  const [currentPlayer, setCurrentPlayer] = useState(1); // 1 for Player 1 (Yellow), 2 for Player 2 (Blue)
  const [gameOver, setGameOver] = useState(false);
  const [winner, setWinner] = useState(0);
  const { toast } = useToast();

  // Reset the game
  const resetGame = () => {
    setBoard(Array(rows).fill(0).map(() => Array(cols).fill(0)));
    setCurrentPlayer(1);
    setGameOver(false);
    setWinner(0);
  };

  // Check if the game is over
  useEffect(() => {
    if (gameOver && onGameEnd) {
      onGameEnd(winner, board);
    }
  }, [gameOver, winner, board, onGameEnd]);

  // Make a move
  const makeMove = (col: number) => {
    if (gameOver) return;

    // Find the lowest empty row in the selected column
    const newBoard = [...board];
    let rowToPlace = -1;

    for (let r = rows - 1; r >= 0; r--) {
      if (newBoard[r][col] === 0) {
        rowToPlace = r;
        break;
      }
    }

    // If column is full, don't do anything
    if (rowToPlace === -1) {
      toast({
        variant: "destructive",
        title: "Column is full",
        description: "Please select another column.",
      });
      return;
    }

    // Place the piece
    newBoard[rowToPlace][col] = currentPlayer;
    setBoard(newBoard);

    // Check for a win
    if (checkForWin(newBoard, rowToPlace, col)) {
      setGameOver(true);
      setWinner(currentPlayer);
      toast({
        title: `Player ${currentPlayer} wins!`,
        description: `Player ${currentPlayer === 1 ? 'Yellow' : 'Blue'} has connected four!`,
      });
      return;
    }

    // Check for a draw
    if (checkForDraw(newBoard)) {
      setGameOver(true);
      setWinner(0);
      toast({
        title: "Draw!",
        description: "The game ended in a draw.",
      });
      return;
    }

    // Switch players
    setCurrentPlayer(currentPlayer === 1 ? 2 : 1);
  };

  // Check for a win
  const checkForWin = (board: number[][], row: number, col: number) => {
    const player = board[row][col];
    let count;

    // Check horizontally
    count = 1;
    // Check left
    for (let c = col - 1; c >= 0 && board[row][c] === player; c--) {
      count++;
    }
    // Check right
    for (let c = col + 1; c < cols && board[row][c] === player; c++) {
      count++;
    }
    if (count >= 4) return true;

    // Check vertically
    count = 1;
    // Check down
    for (let r = row + 1; r < rows && board[r][col] === player; r++) {
      count++;
    }
    if (count >= 4) return true;

    // Check diagonally (top-left to bottom-right)
    count = 1;
    // Check top-left
    for (
      let r = row - 1, c = col - 1;
      r >= 0 && c >= 0 && board[r][c] === player;
      r--, c--
    ) {
      count++;
    }
    // Check bottom-right
    for (
      let r = row + 1, c = col + 1;
      r < rows && c < cols && board[r][c] === player;
      r++, c++
    ) {
      count++;
    }
    if (count >= 4) return true;

    // Check diagonally (top-right to bottom-left)
    count = 1;
    // Check top-right
    for (
      let r = row - 1, c = col + 1;
      r >= 0 && c < cols && board[r][c] === player;
      r--, c++
    ) {
      count++;
    }
    // Check bottom-left
    for (
      let r = row + 1, c = col - 1;
      r < rows && c >= 0 && board[r][c] === player;
      r++, c--
    ) {
      count++;
    }
    if (count >= 4) return true;

    return false;
  };

  // Check for a draw
  const checkForDraw = (board: number[][]) => {
    for (let c = 0; c < cols; c++) {
      if (board[0][c] === 0) {
        return false;
      }
    }
    return true;
  };

  return (
    <div className="flex flex-col items-center">
      <div className="mb-6 p-4 bg-connect4-blue rounded-lg">
        <h2 className="text-2xl font-bold text-center text-white mb-2">
          {gameOver
            ? winner !== 0
              ? `Player ${winner === 1 ? 'Yellow' : 'Blue'} Wins!`
              : "It's a Draw!"
            : `Player ${currentPlayer === 1 ? 'Yellow' : 'Blue'}'s Turn`}
        </h2>
        <div className="flex justify-center items-center gap-4">
          <div
            className={`w-6 h-6 rounded-full ${
              currentPlayer === 1 ? "bg-connect4-yellow" : "bg-gray-600"
            }`}
          ></div>
          <div
            className={`w-6 h-6 rounded-full ${
              currentPlayer === 2 ? "bg-blue-500" : "bg-gray-600"
            }`}
          ></div>
        </div>
      </div>

      <div className="bg-connect4-blue p-4 rounded-lg shadow-lg">
        <div className="grid grid-cols-7 gap-2 mb-2">
          {Array(cols)
            .fill(null)
            .map((_, colIndex) => (
              <Button
                key={`control-${colIndex}`}
                onClick={() => makeMove(colIndex)}
                disabled={gameOver || board[0][colIndex] !== 0}
                className="h-8 w-full bg-gray-700 hover:bg-gray-600"
              >
                ↓
              </Button>
            ))}
        </div>
        <div
          className="grid grid-cols-7 gap-2 p-2 bg-connect4-dark rounded-md"
          style={{ width: "min(100%, 500px)" }}
        >
          {board.map((row, rowIndex) =>
            row.map((cell, colIndex) => (
              <div
                key={`${rowIndex}-${colIndex}`}
                className={`aspect-square rounded-full flex items-center justify-center transition-colors ${
                  cell === 0
                    ? "bg-gray-700"
                    : cell === 1
                    ? "bg-connect4-yellow"
                    : "bg-blue-500"
                }`}
              ></div>
            ))
          )}
        </div>
      </div>

      <div className="mt-6">
        <Button
          onClick={resetGame}
          className="bg-gray-700 text-white hover:bg-gray-600"
        >
          New Game
        </Button>
      </div>
    </div>
  );
};

export default GameBoard;
