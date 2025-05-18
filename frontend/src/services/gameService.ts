import api from './api';

interface GameSessionDTO {
  id: number;
  gameName: string;
  player1Username: string;
  player2Username: string;
  boardState: string; 
  currentPlayerUsername: string;
  status: string; 
}

interface MakeMoveRequest {
  column: number;
}


export const getGameState = async (gameId: number): Promise<GameSessionDTO> => {
    const response = await api.get<GameSessionDTO>(`/game/${gameId}/state`);
    return response.data;
};


export const makeMove = async (gameId: number, moveRequest: MakeMoveRequest): Promise<GameSessionDTO> => {
    console.log("Making move:", moveRequest);
    const response = await api.post<GameSessionDTO>(`/game/${gameId}/move`, moveRequest);
    console.log("Move response:", response.data);
    return response.data;
};