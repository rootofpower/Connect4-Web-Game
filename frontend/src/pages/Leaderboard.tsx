import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { getLeaderboard } from "@/services/playerStatsService"; // Потрібно створити цей сервіс та функцію
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { TrophyIcon, Loader2 } from 'lucide-react';

interface LeaderboardEntry {
  rank?: number;
  username: string;
  wins: number;
  losses: number;
  draws: number;
  mmr: number;
}

const Leaderboard = () => {
  const game = 'connect4'; 
  const sortBy = 'mmr';

  const { data: leaderboardData, isLoading, error } = useQuery<LeaderboardEntry[]>({
      queryKey: ['leaderboard', game, sortBy],
      queryFn: () => getLeaderboard(game, sortBy, 10),
  });

  if (isLoading) return <div className="flex justify-center items-center min-h-screen"><Loader2 className="h-12 w-12 animate-spin text-white"/></div>;
  if (error) return <div className="text-red-500 text-center mt-10">Error loading leaderboard: {(error as Error).message}</div>;

  return (
    <div className="min-h-[calc(100vh-5rem)] p-4">
      <div className="connect4-container">
         <div className="flex flex-col md:flex-row justify-between items-center mb-8">
           <h1 className="text-3xl font-bold text-white flex items-center"><TrophyIcon className="mr-2 text-connect4-yellow" />Leaderboard</h1>
         </div>
         <Card className="bg-connect4-blue border-gray-700 overflow-hidden">
           <CardHeader>
             <CardTitle className="text-2xl font-bold text-white">Top Players ({sortBy.toUpperCase()})</CardTitle>
           </CardHeader>
           <CardContent className="pt-6">
            <div className="overflow-x-auto">
              <table className="w-full text-left">
                <thead>
                  <tr className="border-b border-gray-700">
                    <th className="pb-4 pr-4 text-gray-400 font-medium">Rank</th>
                    <th className="pb-4 pr-4 text-gray-400 font-medium">Player</th>
                    <th className="pb-4 pr-4 text-gray-400 font-medium text-center">MMR</th>
                    <th className="pb-4 pr-4 text-gray-400 font-medium text-center">Wins</th>
                    <th className="pb-4 pr-4 text-gray-400 font-medium text-center">Losses</th>
                    <th className="pb-4 pr-4 text-gray-400 font-medium text-center">Draws</th>
                  </tr>
                </thead>
                 <tbody>
                  {leaderboardData && leaderboardData.length > 0 ? (
                    leaderboardData.map((player, index) => (
                      <tr key={player.username} className="border-b border-gray-700 hover:bg-gray-800/50">
                        <td className="py-4 pr-4"><span className="font-medium text-white">{index + 1}</span></td>
                        <td className="py-4 pr-4"><span className="font-medium text-white">{player.username}</span></td>
                        <td className="py-4 pr-4 text-center text-blue-400 font-semibold">{player.mmr}</td>
                        <td className="py-4 pr-4 text-center text-green-400">{player.wins}</td>
                        <td className="py-4 pr-4 text-center text-red-400">{player.losses}</td>
                        <td className="py-4 pr-4 text-center text-gray-400">{player.draws}</td>
                      </tr>
                    ))
                   ) : (
                     <tr><td colSpan={6} className="text-center py-4 text-gray-500">Leaderboard is empty.</td></tr>
                   )}
                 </tbody>
              </table>
            </div>
           </CardContent>
         </Card>
      </div>
    </div>
  );
};

export default Leaderboard;