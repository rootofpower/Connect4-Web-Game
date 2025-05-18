import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Menu, X } from 'lucide-react';
import { Button } from './ui/button';
import { useAuth } from '@/context/AuthContext'; // Імпортуємо

const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { isAuthenticated, user, logout } = useAuth(); // Використовуємо
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
    setIsOpen(false);
  };

  const handleLinkClick = () => setIsOpen(false);


  return (
    <nav className="bg-connect4-blue">
      <div className="connect4-container mx-auto px-2 sm:px-6 lg:px-8">
        <div className="relative flex items-center justify-between h-16">
          <div className="absolute inset-y-0 left-0 flex items-center sm:hidden">
            <button type="button" className="inline-flex items-center justify-center p-2 rounded-md text-gray-400 hover:text-white hover:bg-gray-700 focus:outline-none" aria-controls="mobile-menu" aria-expanded={isOpen} onClick={() => setIsOpen(!isOpen)}>
              <span className="sr-only">Open main menu</span>
              {isOpen ? <X className="block h-6 w-6" /> : <Menu className="block h-6 w-6" />}
            </button>
          </div>
          <div className="flex-1 flex items-center justify-center sm:items-stretch sm:justify-start">
            <div className="flex-shrink-0 flex items-center">
              <Link to="/" className="flex items-center" onClick={handleLinkClick}>
                <span className="text-connect4-yellow text-2xl font-extrabold tracking-tight">Connect</span><span className="text-white text-2xl font-extrabold tracking-tight">4</span>
              </Link>
            </div>
            <div className="hidden sm:block sm:ml-6">
              <div className="flex space-x-4">
                <Link to="/" className="nav-link">Home</Link>
                <Link to="/leaderboard" className="nav-link">Leaderboard</Link>
                <Link to="/ratings" className="nav-link">Ratings</Link>
                <Link to="/comments" className="nav-link">Comments</Link>
                {isAuthenticated && <Link to="/game/lobby" className="nav-link">Multiplayer</Link>} {/* Захищено в App.tsx */}
              </div>
            </div>
          </div>
          <div className="absolute inset-y-0 right-0 flex items-center pr-2 sm:static sm:inset-auto sm:ml-6 sm:pr-0">
            <div className="hidden sm:flex items-center space-x-2">
              {isAuthenticated ? (
                <>
                  <span className="text-gray-300 text-sm mr-3">Hi, {user?.username}!</span>
                  <Button onClick={handleLogout} variant="destructive" size="sm">Log out</Button>
                </>
              ) : (
                <>
                  <Link to="/login"><Button variant="outline" size="sm" className="text-white border-gray-600 hover:bg-gray-700">Login</Button></Link>
                  <Link to="/register"><Button size="sm" className="bg-connect4-yellow text-connect4-dark hover:bg-yellow-500">Register</Button></Link>
                </>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className={`${isOpen ? 'block' : 'hidden'} sm:hidden`} id="mobile-menu">
        <div className="px-2 pt-2 pb-3 space-y-1">
          <Link to="/" className="nav-link-mobile block" onClick={handleLinkClick}>Home</Link>
          <Link to="/leaderboard" className="nav-link-mobile block" onClick={handleLinkClick}>Leaderboard</Link>
          <Link to="/ratings" className="nav-link-mobile block" onClick={handleLinkClick}>Ratings</Link>
          <Link to="/comments" className="nav-link-mobile block" onClick={handleLinkClick}>Comments</Link>
          {isAuthenticated ? (
            <>
              <Link to="/game/lobby" className="nav-link-mobile block" onClick={handleLinkClick}>Multiplayer</Link>
              <Button onClick={handleLogout} variant="destructive" className="w-full text-left mt-1 px-3 py-2">Log out ({user?.username})</Button>
            </>
          ) : (
            <>
              <Link to="/login" className="nav-link-mobile block" onClick={handleLinkClick}>Login</Link>
              <Link to="/register" className="nav-link-mobile block" onClick={handleLinkClick}>Register</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
};
export default Navbar;