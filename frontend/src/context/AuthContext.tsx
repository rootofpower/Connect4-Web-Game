import React, { createContext, useContext, useState, useEffect, ReactNode, useMemo, useCallback } from 'react';
import api from '@/services/api'; 

interface User {
  id: number; 
  username: string;
  email?: string;
  role?: string; 
}

type AuthContextType = {
  user: User | null;          
  token: string | null;      
  isAuthenticated: boolean;   
  isLoading: boolean;         
  login: (userData: User, token: string) => void; 
  logout: () => void;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem('authToken');
  });
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(!!token);
  const [isLoading, setIsLoading] = useState<boolean>(true); 

  useEffect(() => {
    const initializeAuth = async () => {
      if (token) {
        console.log("Auth: Found token in storage, validating...");
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        try {
          const response = await api.get('/auth/profile'); 
          if (response.data) {
            setUser(response.data as User); 
            setIsAuthenticated(true);
            console.log("Auth: Token validated, user set:", response.data);
          } else {
            await handleLogout(); 
          }
        } catch (error) {
          console.error("Auth: Token validation failed.", error);
          await handleLogout(); 
        }
      } else {
        setIsAuthenticated(false);
        setUser(null);
         console.log("Auth: No token found.");
      }
      setIsLoading(false);
    };

    initializeAuth();
  }, []); 

  const handleLogin = useCallback((userData: User, receivedToken: string) => {
    console.log("Auth: Logging in user:", userData.username);
    localStorage.setItem('authToken', receivedToken); 
    api.defaults.headers.common['Authorization'] = `Bearer ${receivedToken}`;
    setUser(userData);
    setToken(receivedToken); 
    setIsAuthenticated(true);
    setIsLoading(false); 
  }, []);

  const handleLogout = useCallback(() => {
    console.log("Auth: Logging out.");
    localStorage.removeItem('authToken');
    delete api.defaults.headers.common['Authorization'];
    setUser(null);
    setToken(null); 
    setIsAuthenticated(false);
  }, []);

  const contextValue = useMemo(() => ({
    user,
    token, 
    isAuthenticated,
    isLoading,
    login: handleLogin,
    logout: handleLogout,
  }), [user, token, isAuthenticated, isLoading, handleLogin, handleLogout]);

  return (
    <AuthContext.Provider value={contextValue}>
      {isLoading ? <div>Loading Authentication...</div> : children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};