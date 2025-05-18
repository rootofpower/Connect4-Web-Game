import React from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useParams, useNavigate } from 'react-router-dom';
import { getGameState, makeMove } from '@/services/gameService';
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/button';
import { useToast } from "@/components/ui/use-toast";
import { Loader2, ArrowLeft } from 'lucide-react';
import { getLobbyState, setReady, setUnready, startGame, leaveLobby, type StartGameResponse } from "@/services/lobbyService";


type CellState = 'EMPTY' | 'PLAYER1' | 'PLAYER2';
type BoardState = CellState[][];

const GameScreen = () => {
    const { gameId } = useParams<{ gameId: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const { toast } = useToast();
    const queryClient = useQueryClient();
    const gameIdNum = gameId ? parseInt(gameId, 10) : undefined;

    const { data: gameState, isLoading, error, isError } = useQuery({
        queryKey: ['game', gameIdNum],
        queryFn: () => getGameState(gameIdNum!),
        refetchInterval: 1000,
        enabled: !!gameIdNum,
        retry: 1,
    });

     const moveMutation = useMutation({
        mutationFn: (column: number) => makeMove(gameIdNum!, { column }),
        onSuccess: (updatedGameState) => {
            queryClient.setQueryData(['game', gameIdNum], updatedGameState);
        },
        onError: (err: any) => {
            const errorMsg = err.response?.data?.error || err.message || "Unknown error making move";
            toast({ title: "Invalid Move", description: errorMsg, variant: "destructive"});
        },
     });

    //  const leaveMutation = useMutation({
    //           mutationFn: () => leaveLobby(lobbyCode!),
    //           onSuccess: () => {
    //               toast({ title: "Left lobby" });
    //               queryClient.removeQueries({ queryKey: ['lobby', lobbyCode] });
    //               navigate('/');
    //           },
    //           onError: (err: Error) => toast({ title: "Error leaving lobby", description: err.message, variant: "destructive" }),
    //        });

    const renderBoard = (boardStateJson: string | undefined): React.ReactNode => {
        if (!boardStateJson) return <div className="text-center text-gray-400">Waiting for board state...</div>;
        try {
            const board: BoardState = JSON.parse(boardStateJson);
            return (
                <div className="bg-slate-800 p-2 sm:p-3 inline-block border-2 border-slate-700 rounded-lg shadow-xl">
                    {board.map((row, rowIndex) => (
                        <div key={rowIndex} className="flex">
                            {row.map((cell, colIndex) => {
                                let discColor = 'bg-gray-700';
                                if (cell === 'PLAYER1') discColor = 'bg-yellow-400';
                                if (cell === 'PLAYER2') discColor = 'bg-blue-500';
                                
                                const canClick = gameState?.currentPlayerUsername === user?.username &&
                                                 (gameState?.status === 'PLAYER_1_TURN' || gameState?.status === 'PLAYER_2_TURN') &&
                                                 !isGameOver;

                                return (
                                    <div
                                        key={colIndex}
                                        className={`w-12 h-12 sm:w-14 sm:h-14 m-[2px] sm:m-1 flex items-center justify-center rounded-md transition-colors
                                                    ${canClick ? 'cursor-pointer hover:bg-slate-600' : 'cursor-default'}`}
                                        onClick={() => canClick && !moveMutation.isPending && moveMutation.mutate(colIndex)}
                                    >
                                        <div className={`w-10 h-10 sm:w-12 sm:h-12 rounded-full ${discColor} shadow-inner`}></div>
                                    </div>
                                );
                            })}
                        </div>
                    ))}
                </div>
            );
        } catch (e) {
            console.error("Failed to parse board state:", e);
            return <div className="text-red-500">Error rendering board state.</div>;
        }
    };

     let statusMessage = '';
     const isMyTurn = user?.username === gameState?.currentPlayerUsername;
     const isGameOver = gameState?.status === 'PLAYER_1_WINS' || gameState?.status === 'PLAYER_2_WINS' || gameState?.status === 'DRAW';

     if (isLoading) {
        statusMessage = 'Loading game...';
     } else if (isError) {
        statusMessage = `Error: ${(error as Error).message}`;
     } else if (gameState) {
         switch (gameState.status) {
             case 'PLAYER_1_WINS':
                 statusMessage = `Game Over! ${gameState.player1Username} (Yellow) Wins!`;
                 break;
             case 'PLAYER_2_WINS':
                 statusMessage = `Game Over! ${gameState.player2Username} (Blue) Wins!`;
                 break;
             case 'DRAW':
                 statusMessage = "Game Over! It's a Draw!";
                 break;
             case 'PLAYER_1_TURN':
                 statusMessage = isMyTurn ? "Your Turn (Yellow)" : `Waiting for ${gameState.currentPlayerUsername} (Yellow)...`;
                 break;
             case 'PLAYER_2_TURN':
                 statusMessage = isMyTurn ? "Your Turn (Blue)" : `Waiting for ${gameState.currentPlayerUsername} (Blue)...`;
                 break;
             default:
                 statusMessage = `Status: ${gameState.status}`;
         }
     } else {
         statusMessage = 'Game data not available.';
     }


    return (
        <div className="container mx-auto p-4 flex flex-col items-center min-h-[calc(100vh-8rem)] text-white bg-gray-800">
             <button onClick={() => navigate('/')} className="absolute top-4 left-4 text-gray-300 hover:text-white transition-colors">
                 <ArrowLeft className="mr-2 h-6 w-6 inline-block"/> Back to Lobby
             </button>
             <h1 className="text-4xl font-bold mb-6 mt-10 text-transparent bg-clip-text bg-gradient-to-r from-yellow-400 to-blue-500">Connect Four</h1>
             
             <div className="mb-6 text-xl font-semibold text-center p-3 bg-slate-700 rounded-md shadow-md min-w-[300px]">
                {isLoading && <Loader2 className="animate-spin inline mr-2 h-5 w-5"/>}
                {statusMessage}
            </div>

            {renderBoard(gameState?.boardState)}

            {moveMutation.isPending && 
                <div className="mt-4 flex items-center text-yellow-300">
                    <Loader2 className="animate-spin inline mr-2 h-5 w-5"/>
                    Processing move...
                </div>
            }

             {isGameOver && gameState && (
                <div className="mt-8 p-6 bg-slate-700 rounded-lg shadow-xl text-center">
                    <h2 className="text-2xl font-bold mb-4">Game Finished!</h2>
                    <p className="text-lg mb-6">{statusMessage}</p>
                    <div className="flex flex-col sm:flex-row gap-4 justify-center">
                         <Button onClick={() => navigate('/')} variant="secondary" size="lg">Back to Menu</Button>
                         <Button onClick={() => navigate(`/ratings?gameType=connect4&gameId=${gameIdNum}`)} className="bg-yellow-500 hover:bg-yellow-600 text-black" size="lg">Rate This Game</Button>
                         <Button onClick={() => navigate(`/comments?gameType=connect4&gameId=${gameIdNum}`)} className="bg-blue-500 hover:bg-blue-600 text-white" size="lg">Leave Comment</Button>
                     </div>
                 </div>
             )}
             {!isGameOver && !isLoading && (
                 <Button onClick={() => navigate('/')} variant="outline" className="mt-8 border-gray-500 text-gray-300 hover:bg-gray-700 hover:text-white">
                     <ArrowLeft className="mr-2 h-4 w-4"/> Quit Game
                 </Button>
             )}
        </div>
    );
};

export default GameScreen;