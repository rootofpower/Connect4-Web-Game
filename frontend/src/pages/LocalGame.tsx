
import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import GameBoard from '@/components/GameBoard';

const LocalGame = () => {
  const handleGameEnd = (winner: number, board: number[][]) => {
    console.log('Game ended with winner:', winner);
    console.log('Final board state:', board);
  };

  return (
    <div className="min-h-[calc(100vh-5rem)] flex flex-col items-center justify-center p-4">
      <h1 className="text-3xl font-bold text-white mb-8">Local Game</h1>
      
      <GameBoard onGameEnd={handleGameEnd} />
      
      <div className="mt-8">
        <Link to="/game/mode">
          <Button variant="outline" className="border-gray-600 text-white hover:bg-gray-700">
            Back to Game Modes
          </Button>
        </Link>
      </div>
    </div>
  );
};

export default LocalGame;
