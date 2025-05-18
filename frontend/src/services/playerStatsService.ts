import api from './api';

interface PlayerStatsDTO {
  id?: number;
  game: string;
  username: string;
  wins: number;
  losses: number;
  draws: number;
  mmr: number;
  playedAt?: string | Date | number; 
  winRate?: number;
  totalGames?: number;
}


export const getLeaderboard = async (
    game: string = 'connect4',
    sortBy: string = 'mmr', 
    limit: number = 10      
): Promise<PlayerStatsDTO[]> => {
    const response = await api.get<PlayerStatsDTO[]>(`/leaderboard`, {
        params: { game, sortBy, limit } 
    });
    return response.data;
};


export const getPlayerStats = async (game: string, username: string): Promise<PlayerStatsDTO> => {
    const response = await api.get<PlayerStatsDTO>(`/stats/${game}/player/${username}`);
    return response.data;
};

/*
export const deleteMyStats = async (game: string): Promise<void> => {
    await api.delete(`/stats/${game}/my-stats`); 
};

export const resetGameStatsForAdmin = async (game: string): Promise<void> => {
    await api.delete(`/stats/${game}`);
};
*/
