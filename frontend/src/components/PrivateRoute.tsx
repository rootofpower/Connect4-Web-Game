import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from "../context/AuthContext"; // Перевір шлях

const PrivateRoute = () => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return <div>Checking authentication...</div>; // Або спіннер
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
};

export default PrivateRoute;