import api from './api';

export interface LobbyDTO {
  lobbyCode: string;
  hostUsername: string;
  opponentUsername: string | null;
  hostReady: boolean;
  opponentReady: boolean;
  status: string;
  gameName: string;
    gameSessionId: number | null; 
}

export interface StartGameResponse {
  message: string;
  gameSessionId: number | null; 
}

export interface CreateLobbyRequest {
    gameName: string;
}

export const createLobby = async (gameName: string = 'connect4'): Promise<LobbyDTO> => {
    const requestData: CreateLobbyRequest = { gameName };
    const response = await api.post<LobbyDTO>('/lobby/create', requestData);
    return response.data; 
};

export const joinLobby = async (lobbyCode: string): Promise<LobbyDTO> => {
    const response = await api.post<LobbyDTO>(`/lobby/join/${lobbyCode}`);
    return response.data;
};

export const getLobbyState = async (lobbyCode: string): Promise<LobbyDTO> => {
    const response = await api.get<LobbyDTO>(`/lobby/${lobbyCode}`);
    return response.data;
};

export const setReady = async (lobbyCode: string): Promise<LobbyDTO> => {
    const response = await api.post<LobbyDTO>(`/lobby/${lobbyCode}/ready`);
    return response.data;
};

export const setUnready = async (lobbyCode: string): Promise<LobbyDTO> => {
    const response = await api.post<LobbyDTO>(`/lobby/${lobbyCode}/unready`);
    return response.data;
};

export const startGame = async (lobbyCode: string): Promise<StartGameResponse> => {
    const response = await api.post<StartGameResponse>(`/lobby/${lobbyCode}/start`);
    return response.data;
};

export const leaveLobby = async (lobbyCode: string): Promise<LobbyDTO | null> => {
    try {
         const response = await api.post<LobbyDTO>(`/lobby/${lobbyCode}/leave`);
         if (response.status === 204) {
             return null;
         }
         return response.data; 
    } catch (error) {
         console.error("API call error in leaveLobby:", error);
         throw error;
    }
};
