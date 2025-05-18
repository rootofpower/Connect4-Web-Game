
import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-connect4-blue py-6 mt-auto">
      <div className="connect4-container">
        <div className="flex flex-col md:flex-row justify-between items-center">
          <div className="mb-4 md:mb-0">
            <Link to="/" className="flex items-center">
              <span className="text-connect4-yellow text-xl font-bold">Connect</span>
              <span className="text-white text-xl font-bold">4</span>
            </Link>
            <p className="text-gray-400 text-sm mt-2">© 2025 Connect4. All rights reserved.</p>
          </div>
          <div className="flex flex-col md:flex-row space-y-2 md:space-y-0 md:space-x-6">
            <Link to="/privacy" className="text-gray-300 hover:text-white text-sm">Privacy Policy</Link>
            <Link to="/terms" className="text-gray-300 hover:text-white text-sm">Terms of Service</Link>
            <Link to="/contact" className="text-gray-300 hover:text-white text-sm">Contact Us</Link>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
