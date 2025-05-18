
import React from 'react';
import { Link } from 'react-router-dom';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Users, User } from 'lucide-react';

const GameMode = () => {
  return (
    <div className="min-h-[calc(100vh-5rem)] flex items-center justify-center p-4">
      <div className="w-full max-w-4xl">
        <h1 className="text-3xl font-bold text-white text-center mb-8">Choose Game Mode</h1>
        
        <div className="grid md:grid-cols-2 gap-8">
          <Card className="bg-connect4-blue border-gray-700 transition-transform hover:scale-105">
            <CardHeader>
              <div className="mx-auto bg-gray-800 p-6 rounded-full w-24 h-24 flex items-center justify-center mb-4">
                <User className="h-12 w-12 text-connect4-yellow" />
              </div>
              <CardTitle className="text-2xl font-bold text-white text-center">Local Play</CardTitle>
              <CardDescription className="text-gray-400 text-center">
                Play against a friend on the same device
              </CardDescription>
            </CardHeader>
            <CardContent className="text-center text-gray-300">
              <p>Perfect for one-on-one challenges with someone sitting right next to you. Take turns dropping pieces on the same device.</p>
            </CardContent>
            <CardFooter className="flex justify-center">
              <Link to="/game/local">
                <Button size="lg" className="bg-connect4-yellow text-connect4-dark hover:bg-yellow-500">
                  Start Local Game
                </Button>
              </Link>
            </CardFooter>
          </Card>
          
          {/* Multiplayer Card */}
          <Card className="bg-connect4-blue border-gray-700 transition-transform hover:scale-105">
            <CardHeader>
              <div className="mx-auto bg-gray-800 p-6 rounded-full w-24 h-24 flex items-center justify-center mb-4">
                <Users className="h-12 w-12 text-connect4-yellow" />
              </div>
              <CardTitle className="text-2xl font-bold text-white text-center">Multiplayer</CardTitle>
              <CardDescription className="text-gray-400 text-center">
                Challenge players online
              </CardDescription>
            </CardHeader>
            <CardContent className="text-center text-gray-300">
              <p>Create or join a lobby to play against other Connect Four enthusiasts online. Test your skills and strategy globally.</p>
            </CardContent>
            <CardFooter className="flex justify-center">
              <Link to="/game/lobby">
                <Button size="lg" className="bg-blue-600 hover:bg-blue-700 text-white">
                  Multiplayer
                </Button>
              </Link>
            </CardFooter>
          </Card>
        </div>
        
        <div className="mt-8 text-center">
          <Link to="/">
            <Button variant="ghost" className="text-gray-400 hover:text-white">
              Back to Home
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
};

export default GameMode;
