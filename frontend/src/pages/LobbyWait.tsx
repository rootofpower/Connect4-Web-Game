import React from 'react';
import { useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useParams, useNavigate, Link } from 'react-router-dom'; // <-- Імпортовано Link
import { getLobbyState, setReady, setUnready, startGame, leaveLobby, type StartGameResponse } from "@/services/lobbyService";
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/button';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { CheckCircle2, XCircle, Hourglass, Loader2, LogOut, Play } from 'lucide-react';

const LobbyWait = () => {
    const { lobbyCode } = useParams<{ lobbyCode: string }>();
    const navigate = useNavigate();
    const { user } = useAuth();
    const queryClient = useQueryClient();
    const { toast } = useToast();

    const { data: lobby, isLoading: isQueryLoading, error, isError } = useQuery({
        queryKey: ['lobby', lobbyCode],
        queryFn: () => getLobbyState(lobbyCode!),
        refetchInterval: 1500,
        enabled: !!lobbyCode,
        retry: 1,
    });

    const readyMutation = useMutation({
         mutationFn: () => setReady(lobbyCode!),
         onSuccess: (updatedLobby) => queryClient.setQueryData(['lobby', lobbyCode], updatedLobby),
         onError: (err: Error) => toast({ title: "Error", description: err.message, variant: "destructive" }),
     });

     const unreadyMutation = useMutation({
         mutationFn: () => setUnready(lobbyCode!),
         onSuccess: (updatedLobby) => queryClient.setQueryData(['lobby', lobbyCode], updatedLobby),
         onError: (err: Error) => toast({ title: "Error", description: err.message, variant: "destructive" }),
     });

    const startMutation = useMutation({
         mutationFn: () => startGame(lobbyCode!),
         onSuccess: (data: StartGameResponse) => {
             toast({ title: "Game Starting!" });
             queryClient.invalidateQueries({ queryKey: ['lobby', lobbyCode] });
             navigate(`/game/${data.gameSessionId}`);
         },
         onError: (err: any) => {
            const errorMsg = err.response?.data?.error || err.message || "Unknown error starting game";
            toast({ title: "Error starting game", description: errorMsg, variant: "destructive"});
         },
     });

      const leaveMutation = useMutation({
         mutationFn: () => leaveLobby(lobbyCode!),
         onSuccess: () => {
             toast({ title: "Left lobby" });
             queryClient.removeQueries({ queryKey: ['lobby', lobbyCode] });
             navigate('/');
         },
         onError: (err: Error) => toast({ title: "Error leaving lobby", description: err.message, variant: "destructive" }),
      });

      useEffect(() => {
        if (lobby && lobby.status === 'IN_GAME' && lobby.gameSessionId) {
            console.log(`[LobbyWait] Status is IN_GAME with ID ${lobby.gameSessionId}. Navigating...`);
            navigate(`/game/${lobby.gameSessionId}`);
        }
    }, [lobby, navigate]); 

    if (isQueryLoading) return <div className="flex justify-center items-center min-h-screen"><Loader2 className="h-12 w-12 animate-spin text-white"/></div>;

    if (isError && (error as any)?.response?.status !== 404) return <div className="text-red-500 text-center mt-10">Error loading lobby: {(error as Error).message}</div>;

    if (!lobby) return <div className="text-gray-400 text-center mt-10">Lobby not found or has been closed. <Link to="/" className="text-blue-400 hover:underline">Go Home</Link></div>;

    const isHost = user?.username === lobby.hostUsername;
    const isOpponent = user?.username === lobby.opponentUsername;
    const viewerIsReady = isHost ? lobby.hostReady : (isOpponent ? lobby.opponentReady : false);
    const canStart = isHost && lobby.opponentUsername && lobby.status !== 'IN_GAME';

    return (
        <div className="container mx-auto p-4 flex justify-center items-center min-h-[calc(100vh-8rem)]">
            <Card className="w-full max-w-lg bg-connect4-blue border-gray-700 text-white">
                 <CardHeader>
                     <CardTitle className="text-center text-2xl">Lobby: <span className="text-connect4-yellow font-mono">{lobby.lobbyCode}</span></CardTitle>
                     <CardDescription className="text-center text-gray-400">Waiting for players... Game: {lobby.gameName}</CardDescription>
                 </CardHeader>
                 <CardContent className="space-y-4">
                    <div className="space-y-3">
                        <div className="flex items-center justify-between bg-gray-800 p-3 rounded">
                             <span>Host: {lobby.hostUsername}</span>
                             {lobby.hostReady ? <CheckCircle2 className="text-green-500" /> : <Hourglass className="text-yellow-500" />}
                         </div>
                          <div className="flex items-center justify-between bg-gray-800 p-3 rounded">
                             <span>Opponent: {lobby.opponentUsername ?? 'Waiting...'}</span>
                              {lobby.opponentUsername ? (lobby.opponentReady ? <CheckCircle2 className="text-green-500" /> : <Hourglass className="text-yellow-500" />) : <XCircle className="text-red-600"/>}
                         </div>
                    </div>
                    <p className="text-center text-gray-400 italic">
                       {lobby.status === 'WAITING_FOR_OPPONENT' && 'Waiting for an opponent to join...'}
                       {lobby.status === 'WAITING_FOR_READY' && 'Waiting for players to be ready...'}
                       {lobby.status === 'READY_TO_START' && 'Ready to start! Host can begin the game.'}
                       {lobby.status === 'IN_GAME' && 'Game is in progress...'}
                       {lobby.status === 'FINISHED' && 'Game has finished.'}
                    </p>
                    <div className="flex flex-col sm:flex-row gap-4 justify-center pt-4">
                         {(isHost || isOpponent) && !viewerIsReady && lobby.status !== 'IN_GAME' && (
                            <Button onClick={() => readyMutation.mutate()} disabled={readyMutation.isPending} className="flex-1">
                                {readyMutation.isPending ? <Loader2 className="animate-spin mr-2"/> : null} Ready Up
                            </Button>
                         )}
                         {(isHost || isOpponent) && viewerIsReady && lobby.status !== 'IN_GAME' && (
                            <Button onClick={() => unreadyMutation.mutate()} disabled={unreadyMutation.isPending} variant="secondary" className="flex-1">
                                {unreadyMutation.isPending ? <Loader2 className="animate-spin mr-2"/> : null} Unready
                            </Button>
                         )}

                          {isHost && (
                             <Button onClick={() => startMutation.mutate()} disabled={!canStart || startMutation.isPending} className="flex-1 bg-green-600 hover:bg-green-700">
                                 {startMutation.isPending ? <Loader2 className="animate-spin mr-2"/> : <Play className="mr-2 h-4 w-4"/>} Start Game
                             </Button>
                          )}

                          <Button onClick={() => leaveMutation.mutate()} disabled={leaveMutation.isPending} variant="destructive" className="flex-1">
                             {leaveMutation.isPending ? <Loader2 className="animate-spin mr-2"/> : <LogOut className="mr-2 h-4 w-4"/>} Leave Lobby
                          </Button>
                    </div>

                 </CardContent>
            </Card>
        </div>
    );
};
export default LobbyWait;