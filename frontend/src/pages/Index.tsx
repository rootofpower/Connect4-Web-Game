
import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { motion } from 'framer-motion';
import { GamepadIcon, TrophyIcon, StarIcon, MessageSquareIcon } from 'lucide-react';

const Index = () => {
  return (
    <div className="flex flex-col min-h-screen">
      <div className="flex-grow">
        {/* Hero Section */}
        <section className="bg-connect4-blue py-20">
          <div className="connect4-container">
            <div className="flex flex-col md:flex-row items-center">
              <div className="md:w-1/2 mb-10 md:mb-0">
                <h1 className="text-4xl md:text-5xl font-extrabold text-white mb-4">
                  Master the Classic <span className="text-connect4-yellow">Connect Four</span> Game
                </h1>
                <p className="text-gray-300 text-lg mb-8">
                  Challenge friends, compete globally, and climb the leaderboard in this strategic battle of wits.
                </p>
                <div className="flex space-x-4">
                  <Link to="/game/mode">
                    <Button className="bg-connect4-yellow text-connect4-dark hover:bg-yellow-500 font-bold text-lg px-8 py-6">
                      Start Game
                    </Button>
                  </Link>
                  <Link to="/leaderboard">
                    <Button variant="outline" className="border-gray-600 text-white hover:bg-gray-700 font-bold text-lg px-8 py-6">
                      Leaderboard
                    </Button>
                  </Link>
                </div>
              </div>
              <div className="md:w-1/2 flex justify-center">
                <div className="w-72 h-64 bg-connect4-dark rounded-lg grid grid-cols-7 grid-rows-6 gap-2 p-4 shadow-lg border-2 border-gray-700">
                  {Array(42).fill(null).map((_, index) => {
                    let color = "bg-gray-700";
                    if ([10, 16, 22, 28, 12, 18, 24, 30].includes(index)) {
                      color = "bg-connect4-yellow";
                    } else if ([11, 17, 23, 29, 13, 19, 25, 31].includes(index)) {
                      color = "bg-blue-500";
                    }
                    return (
                      <div key={index} className={`${color} rounded-full`}></div>
                    );
                  })}
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Features Section */}
        <section className="py-16">
          <div className="connect4-container">
            <h2 className="text-3xl font-bold text-center text-white mb-12">Game Features</h2>
            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
              <div className="card-game flex flex-col items-center text-center">
                <div className="bg-connect4-yellow p-3 rounded-full mb-4">
                  <GamepadIcon className="h-8 w-8 text-connect4-dark" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Multiple Game Modes</h3>
                <p className="text-gray-300">Play locally with friends or compete online against global players.</p>
              </div>
              <div className="card-game flex flex-col items-center text-center">
                <div className="bg-connect4-yellow p-3 rounded-full mb-4">
                  <TrophyIcon className="h-8 w-8 text-connect4-dark" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Global Leaderboards</h3>
                <p className="text-gray-300">Climb the ranks and become the ultimate Connect Four champion.</p>
              </div>
              <div className="card-game flex flex-col items-center text-center">
                <div className="bg-connect4-yellow p-3 rounded-full mb-4">
                  <StarIcon className="h-8 w-8 text-connect4-dark" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Game Ratings</h3>
                <p className="text-gray-300">Rate games and see what others think about their Connect Four experience.</p>
              </div>
              <div className="card-game flex flex-col items-center text-center">
                <div className="bg-connect4-yellow p-3 rounded-full mb-4">
                  <MessageSquareIcon className="h-8 w-8 text-connect4-dark" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Community Comments</h3>
                <p className="text-gray-300">Engage with the community, share strategies, and make friends.</p>
              </div>
            </div>
          </div>
        </section>

        {/* Call to Action */}
        <section className="bg-connect4-blue py-16">
          <div className="connect4-container text-center">
            <h2 className="text-3xl font-bold text-white mb-4">Ready to Play?</h2>
            <p className="text-gray-300 text-lg mb-8 max-w-2xl mx-auto">
              Create an account to track your progress, save your stats, and compete with players around the world.
            </p>
            <div className="flex flex-col sm:flex-row justify-center gap-4">
              <Link to="/register">
                <Button className="bg-connect4-yellow text-connect4-dark hover:bg-yellow-500 font-bold px-8 py-3">
                  Register Now
                </Button>
              </Link>
              <Link to="/login">
                <Button variant="outline" className="border-gray-600 text-white hover:bg-gray-700 font-bold px-8 py-3">
                  Login
                </Button>
              </Link>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default Index;
