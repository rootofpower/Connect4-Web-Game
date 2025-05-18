import React from 'react';
import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Index from "./pages/Index";
import Login from "./pages/Login";
import Register from "./pages/Register";
import LocalGame from "./pages/LocalGame";
import GameLobby from "./pages/GameLobby"; 
import Leaderboard from "./pages/Leaderboard";
import Ratings from "./pages/Ratings";
import Comments from "./pages/Comments";
import NotFound from "./pages/NotFound";
import PrivateRoute from "./components/PrivateRoute";
import { AuthProvider } from "./context/AuthContext";
import LobbyWait from './pages/LobbyWait';
import GameScreen from './pages/GameScreen';
import GameMode from './pages/GameMode'; 

const queryClient = new QueryClient();

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <BrowserRouter>
        <AuthProvider>
          <Toaster />
          <Sonner />
          <div className="flex flex-col min-h-screen">
            <Navbar />
            <main className="flex-1">
              <Routes>
                <Route path="/" element={<Index />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/game/local" element={<LocalGame />} />
                <Route path="/leaderboard" element={<Leaderboard />} />
                <Route path="/ratings" element={<Ratings />} />
                <Route path="/comments" element={<Comments />} />
                <Route path="/game/mode" element={<GameMode />} />

                <Route element={<PrivateRoute />}>
                  <Route path="/game/lobby" element={<GameLobby />} />
                  <Route path="/lobby/wait/:lobbyCode" element={<LobbyWait />} />
                  <Route path="/game/:gameId" element={<GameScreen />} />
                </Route>

                <Route path="*" element={<NotFound />} />
              </Routes>
            </main>
            <Footer />
          </div>
        </AuthProvider>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
