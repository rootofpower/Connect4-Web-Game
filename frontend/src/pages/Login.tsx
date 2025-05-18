import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Label } from '@/components/ui/label';
import { useToast } from '@/components/ui/use-toast';
import api from '@/services/api';
import axios from 'axios';
import { useAuth } from '@/context/AuthContext'; 

const Login = () => {
  const [identifier, setIdentifier] = useState('');
  const [password, setPassword] = useState('');
  const { toast } = useToast();
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth(); 
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const response = await api.post('/auth/login', { identifier, password });
      const { token, user } = response.data;

      if (!token || !user) {
          throw new Error("Invalid response structure from login API.");
      }

      login(user, token); 

      toast({ title: 'Login Successful', description: 'Welcome back!' });
      navigate('/');

    } catch (error: unknown) {
      let title = "Login Failed";
      let description = "An unknown error occurred.";
      if (axios.isAxiosError(error) && error.response) {
        const status = error.response.status;
        const data = typeof error.response.data === 'string' ? error.response.data : '';
        if (status === 401){
          description = "Invalid credentials. Please check your email/username and password.";
        } else if (status === 500 && data.toLowerCase().includes("error during login")) {
          description = "Internal server error. Please try again later.";
        } else if(data){
          description = data;
        } else {
          description = "An unexpected error occurred. Status code: " + status;
        }
      } else if (error instanceof Error) {
        description = error.message;
      }
      toast({ variant: 'destructive', title: 'Login Failed', description: description });
    }
  };

  if (isAuthenticated) return null;

  return (
    <div className="min-h-[calc(100vh-5rem)] flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
     <Card className="w-full max-w-md bg-connect4-blue border-gray-700">
       <CardHeader className="space-y-1">
         <CardTitle className="text-2xl font-bold text-white text-center">Login</CardTitle>
         <CardDescription className="text-gray-400 text-center">
           Enter your credentials to access your account
         </CardDescription>
       </CardHeader>
       <CardContent>
         <form onSubmit={handleSubmit} className="space-y-4">
             <div className="space-y-2">
             <Label htmlFor="identifier" className="text-white">Email or Username</Label>
             <Input id="identifier" type="text" placeholder="name@example.com or coolplayer123" value={identifier} onChange={(e) => setIdentifier(e.target.value)} required className="bg-gray-800 border-gray-700 text-white"/>
             </div>
             <div className="space-y-2">
             <Label htmlFor="password" className="text-white">Password</Label>
             <Input id="password" type="password" placeholder="••••••••" value={password} onChange={(e) => setPassword(e.target.value)} required className="bg-gray-800 border-gray-700 text-white"/>
             </div>
           <Button type="submit" className="w-full bg-connect4-yellow text-connect4-dark hover:bg-yellow-500">Sign in</Button>
         </form>
       </CardContent>
       <CardFooter className="flex justify-center">
         <p className="text-gray-400 text-center text-sm">
           Don't have an account?{" "}
           <Link to="/register" className="text-blue-400 hover:underline">Sign up</Link>
         </p>
       </CardFooter>
     </Card>
   </div>
  );
};
export default Login;