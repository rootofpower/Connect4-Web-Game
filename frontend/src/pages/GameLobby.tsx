import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation } from '@tanstack/react-query';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { useToast } from '@/components/ui/use-toast';
import * as lobbyService from "@/services/lobbyService"; 

const GameLobby = () => {
    const [joinCode, setJoinCode] = useState('');
    const navigate = useNavigate();
    const { toast } = useToast();

    const createLobbyMutation = useMutation({
        mutationFn: () => lobbyService.createLobby('connect4'),
        onSuccess: (data) => {
            toast({ title: "Lobby Created!", description: `Code: ${data.lobbyCode}` });
            navigate(`/lobby/wait/${data.lobbyCode}`);
        },
        onError: (error: Error) => {
            toast({ variant: "destructive", title: "Failed to create lobby", description: error.message });
        },
    });

    const joinLobbyMutation = useMutation({
        mutationFn: (code: string) => lobbyService.joinLobby(code),
        onSuccess: (data) => {
            toast({ title: "Joined Lobby!", description: `Code: ${data.lobbyCode}` });
            navigate(`/lobby/wait/${data.lobbyCode}`);
        },
        onError: (error: any) => { 
            const errorMsg = error.response?.data?.error || error.message || "Unknown error joining lobby";
            toast({ variant: "destructive", title: "Failed to join lobby", description: errorMsg });
        },
    });

    const handleJoinLobby = () => {
        const codeToJoin = joinCode.trim().toUpperCase();
        if (codeToJoin.length === 6) { 
            joinLobbyMutation.mutate(codeToJoin);
        } else {
            toast({ variant: "destructive", title: "Invalid Code", description: "Lobby code must be 6 characters long." });
        }
    };

    return (
      <div className="container mx-auto p-4 flex justify-center items-center min-h-[calc(100vh-8rem)]">
          <Card className="w-full max-w-md bg-connect4-blue border-gray-700">
              <CardHeader>
                  <CardTitle className="text-center text-white">Multiplayer Lobby</CardTitle>
                  <CardDescription className="text-center text-gray-400">Create or join a game lobby.</CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                  <div className="text-center">
                      <Button
                          onClick={() => createLobbyMutation.mutate()}
                          disabled={createLobbyMutation.isPending}
                          className="w-full bg-connect4-yellow text-connect4-dark hover:bg-yellow-500"
                          size="lg"
                      >
                          {createLobbyMutation.isPending ? 'Creating...' : 'Create New Lobby'}
                      </Button>
                  </div>
  
                  <div className="text-center text-sm text-gray-500">OR</div>
  
                  <div className="space-y-2">
                       <label htmlFor="lobbyCodeInput" className="text-white text-center block mb-1">Join Existing Lobby</label>
                      <Input
                          id="lobbyCodeInput"
                          type="text"
                          placeholder="Enter 6-character Lobby Code"
                          value={joinCode}
                          onChange={(e) => setJoinCode(e.target.value)}
                          maxLength={6}
                          className="text-center bg-gray-800 border-gray-700 text-white"
                          // --- ЗМІНЕНО isLoading -> isPending ---
                          disabled={joinLobbyMutation.isPending}
                          onKeyDown={(e) => e.key === 'Enter' && handleJoinLobby()}
                      />
                      <Button
                          onClick={handleJoinLobby}
                          disabled={joinLobbyMutation.isPending || !joinCode.trim() || joinCode.trim().length !== 6}
                          className="w-full"
                          variant="secondary"
                      >
                          {joinLobbyMutation.isPending ? 'Joining...' : 'Join Lobby'}
                      </Button>
                  </div>
              </CardContent>
          </Card>
      </div>
  );
};

export default GameLobby;